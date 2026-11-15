package org.springframework.samples.petclinic.reservation;

/**
 * Seat numbers for one schedule, for clients that poll without loading a page.
 */
public record ScheduleAvailability(Integer scheduleId, int capacity, int reserved, int remaining,
		BookingStatus status) {

	static ScheduleAvailability of(TreatmentSchedule schedule, java.time.LocalDateTime now) {
		return new ScheduleAvailability(schedule.getId(), schedule.getCapacity(), schedule.getReservedCount(),
				schedule.getRemaining(), schedule.statusAt(now));
	}

}
