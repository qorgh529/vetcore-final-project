package org.springframework.samples.petclinic.reservation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.samples.petclinic.account.AppUser;
import org.springframework.samples.petclinic.account.AppUserRepository;
import org.springframework.samples.petclinic.reservation.ReservationException.Reason;

/**
 * Booking rules against a real (H2) database, including many requests at once.
 */
@SpringBootTest
class ReservationServiceTests {

	private static final AtomicInteger SEQUENCE = new AtomicInteger();

	@Autowired
	ReservationService service;

	@Autowired
	ReservationRepository reservations;

	@Autowired
	TreatmentScheduleRepository schedules;

	@Autowired
	PartnerHospitalRepository hospitals;

	@Autowired
	AppUserRepository users;

	@Autowired
	Clock clock;

	private LocalDateTime now() {
		return LocalDateTime.now(this.clock);
	}

	private String newUser() {
		AppUser user = new AppUser();
		user.setUsername("tester_" + SEQUENCE.incrementAndGet());
		user.setPassword("x");
		user.setDisplayName("Tester");
		user.setCreatedAt(now());
		return this.users.save(user).getUsername();
	}

	private Integer newSchedule(int capacity, LocalDateTime opensAt, int daysUntilTreatment) {
		TreatmentSchedule schedule = new TreatmentSchedule();
		schedule.setHospital(this.hospitals.findById(1).orElseThrow());
		schedule.setTreatmentName("test treatment");
		schedule.setTreatmentDate(now().toLocalDate().plusDays(daysUntilTreatment));
		schedule.setStartTime(LocalTime.of(10, 0));
		schedule.setCapacity(capacity);
		schedule.setBookingOpensAt(opensAt);
		return this.schedules.save(schedule).getId();
	}

	private Integer openSchedule(int capacity) {
		return newSchedule(capacity, now().minusDays(1), 10);
	}

	private int reserved(Integer scheduleId) {
		return this.schedules.findById(scheduleId).orElseThrow().getReservedCount();
	}

	private void assertRefused(Reason reason, Runnable action) {
		assertThatThrownBy(action::run).isInstanceOfSatisfying(ReservationException.class,
				ex -> assertThat(ex.getReason()).isEqualTo(reason));
	}

	@Test
	void bookingTakesOneSeat() {
		Integer scheduleId = openSchedule(3);
		String user = newUser();

		Integer id = this.service.book(user, scheduleId, "Leo", "first visit");

		assertThat(reserved(scheduleId)).isEqualTo(1);
		Reservation saved = this.service.findMine(user, id);
		assertThat(saved.getStatus()).isEqualTo(ReservationStatus.BOOKED);
		assertThat(saved.getPetName()).isEqualTo("Leo");
	}

	@Test
	void bookingClosesWhenCapacityIsReached() {
		Integer scheduleId = openSchedule(2);
		this.service.book(newUser(), scheduleId, "A", null);
		this.service.book(newUser(), scheduleId, "B", null);

		assertRefused(Reason.FULL, () -> this.service.book(newUser(), scheduleId, "C", null));

		assertThat(reserved(scheduleId)).isEqualTo(2);
		assertThat(this.schedules.findById(scheduleId).orElseThrow().statusAt(now())).isEqualTo(BookingStatus.FULL);
	}

	@Test
	void sameUserCannotBookTheSameScheduleTwice() {
		Integer scheduleId = openSchedule(5);
		String user = newUser();
		this.service.book(user, scheduleId, "Leo", null);

		assertRefused(Reason.DUPLICATE, () -> this.service.book(user, scheduleId, "Leo", null));

		assertThat(reserved(scheduleId)).isEqualTo(1);
	}

	@Test
	void bookingBeforeOpenTimeIsRefused() {
		Integer scheduleId = newSchedule(5, now().plusHours(1), 10);

		assertRefused(Reason.NOT_OPEN, () -> this.service.book(newUser(), scheduleId, "Leo", null));

		assertThat(reserved(scheduleId)).isZero();
	}

	@Test
	void bookingOnTheTreatmentDayIsRefused() {
		Integer scheduleId = newSchedule(5, now().minusDays(1), 0);

		assertRefused(Reason.ENDED, () -> this.service.book(newUser(), scheduleId, "Leo", null));
	}

	@Test
	void cancellingReturnsTheSeatAndAllowsRebooking() {
		Integer scheduleId = openSchedule(1);
		String user = newUser();
		Integer id = this.service.book(user, scheduleId, "Leo", null);

		this.service.cancel(user, id);

		assertThat(reserved(scheduleId)).isZero();
		assertThat(this.service.findMine(user, id).getStatus()).isEqualTo(ReservationStatus.CANCELLED);

		// cancelling twice must not release a second seat
		assertRefused(Reason.NOT_FOUND, () -> this.service.cancel(user, id));
		assertThat(reserved(scheduleId)).isZero();

		Integer again = this.service.book(user, scheduleId, "Leo", "again");
		assertThat(again).isEqualTo(id);
		assertThat(reserved(scheduleId)).isEqualTo(1);
	}

	@Test
	void anotherUsersReservationCannotBeCancelled() {
		Integer scheduleId = openSchedule(2);
		Integer id = this.service.book(newUser(), scheduleId, "Leo", null);

		assertRefused(Reason.NOT_FOUND, () -> this.service.cancel(newUser(), id));

		assertThat(reserved(scheduleId)).isEqualTo(1);
	}

	@Test
	void changingDetailsKeepsTheSeat() {
		Integer scheduleId = openSchedule(2);
		String user = newUser();
		Integer id = this.service.book(user, scheduleId, "Leo", null);

		this.service.change(user, id, scheduleId, "Leo Jr", "allergy");

		Reservation changed = this.service.findMine(user, id);
		assertThat(changed.getPetName()).isEqualTo("Leo Jr");
		assertThat(changed.getMemo()).isEqualTo("allergy");
		assertThat(reserved(scheduleId)).isEqualTo(1);
	}

	@Test
	void movingToAnotherScheduleMovesTheSeat() {
		Integer from = openSchedule(2);
		Integer to = openSchedule(2);
		String user = newUser();
		Integer id = this.service.book(user, from, "Leo", null);

		this.service.change(user, id, to, "Leo", null);

		assertThat(reserved(from)).isZero();
		assertThat(reserved(to)).isEqualTo(1);
		assertThat(this.service.findMine(user, id).getSchedule().getId()).isEqualTo(to);
	}

	@Test
	void movingToAFullScheduleChangesNothing() {
		Integer from = openSchedule(2);
		Integer to = openSchedule(1);
		this.service.book(newUser(), to, "Other", null);
		String user = newUser();
		Integer id = this.service.book(user, from, "Leo", null);

		assertRefused(Reason.FULL, () -> this.service.change(user, id, to, "Leo", null));

		assertThat(reserved(from)).isEqualTo(1);
		assertThat(reserved(to)).isEqualTo(1);
		assertThat(this.service.findMine(user, id).getSchedule().getId()).isEqualTo(from);
	}

	@Test
	void manyUsersAtOnceNeverExceedCapacity() throws Exception {
		int capacity = 5;
		int requests = 40;
		Integer scheduleId = openSchedule(capacity);
		List<String> usernames = new ArrayList<>();
		for (int i = 0; i < requests; i++) {
			usernames.add(newUser());
		}

		ExecutorService pool = Executors.newFixedThreadPool(requests);
		CountDownLatch start = new CountDownLatch(1);
		List<Future<Reason>> results = new ArrayList<>();
		for (String username : usernames) {
			Callable<Reason> task = () -> {
				start.await();
				try {
					this.service.book(username, scheduleId, "pet", null);
					return null;
				}
				catch (ReservationException ex) {
					return ex.getReason();
				}
			};
			results.add(pool.submit(task));
		}
		start.countDown();

		int booked = 0;
		int full = 0;
		for (Future<Reason> result : results) {
			Reason reason = result.get();
			if (reason == null) {
				booked++;
			}
			else if (reason == Reason.FULL) {
				full++;
			}
		}
		pool.shutdown();

		assertThat(booked).isEqualTo(capacity);
		assertThat(full).isEqualTo(requests - capacity);
		assertThat(reserved(scheduleId)).isEqualTo(capacity);
		assertThat(this.reservations.countByScheduleIdAndStatus(scheduleId, ReservationStatus.BOOKED))
			.isEqualTo(capacity);
	}

	@Test
	void sameUserSendingTwoRequestsAtOnceGetsOneSeat() throws Exception {
		Integer scheduleId = openSchedule(5);
		String user = newUser();
		int requests = 8;

		ExecutorService pool = Executors.newFixedThreadPool(requests);
		CountDownLatch start = new CountDownLatch(1);
		List<Future<Boolean>> results = new ArrayList<>();
		for (int i = 0; i < requests; i++) {
			Callable<Boolean> task = () -> {
				start.await();
				try {
					this.service.book(user, scheduleId, "pet", null);
					return true;
				}
				catch (ReservationException ex) {
					return false;
				}
			};
			results.add(pool.submit(task));
		}
		start.countDown();
		int booked = 0;
		for (Future<Boolean> result : results) {
			if (result.get()) {
				booked++;
			}
		}
		pool.shutdown();

		assertThat(booked).isEqualTo(1);
		assertThat(reserved(scheduleId)).isEqualTo(1);
	}

}
