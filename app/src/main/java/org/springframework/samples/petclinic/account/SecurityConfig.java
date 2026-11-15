package org.springframework.samples.petclinic.account;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Browsing hospitals and schedules is public. Reservations need a signed-in user. Owner
 * records and the admin pages are for staff only.
 */
@Configuration
public class SecurityConfig {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(auth -> auth.requestMatchers("/owners/**", "/admin/**")
			.hasRole(AppUser.ROLE_STAFF)
			.requestMatchers("/reservations/**")
			.authenticated()
			.requestMatchers("/", "/resources/**", "/webjars/**", "/error", "/oups", "/login", "/signup",
					"/hospitals/**", "/schedules/**", "/api/schedules/**", "/vets", "/vets.html", "/actuator/health/**",
					"/actuator/info", "/actuator/prometheus")
			.permitAll()
			.anyRequest()
			.authenticated())
			.formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/reservations").permitAll())
			.logout(logout -> logout.logoutSuccessUrl("/"));
		return http.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
