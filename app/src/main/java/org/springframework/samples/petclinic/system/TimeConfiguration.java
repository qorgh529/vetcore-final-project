package org.springframework.samples.petclinic.system;

import java.time.Clock;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * One clock for the whole application, fixed to the business time zone, so that booking
 * rules do not depend on the time zone of the server or container.
 */
@Configuration
class TimeConfiguration {

	@Bean
	Clock clock(@Value("${petclinic.zone:Asia/Seoul}") String zone) {
		return Clock.system(ZoneId.of(zone));
	}

}
