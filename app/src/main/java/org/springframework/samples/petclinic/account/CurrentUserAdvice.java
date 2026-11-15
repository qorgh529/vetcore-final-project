package org.springframework.samples.petclinic.account;

import java.security.Principal;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Makes the signed-in user available to every page so the menu can show the right items.
 */
@ControllerAdvice
class CurrentUserAdvice {

	@ModelAttribute("currentUsername")
	String currentUsername(Principal principal) {
		return (principal != null) ? principal.getName() : null;
	}

	@ModelAttribute("currentUserIsStaff")
	boolean currentUserIsStaff(Principal principal) {
		return principal instanceof Authentication authentication && authentication.getAuthorities()
			.stream()
			.anyMatch(authority -> ("ROLE_" + AppUser.ROLE_STAFF).equals(authority.getAuthority()));
	}

}
