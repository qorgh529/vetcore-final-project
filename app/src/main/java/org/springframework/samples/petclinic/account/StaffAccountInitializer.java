package org.springframework.samples.petclinic.account;

import java.time.Clock;
import java.time.LocalDateTime;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Creates the first staff account from configuration, because staff cannot sign up
 * through the public form. Nothing is created unless both values are set, so there is no
 * default password.
 * <p>
 * Set {@code PETCLINIC_STAFF_USERNAME} and {@code PETCLINIC_STAFF_PASSWORD}. An existing
 * account with that username is left as it is.
 */
@Component
class StaffAccountInitializer implements ApplicationRunner {

	private static final Log log = LogFactory.getLog(StaffAccountInitializer.class);

	private final AppUserRepository users;

	private final PasswordEncoder passwordEncoder;

	private final Clock clock;

	private final String username;

	private final String password;

	StaffAccountInitializer(AppUserRepository users, PasswordEncoder passwordEncoder, Clock clock,
			@Value("${petclinic.staff.username:}") String username,
			@Value("${petclinic.staff.password:}") String password) {
		this.users = users;
		this.passwordEncoder = passwordEncoder;
		this.clock = clock;
		this.username = username;
		this.password = password;
	}

	@Override
	public void run(ApplicationArguments args) {
		if (this.username.isBlank() || this.password.isBlank()) {
			log.info("No staff account configured; owner records and admin pages cannot be opened");
			return;
		}
		if (this.users.existsByUsername(this.username)) {
			return;
		}
		AppUser staff = new AppUser();
		staff.setUsername(this.username);
		staff.setPassword(this.passwordEncoder.encode(this.password));
		staff.setDisplayName(this.username);
		staff.setRole(AppUser.ROLE_STAFF);
		staff.setCreatedAt(LocalDateTime.now(this.clock));
		try {
			this.users.saveAndFlush(staff);
			log.info("Created staff account " + this.username);
		}
		catch (DataIntegrityViolationException ex) {
			// Another instance starting at the same moment created it first
		}
	}

}
