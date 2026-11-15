package org.springframework.samples.petclinic.reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.samples.petclinic.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * One treatment session at a hospital, with a fixed number of seats.
 * <p>
 * {@code reservedCount} is only changed by the conditional updates in
 * {@link TreatmentScheduleRepository}, never through this entity.
 */
@Entity
@Table(name = "treatment_schedules")
public class TreatmentSchedule extends BaseEntity {

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "hospital_id")
	private PartnerHospital hospital;

	@Column(nullable = false)
	private String treatmentName;

	@Column(nullable = false)
	private LocalDate treatmentDate;

	@Column(nullable = false)
	private LocalTime startTime;

	@Column(nullable = false)
	private int capacity;

	@Column(nullable = false, updatable = false)
	private int reservedCount;

	@Column(nullable = false)
	private LocalDateTime bookingOpensAt;

	public PartnerHospital getHospital() {
		return this.hospital;
	}

	public void setHospital(PartnerHospital hospital) {
		this.hospital = hospital;
	}

	public String getTreatmentName() {
		return this.treatmentName;
	}

	public void setTreatmentName(String treatmentName) {
		this.treatmentName = treatmentName;
	}

	public LocalDate getTreatmentDate() {
		return this.treatmentDate;
	}

	public void setTreatmentDate(LocalDate treatmentDate) {
		this.treatmentDate = treatmentDate;
	}

	public LocalTime getStartTime() {
		return this.startTime;
	}

	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}

	public int getCapacity() {
		return this.capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public int getReservedCount() {
		return this.reservedCount;
	}

	public LocalDateTime getBookingOpensAt() {
		return this.bookingOpensAt;
	}

	public void setBookingOpensAt(LocalDateTime bookingOpensAt) {
		this.bookingOpensAt = bookingOpensAt;
	}

	public int getRemaining() {
		return Math.max(this.capacity - this.reservedCount, 0);
	}

	public boolean isFull() {
		return this.reservedCount >= this.capacity;
	}

	/** Booking has opened. */
	public boolean isOpenAt(LocalDateTime now) {
		return !now.isBefore(this.bookingOpensAt);
	}

	/** Changes are accepted until the day before the treatment. */
	public boolean isChangeableAt(LocalDateTime now) {
		return now.toLocalDate().isBefore(this.treatmentDate);
	}

	public BookingStatus statusAt(LocalDateTime now) {
		if (!isChangeableAt(now)) {
			return BookingStatus.ENDED;
		}
		if (!isOpenAt(now)) {
			return BookingStatus.NOT_OPEN;
		}
		return isFull() ? BookingStatus.FULL : BookingStatus.OPEN;
	}

}
