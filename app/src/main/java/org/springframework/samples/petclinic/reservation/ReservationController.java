package org.springframework.samples.petclinic.reservation;

import java.security.Principal;
import java.time.Clock;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.reservation.ReservationException.Reason;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

/**
 * Pages for the signed-in user's own reservations.
 */
@Controller
class ReservationController {

	private static final String FORM_VIEW = "reservations/reservationForm";

	private final ReservationService service;

	private final TreatmentScheduleRepository schedules;

	private final Clock clock;

	ReservationController(ReservationService service, TreatmentScheduleRepository schedules, Clock clock) {
		this.service = service;
		this.schedules = schedules;
		this.clock = clock;
	}

	@GetMapping("/reservations")
	String myReservations(Principal principal, Model model) {
		model.addAttribute("reservations", this.service.findMine(principal.getName()));
		model.addAttribute("now", LocalDateTime.now(this.clock));
		return "reservations/myReservations";
	}

	@GetMapping("/reservations/new")
	String newForm(@RequestParam int scheduleId, Model model) {
		ReservationForm form = new ReservationForm();
		form.setScheduleId(scheduleId);
		model.addAttribute("reservationForm", form);
		return showForm(model, scheduleId, null);
	}

	@PostMapping("/reservations")
	String book(@Valid @ModelAttribute("reservationForm") ReservationForm form, BindingResult result,
			Principal principal, Model model, RedirectAttributes redirectAttributes) {
		if (form.getScheduleId() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		if (result.hasErrors()) {
			return showForm(model, form.getScheduleId(), null);
		}
		try {
			this.service.book(principal.getName(), form.getScheduleId(), form.getPetName(), form.getMemo());
		}
		catch (ReservationException ex) {
			return refused(ex, model, form.getScheduleId(), null);
		}
		redirectAttributes.addFlashAttribute("messageKey", "reservation.booked");
		return "redirect:/reservations";
	}

	@GetMapping("/reservations/{reservationId}/edit")
	String editForm(@PathVariable int reservationId, Principal principal, Model model) {
		Reservation reservation = mine(principal, reservationId);
		ReservationForm form = new ReservationForm();
		form.setScheduleId(reservation.getSchedule().getId());
		form.setPetName(reservation.getPetName());
		form.setMemo(reservation.getMemo());
		model.addAttribute("reservationForm", form);
		return showForm(model, form.getScheduleId(), reservationId);
	}

	@PostMapping("/reservations/{reservationId}")
	String change(@PathVariable int reservationId, @Valid @ModelAttribute("reservationForm") ReservationForm form,
			BindingResult result, Principal principal, Model model, RedirectAttributes redirectAttributes) {
		Reservation reservation = mine(principal, reservationId);
		if (result.hasErrors()) {
			return showForm(model, reservation.getSchedule().getId(), reservationId);
		}
		try {
			this.service.change(principal.getName(), reservationId, form.getScheduleId(), form.getPetName(),
					form.getMemo());
		}
		catch (ReservationException ex) {
			return refused(ex, model, reservation.getSchedule().getId(), reservationId);
		}
		redirectAttributes.addFlashAttribute("messageKey", "reservation.changed");
		return "redirect:/reservations";
	}

	@PostMapping("/reservations/{reservationId}/cancel")
	String cancel(@PathVariable int reservationId, Principal principal, RedirectAttributes redirectAttributes) {
		try {
			this.service.cancel(principal.getName(), reservationId);
			redirectAttributes.addFlashAttribute("messageKey", "reservation.cancelled");
		}
		catch (ReservationException ex) {
			if (ex.getReason() == Reason.NOT_FOUND) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND);
			}
			redirectAttributes.addFlashAttribute("errorKey", ex.getMessageKey());
		}
		return "redirect:/reservations";
	}

	private Reservation mine(Principal principal, int reservationId) {
		try {
			return this.service.findMine(principal.getName(), reservationId);
		}
		catch (ReservationException ex) {
			// Someone else's reservation looks the same as a missing one
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
	}

	private String refused(ReservationException ex, Model model, Integer scheduleId, Integer reservationId) {
		if (ex.getReason() == Reason.NOT_FOUND) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		model.addAttribute("errorKey", ex.getMessageKey());
		return showForm(model, scheduleId, reservationId);
	}

	/**
	 * @param currentScheduleId the schedule shown at the top of the form
	 * @param reservationId null when making a new reservation
	 */
	private String showForm(Model model, Integer currentScheduleId, Integer reservationId) {
		LocalDateTime now = LocalDateTime.now(this.clock);
		TreatmentSchedule schedule = this.schedules.findById(currentScheduleId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		model.addAttribute("schedule", schedule);
		model.addAttribute("reservationId", reservationId);
		model.addAttribute("now", now);
		if (reservationId != null) {
			// Other sessions the reservation can move to
			model.addAttribute("choices", this.schedules
				.findByTreatmentDateGreaterThanEqualOrderByTreatmentDateAscStartTimeAsc(now.toLocalDate().plusDays(1)));
		}
		return FORM_VIEW;
	}

}
