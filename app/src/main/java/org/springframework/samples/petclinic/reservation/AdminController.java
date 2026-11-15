package org.springframework.samples.petclinic.reservation;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

/**
 * Staff pages for registering partner hospitals and treatment schedules. Access is
 * limited to staff in {@code SecurityConfig}.
 * <p>
 * Nothing can be deleted here: reservations refer to schedules and schedules to
 * hospitals, and those records must stay readable.
 */
@Controller
class AdminController {

	private static final String HOSPITAL_FORM = "admin/hospitalForm";

	private static final String SCHEDULE_FORM = "admin/scheduleForm";

	private final PartnerHospitalRepository hospitals;

	private final TreatmentScheduleRepository schedules;

	private final Clock clock;

	AdminController(PartnerHospitalRepository hospitals, TreatmentScheduleRepository schedules, Clock clock) {
		this.hospitals = hospitals;
		this.schedules = schedules;
		this.clock = clock;
	}

	@GetMapping("/admin")
	String index() {
		return "redirect:/admin/schedules";
	}

	// ---- hospitals

	@GetMapping("/admin/hospitals")
	String hospitals(Model model) {
		model.addAttribute("hospitals", this.hospitals.findAllByOrderByRegionAscNameAsc());
		return "admin/hospitalList";
	}

	@GetMapping("/admin/hospitals/new")
	String newHospital(Model model) {
		model.addAttribute("hospitalForm", new HospitalForm());
		model.addAttribute("hospitalId", null);
		return HOSPITAL_FORM;
	}

	@PostMapping("/admin/hospitals")
	String createHospital(@Valid @ModelAttribute("hospitalForm") HospitalForm form, BindingResult result, Model model,
			RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			model.addAttribute("hospitalId", null);
			return HOSPITAL_FORM;
		}
		PartnerHospital hospital = new PartnerHospital();
		apply(form, hospital);
		this.hospitals.save(hospital);
		redirectAttributes.addFlashAttribute("messageKey", "admin.saved");
		return "redirect:/admin/hospitals";
	}

	@GetMapping("/admin/hospitals/{hospitalId}/edit")
	String editHospital(@PathVariable int hospitalId, Model model) {
		PartnerHospital hospital = hospital(hospitalId);
		HospitalForm form = new HospitalForm();
		form.setName(hospital.getName());
		form.setRegion(hospital.getRegion());
		form.setAddress(hospital.getAddress());
		form.setTelephone(hospital.getTelephone());
		model.addAttribute("hospitalForm", form);
		model.addAttribute("hospitalId", hospitalId);
		return HOSPITAL_FORM;
	}

	@PostMapping("/admin/hospitals/{hospitalId}")
	String updateHospital(@PathVariable int hospitalId, @Valid @ModelAttribute("hospitalForm") HospitalForm form,
			BindingResult result, Model model, RedirectAttributes redirectAttributes) {
		PartnerHospital hospital = hospital(hospitalId);
		if (result.hasErrors()) {
			model.addAttribute("hospitalId", hospitalId);
			return HOSPITAL_FORM;
		}
		apply(form, hospital);
		this.hospitals.save(hospital);
		redirectAttributes.addFlashAttribute("messageKey", "admin.saved");
		return "redirect:/admin/hospitals";
	}

	// ---- schedules

	@GetMapping("/admin/schedules")
	String schedules(Model model) {
		model.addAttribute("schedules", this.schedules.findAllByOrderByTreatmentDateDescStartTimeAsc());
		model.addAttribute("now", LocalDateTime.now(this.clock));
		return "admin/scheduleList";
	}

	@GetMapping("/admin/schedules/new")
	String newSchedule(Model model) {
		ScheduleForm form = new ScheduleForm();
		form.setBookingOpensAt(LocalDateTime.now(this.clock).truncatedTo(ChronoUnit.MINUTES));
		model.addAttribute("scheduleForm", form);
		return showScheduleForm(model, null);
	}

	@PostMapping("/admin/schedules")
	String createSchedule(@Valid @ModelAttribute("scheduleForm") ScheduleForm form, BindingResult result, Model model,
			RedirectAttributes redirectAttributes) {
		PartnerHospital hospital = formHospital(form, result);
		if (result.hasErrors()) {
			return showScheduleForm(model, null);
		}
		TreatmentSchedule schedule = new TreatmentSchedule();
		schedule.setHospital(hospital);
		schedule.setTreatmentName(form.getTreatmentName());
		schedule.setTreatmentDate(form.getTreatmentDate());
		schedule.setStartTime(form.getStartTime());
		schedule.setCapacity(form.getCapacity());
		schedule.setBookingOpensAt(form.getBookingOpensAt());
		this.schedules.save(schedule);
		redirectAttributes.addFlashAttribute("messageKey", "admin.saved");
		return "redirect:/admin/schedules";
	}

	@GetMapping("/admin/schedules/{scheduleId}/edit")
	String editSchedule(@PathVariable int scheduleId, Model model) {
		TreatmentSchedule schedule = schedule(scheduleId);
		ScheduleForm form = new ScheduleForm();
		form.setHospitalId(schedule.getHospital().getId());
		form.setTreatmentName(schedule.getTreatmentName());
		form.setTreatmentDate(schedule.getTreatmentDate());
		form.setStartTime(schedule.getStartTime());
		form.setCapacity(schedule.getCapacity());
		form.setBookingOpensAt(schedule.getBookingOpensAt());
		model.addAttribute("scheduleForm", form);
		return showScheduleForm(model, schedule);
	}

	@PostMapping("/admin/schedules/{scheduleId}")
	@Transactional
	String updateSchedule(@PathVariable int scheduleId, @Valid @ModelAttribute("scheduleForm") ScheduleForm form,
			BindingResult result, Model model, RedirectAttributes redirectAttributes) {
		TreatmentSchedule schedule = schedule(scheduleId);
		PartnerHospital hospital = formHospital(form, result);
		if (!result.hasErrors()) {
			// Refused when someone booked in the meantime and the new capacity no longer
			// covers the seats taken
			int changed = this.schedules.updateSchedule(scheduleId, hospital, form.getTreatmentName(),
					form.getTreatmentDate(), form.getStartTime(), form.getCapacity(), form.getBookingOpensAt());
			if (changed == 0) {
				result.rejectValue("capacity", "admin.capacity.belowReserved");
			}
		}
		if (result.hasErrors()) {
			return showScheduleForm(model, schedule);
		}
		redirectAttributes.addFlashAttribute("messageKey", "admin.saved");
		return "redirect:/admin/schedules";
	}

	private String showScheduleForm(Model model, TreatmentSchedule existing) {
		model.addAttribute("hospitals", this.hospitals.findAllByOrderByRegionAscNameAsc());
		model.addAttribute("scheduleId", (existing != null) ? existing.getId() : null);
		model.addAttribute("reservedCount", (existing != null) ? existing.getReservedCount() : 0);
		return SCHEDULE_FORM;
	}

	private PartnerHospital formHospital(ScheduleForm form, BindingResult result) {
		if (form.getHospitalId() == null) {
			return null;
		}
		PartnerHospital hospital = this.hospitals.findById(form.getHospitalId()).orElse(null);
		if (hospital == null) {
			result.rejectValue("hospitalId", "notFound");
		}
		return hospital;
	}

	private PartnerHospital hospital(int hospitalId) {
		return this.hospitals.findById(hospitalId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}

	private TreatmentSchedule schedule(int scheduleId) {
		return this.schedules.findById(scheduleId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}

	private static void apply(HospitalForm form, PartnerHospital hospital) {
		hospital.setName(form.getName());
		hospital.setRegion(form.getRegion());
		hospital.setAddress(form.getAddress());
		hospital.setTelephone(form.getTelephone());
	}

}
