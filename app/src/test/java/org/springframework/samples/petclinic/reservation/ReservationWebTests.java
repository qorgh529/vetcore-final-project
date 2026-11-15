package org.springframework.samples.petclinic.reservation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.samples.petclinic.account.AppUserRepository;
import org.springframework.test.web.servlet.MockMvc;

/**
 * The pages and access rules, through the whole application with security switched on.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ReservationWebTests {

	private static final AtomicInteger SEQUENCE = new AtomicInteger();

	@Autowired
	MockMvc mockMvc;

	@Autowired
	AppUserRepository users;

	@Autowired
	TreatmentScheduleRepository schedules;

	@Autowired
	PartnerHospitalRepository hospitals;

	@Autowired
	ReservationRepository reservations;

	@Autowired
	Clock clock;

	private String signUp() throws Exception {
		String username = "web_" + SEQUENCE.incrementAndGet();
		this.mockMvc
			.perform(post("/signup").with(csrf())
				.param("username", username)
				.param("password", "password123")
				.param("displayName", "Web Tester"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/login"));
		return username;
	}

	private Integer openSchedule(int capacity) {
		LocalDateTime now = LocalDateTime.now(this.clock);
		TreatmentSchedule schedule = new TreatmentSchedule();
		schedule.setHospital(this.hospitals.findById(1).orElseThrow());
		schedule.setTreatmentName("web test treatment");
		schedule.setTreatmentDate(now.toLocalDate().plusDays(10));
		schedule.setStartTime(LocalTime.of(10, 0));
		schedule.setCapacity(capacity);
		schedule.setBookingOpensAt(now.minusDays(1));
		return this.schedules.save(schedule).getId();
	}

	@Test
	void hospitalsAndSchedulesArePublic() throws Exception {
		this.mockMvc.perform(get("/hospitals"))
			.andExpect(status().isOk())
			.andExpect(view().name("hospitals/hospitalList"))
			.andExpect(content().string(containsString("베트코어 서울본원")));
		this.mockMvc.perform(get("/hospitals").param("region", "부산"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("해운대동물병원")))
			.andExpect(model().attribute("selectedRegion", "부산"));
		this.mockMvc.perform(get("/hospitals/1")).andExpect(status().isOk());
		this.mockMvc.perform(get("/hospitals/9999")).andExpect(status().isNotFound());
		this.mockMvc.perform(get("/schedules"))
			.andExpect(status().isOk())
			.andExpect(view().name("schedules/scheduleList"));
	}

	@Test
	void availabilityApiReportsSeats() throws Exception {
		Integer scheduleId = openSchedule(7);
		this.mockMvc.perform(get("/api/schedules/{id}/availability", scheduleId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.capacity").value(7))
			.andExpect(jsonPath("$.remaining").value(7))
			.andExpect(jsonPath("$.status").value("OPEN"));
	}

	@Test
	void reservationsNeedLogin() throws Exception {
		this.mockMvc.perform(get("/reservations"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/login"));
		this.mockMvc.perform(post("/reservations").with(csrf()).param("scheduleId", "1").param("petName", "Leo"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/login"));
	}

	@Test
	void signUpThenLogin() throws Exception {
		String username = signUp();
		assertThat(this.users.findByUsername(username).orElseThrow().getPassword()).startsWith("$2")
			.doesNotContain("password123");

		this.mockMvc.perform(formLogin("/login").user(username).password("password123"))
			.andExpect(authenticated().withUsername(username))
			.andExpect(redirectedUrl("/reservations"));
		this.mockMvc.perform(formLogin("/login").user(username).password("wrong-password"))
			.andExpect(unauthenticated())
			.andExpect(redirectedUrl("/login?error"));
	}

	@Test
	void signUpRejectsDuplicateAndWeakInput() throws Exception {
		String username = signUp();
		this.mockMvc
			.perform(post("/signup").with(csrf())
				.param("username", username)
				.param("password", "password123")
				.param("displayName", "Again"))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasFieldErrors("signupForm", "username"));
		this.mockMvc
			.perform(post("/signup").with(csrf())
				.param("username", "ok_name_" + SEQUENCE.incrementAndGet())
				.param("password", "short")
				.param("displayName", "Weak"))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasFieldErrors("signupForm", "password"));
	}

	@Test
	void bookListChangeAndCancel() throws Exception {
		String username = signUp();
		Integer scheduleId = openSchedule(3);

		this.mockMvc.perform(get("/reservations/new").param("scheduleId", scheduleId.toString()).with(user(username)))
			.andExpect(status().isOk())
			.andExpect(view().name("reservations/reservationForm"));

		this.mockMvc
			.perform(post("/reservations").with(user(username))
				.with(csrf())
				.param("scheduleId", scheduleId.toString())
				.param("petName", "Leo")
				.param("memo", "first"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/reservations"));
		Reservation reservation = this.reservations.findByUserUsernameOrderByCreatedAtDesc(username).get(0);

		this.mockMvc.perform(get("/reservations").with(user(username)))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("Leo")));

		this.mockMvc.perform(get("/reservations/{id}/edit", reservation.getId()).with(user(username)))
			.andExpect(status().isOk());
		this.mockMvc
			.perform(post("/reservations/{id}", reservation.getId()).with(user(username))
				.with(csrf())
				.param("scheduleId", scheduleId.toString())
				.param("petName", "Leo Jr")
				.param("memo", "changed"))
			.andExpect(redirectedUrl("/reservations"));
		assertThat(this.reservations.findById(reservation.getId()).orElseThrow().getPetName()).isEqualTo("Leo Jr");

		this.mockMvc.perform(post("/reservations/{id}/cancel", reservation.getId()).with(user(username)).with(csrf()))
			.andExpect(redirectedUrl("/reservations"));
		assertThat(this.reservations.findById(reservation.getId()).orElseThrow().getStatus())
			.isEqualTo(ReservationStatus.CANCELLED);
		assertThat(this.schedules.findById(scheduleId).orElseThrow().getReservedCount()).isZero();
	}

	@Test
	void fullScheduleShowsTheFormAgainWithAnError() throws Exception {
		Integer scheduleId = openSchedule(1);
		String first = signUp();
		String second = signUp();
		this.mockMvc
			.perform(post("/reservations").with(user(first))
				.with(csrf())
				.param("scheduleId", scheduleId.toString())
				.param("petName", "A"))
			.andExpect(redirectedUrl("/reservations"));

		this.mockMvc
			.perform(post("/reservations").with(user(second))
				.with(csrf())
				.param("scheduleId", scheduleId.toString())
				.param("petName", "B"))
			.andExpect(status().isOk())
			.andExpect(view().name("reservations/reservationForm"))
			.andExpect(model().attribute("errorKey", "reservation.error.FULL"));
	}

	@Test
	void otherUsersCannotSeeOrCancelAReservation() throws Exception {
		Integer scheduleId = openSchedule(2);
		String owner = signUp();
		String other = signUp();
		this.mockMvc
			.perform(post("/reservations").with(user(owner))
				.with(csrf())
				.param("scheduleId", scheduleId.toString())
				.param("petName", "Leo"))
			.andExpect(redirectedUrl("/reservations"));
		Integer id = this.reservations.findByUserUsernameOrderByCreatedAtDesc(owner).get(0).getId();

		this.mockMvc.perform(get("/reservations/{id}/edit", id).with(user(other))).andExpect(status().isNotFound());
		this.mockMvc.perform(post("/reservations/{id}/cancel", id).with(user(other)).with(csrf()))
			.andExpect(status().isNotFound());
		assertThat(this.reservations.findById(id).orElseThrow().getStatus()).isEqualTo(ReservationStatus.BOOKED);
	}

	@Test
	void postWithoutCsrfTokenIsForbidden() throws Exception {
		this.mockMvc.perform(post("/reservations/1/cancel").with(user("someone"))).andExpect(status().isForbidden());
	}

	@Test
	void onlyProbeAndMetricEndpointsAreOpen() throws Exception {
		this.mockMvc.perform(get("/actuator/health")).andExpect(status().isOk());
		this.mockMvc.perform(get("/actuator/health/readiness")).andExpect(status().isOk());
		this.mockMvc.perform(get("/actuator/env")).andExpect(status().is3xxRedirection());
	}

	@Test
	void ownerRecordsAndAdminPagesAreForStaffOnly() throws Exception {
		this.mockMvc.perform(get("/owners/find")).andExpect(redirectedUrl("/login"));
		this.mockMvc.perform(get("/owners/find").with(user("guardian"))).andExpect(status().isForbidden());
		this.mockMvc.perform(get("/admin/schedules").with(user("guardian"))).andExpect(status().isForbidden());
		this.mockMvc.perform(get("/owners/find").with(user("staff").roles("STAFF"))).andExpect(status().isOk());
		this.mockMvc.perform(get("/admin/schedules").with(user("staff").roles("STAFF")))
			.andExpect(status().isOk())
			.andExpect(view().name("admin/scheduleList"));
		this.mockMvc.perform(get("/admin/hospitals").with(user("staff").roles("STAFF"))).andExpect(status().isOk());
	}

	@Test
	void staffRegistersHospitalAndSchedule() throws Exception {
		String name = "admin test hospital " + SEQUENCE.incrementAndGet();
		this.mockMvc.perform(get("/admin/hospitals/new").with(user("staff").roles("STAFF"))).andExpect(status().isOk());
		this.mockMvc
			.perform(post("/admin/hospitals").with(user("staff").roles("STAFF"))
				.with(csrf())
				.param("name", name)
				.param("region", "제주")
				.param("address", "제주시 1")
				.param("telephone", "064-000-0000"))
			.andExpect(redirectedUrl("/admin/hospitals"));
		PartnerHospital hospital = this.hospitals.findByRegionOrderByNameAsc("제주")
			.stream()
			.filter(h -> h.getName().equals(name))
			.findFirst()
			.orElseThrow();

		this.mockMvc.perform(get("/admin/schedules/new").with(user("staff").roles("STAFF"))).andExpect(status().isOk());
		String treatment = "admin test treatment " + SEQUENCE.incrementAndGet();
		this.mockMvc
			.perform(post("/admin/schedules").with(user("staff").roles("STAFF"))
				.with(csrf())
				.param("hospitalId", hospital.getId().toString())
				.param("treatmentName", treatment)
				.param("treatmentDate", "2099-01-20")
				.param("startTime", "10:00")
				.param("capacity", "2")
				.param("bookingOpensAt", "2026-01-01T10:00"))
			.andExpect(redirectedUrl("/admin/schedules"));
		TreatmentSchedule schedule = this.schedules.findAll()
			.stream()
			.filter(s -> s.getTreatmentName().equals(treatment))
			.findFirst()
			.orElseThrow();
		assertThat(schedule.getCapacity()).isEqualTo(2);

		// two seats taken, then the capacity may not drop below two
		for (int i = 0; i < 2; i++) {
			this.mockMvc
				.perform(post("/reservations").with(user(signUp()))
					.with(csrf())
					.param("scheduleId", schedule.getId().toString())
					.param("petName", "pet"))
				.andExpect(redirectedUrl("/reservations"));
		}
		this.mockMvc.perform(get("/admin/schedules/{id}/edit", schedule.getId()).with(user("staff").roles("STAFF")))
			.andExpect(status().isOk());
		this.mockMvc
			.perform(post("/admin/schedules/{id}", schedule.getId()).with(user("staff").roles("STAFF"))
				.with(csrf())
				.param("hospitalId", hospital.getId().toString())
				.param("treatmentName", treatment)
				.param("treatmentDate", "2099-01-20")
				.param("startTime", "10:00")
				.param("capacity", "1")
				.param("bookingOpensAt", "2026-01-01T10:00"))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasFieldErrors("scheduleForm", "capacity"));
		this.mockMvc
			.perform(post("/admin/schedules/{id}", schedule.getId()).with(user("staff").roles("STAFF"))
				.with(csrf())
				.param("hospitalId", hospital.getId().toString())
				.param("treatmentName", treatment)
				.param("treatmentDate", "2099-01-20")
				.param("startTime", "11:30")
				.param("capacity", "5")
				.param("bookingOpensAt", "2026-01-01T10:00"))
			.andExpect(redirectedUrl("/admin/schedules"));
		TreatmentSchedule changed = this.schedules.findById(schedule.getId()).orElseThrow();
		assertThat(changed.getCapacity()).isEqualTo(5);
		assertThat(changed.getReservedCount()).isEqualTo(2);
		assertThat(changed.getStartTime()).isEqualTo(LocalTime.of(11, 30));
	}

}
