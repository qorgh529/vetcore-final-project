package org.springframework.samples.petclinic.reservation;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.samples.petclinic.account.AppUser;
import org.springframework.samples.petclinic.account.AppUserRepository;
import org.springframework.samples.petclinic.reservation.ReservationException.Reason;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Booking rules. Seats are counted with single conditional UPDATE statements, so a
 * schedule can never hold more reservations than its capacity, however many requests
 * arrive at once and however many application instances are running.
 */
@Service
public class ReservationService {

	private final ReservationRepository reservations;

	private final TreatmentScheduleRepository schedules;

	private final AppUserRepository users;

	private final Clock clock;

	public ReservationService(ReservationRepository reservations, TreatmentScheduleRepository schedules,
			AppUserRepository users, Clock clock) {
		this.reservations = reservations;
		this.schedules = schedules;
		this.users = users;
		this.clock = clock;
	}

	@Transactional(readOnly = true)
	public List<Reservation> findMine(String username) {
		return this.reservations.findByUserUsernameOrderByCreatedAtDesc(username);
	}

	@Transactional(readOnly = true)
	public Reservation findMine(String username, Integer reservationId) {
		return this.reservations.findByIdAndUserUsername(reservationId, username)
			.orElseThrow(() -> new ReservationException(Reason.NOT_FOUND));
	}

	@Transactional
	public Integer book(String username, Integer scheduleId, String petName, String memo) {
		LocalDateTime now = LocalDateTime.now(this.clock);
		TreatmentSchedule schedule = bookableSchedule(scheduleId, now);
		AppUser user = this.users.findByUsername(username)
			.orElseThrow(() -> new ReservationException(Reason.NOT_FOUND));

		Reservation existing = this.reservations.findByScheduleIdAndUserId(scheduleId, user.getId()).orElse(null);
		if (existing != null && existing.isBooked()) {
			throw new ReservationException(Reason.DUPLICATE);
		}
		if (this.schedules.tryReserveSeat(scheduleId) == 0) {
			throw new ReservationException(Reason.FULL);
		}
		if (existing != null) {
			if (this.reservations.markRebooked(existing.getId(), petName, memo, now) == 0) {
				throw new ReservationException(Reason.DUPLICATE);
			}
			return existing.getId();
		}
		Reservation reservation = new Reservation();
		reservation.setSchedule(schedule);
		reservation.setUser(user);
		reservation.setPetName(petName);
		reservation.setMemo(memo);
		reservation.setCreatedAt(now);
		reservation.setUpdatedAt(now);
		try {
			return this.reservations.saveAndFlush(reservation).getId();
		}
		catch (DataIntegrityViolationException ex) {
			// The same user sent two requests at once; the unique key let only one in.
			// Throwing rolls this transaction back, which also returns the seat.
			throw new ReservationException(Reason.DUPLICATE);
		}
	}

	@Transactional
	public void cancel(String username, Integer reservationId) {
		LocalDateTime now = LocalDateTime.now(this.clock);
		Reservation reservation = findMine(username, reservationId);
		if (!reservation.isBooked()) {
			throw new ReservationException(Reason.NOT_FOUND);
		}
		if (!reservation.getSchedule().isChangeableAt(now)) {
			throw new ReservationException(Reason.ENDED);
		}
		if (this.reservations.markCancelled(reservationId, now) == 0) {
			throw new ReservationException(Reason.NOT_FOUND);
		}
		this.schedules.releaseSeat(reservation.getSchedule().getId());
	}

	/**
	 * Changes the pet name and memo, and moves the reservation to another schedule when
	 * {@code newScheduleId} differs from the current one.
	 */
	@Transactional
	public void change(String username, Integer reservationId, Integer newScheduleId, String petName, String memo) {
		LocalDateTime now = LocalDateTime.now(this.clock);
		Reservation reservation = findMine(username, reservationId);
		if (!reservation.isBooked()) {
			throw new ReservationException(Reason.NOT_FOUND);
		}
		Integer oldScheduleId = reservation.getSchedule().getId();
		if (!reservation.getSchedule().isChangeableAt(now)) {
			throw new ReservationException(Reason.ENDED);
		}
		if (oldScheduleId.equals(newScheduleId)) {
			if (this.reservations.updateDetails(reservationId, petName, memo, now) == 0) {
				throw new ReservationException(Reason.NOT_FOUND);
			}
			return;
		}

		TreatmentSchedule target = bookableSchedule(newScheduleId, now);
		Integer userId = reservation.getUser().getId();
		Reservation onTarget = this.reservations.findByScheduleIdAndUserId(newScheduleId, userId).orElse(null);
		if (onTarget != null) {
			if (onTarget.isBooked()) {
				throw new ReservationException(Reason.DUPLICATE);
			}
			// An old cancelled row would collide with the unique key
			this.reservations.delete(onTarget);
			this.reservations.flush();
		}
		if (this.reservations.moveToSchedule(reservationId, oldScheduleId, target, petName, memo, now) == 0) {
			throw new ReservationException(Reason.NOT_FOUND);
		}
		// Always touch the two schedule rows in id order. Two users swapping in opposite
		// directions would otherwise wait on each other's row lock.
		boolean reserved;
		if (oldScheduleId < newScheduleId) {
			this.schedules.releaseSeat(oldScheduleId);
			reserved = this.schedules.tryReserveSeat(newScheduleId) == 1;
		}
		else {
			reserved = this.schedules.tryReserveSeat(newScheduleId) == 1;
			this.schedules.releaseSeat(oldScheduleId);
		}
		if (!reserved) {
			throw new ReservationException(Reason.FULL);
		}
	}

	private TreatmentSchedule bookableSchedule(Integer scheduleId, LocalDateTime now) {
		TreatmentSchedule schedule = this.schedules.findById(scheduleId)
			.orElseThrow(() -> new ReservationException(Reason.NOT_FOUND));
		if (!schedule.isChangeableAt(now)) {
			throw new ReservationException(Reason.ENDED);
		}
		if (!schedule.isOpenAt(now)) {
			throw new ReservationException(Reason.NOT_OPEN);
		}
		return schedule;
	}

}
