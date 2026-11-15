package org.springframework.samples.petclinic.reservation;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

/**
 * Public pages: partner hospitals, treatment schedules and remaining seats.
 */
@Controller
class HospitalController {

	private final PartnerHospitalRepository hospitals;

	private final TreatmentScheduleRepository schedules;

	private final Clock clock;

	HospitalController(PartnerHospitalRepository hospitals, TreatmentScheduleRepository schedules, Clock clock) {
		this.hospitals = hospitals;
		this.schedules = schedules;
		this.clock = clock;
	}

	@GetMapping("/hospitals")
	String hospitals(@RequestParam(required = false) String region, Model model) {
		boolean filtered = region != null && !region.isBlank();
		List<PartnerHospital> list = filtered ? this.hospitals.findByRegionOrderByNameAsc(region)
				: this.hospitals.findAllByOrderByRegionAscNameAsc();
		model.addAttribute("hospitals", list);
		model.addAttribute("regions", this.hospitals.findRegions());
		model.addAttribute("selectedRegion", filtered ? region : "");
		return "hospitals/hospitalList";
	}

	@GetMapping("/hospitals/{hospitalId}")
	String hospital(@PathVariable int hospitalId, Model model) {
		PartnerHospital hospital = this.hospitals.findById(hospitalId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		LocalDateTime now = LocalDateTime.now(this.clock);
		model.addAttribute("hospital", hospital);
		model.addAttribute("schedules",
				this.schedules.findByHospitalIdAndTreatmentDateGreaterThanEqualOrderByTreatmentDateAscStartTimeAsc(
						hospitalId, now.toLocalDate()));
		model.addAttribute("now", now);
		return "hospitals/hospitalDetails";
	}

	@GetMapping("/schedules")
	String schedules(Model model) {
		LocalDateTime now = LocalDateTime.now(this.clock);
		model.addAttribute("schedules", this.schedules
			.findByTreatmentDateGreaterThanEqualOrderByTreatmentDateAscStartTimeAsc(now.toLocalDate()));
		model.addAttribute("now", now);
		return "schedules/scheduleList";
	}

	@GetMapping("/api/schedules/{scheduleId}/availability")
	@ResponseBody
	ScheduleAvailability availability(@PathVariable int scheduleId) {
		TreatmentSchedule schedule = this.schedules.findById(scheduleId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		return ScheduleAvailability.of(schedule, LocalDateTime.now(this.clock));
	}

}
