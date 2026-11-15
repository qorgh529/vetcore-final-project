package org.springframework.samples.petclinic.reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ScheduleForm {

	@NotNull
	private Integer hospitalId;

	@NotBlank
	@Size(max = 80)
	private String treatmentName;

	@NotNull
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate treatmentDate;

	@NotNull
	@DateTimeFormat(pattern = "HH:mm")
	private LocalTime startTime;

	@NotNull
	@Min(1)
	@Max(100000)
	private Integer capacity;

	@NotNull
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	private LocalDateTime bookingOpensAt;

	public Integer getHospitalId() {
		return this.hospitalId;
	}

	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
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

	public Integer getCapacity() {
		return this.capacity;
	}

	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}

	public LocalDateTime getBookingOpensAt() {
		return this.bookingOpensAt;
	}

	public void setBookingOpensAt(LocalDateTime bookingOpensAt) {
		this.bookingOpensAt = bookingOpensAt;
	}

}
