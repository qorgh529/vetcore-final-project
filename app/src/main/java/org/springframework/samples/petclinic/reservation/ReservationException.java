package org.springframework.samples.petclinic.reservation;

/**
 * A reservation request that was refused by a business rule.
 */
public class ReservationException extends RuntimeException {

	public enum Reason {

		/** Booking for this schedule has not opened yet. */
		NOT_OPEN,
		/** The treatment day has arrived or passed. */
		ENDED,
		/** No seat is left. */
		FULL,
		/** The user already holds a seat in this schedule. */
		DUPLICATE,
		/** The schedule or reservation does not exist, or belongs to someone else. */
		NOT_FOUND

	}

	private final Reason reason;

	public ReservationException(Reason reason) {
		super(reason.name());
		this.reason = reason;
	}

	public Reason getReason() {
		return this.reason;
	}

	/** Key in messages.properties. */
	public String getMessageKey() {
		return "reservation.error." + this.reason.name();
	}

}
