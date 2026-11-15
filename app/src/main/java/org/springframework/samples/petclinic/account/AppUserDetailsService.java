package org.springframework.samples.petclinic.account;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Loads sign-in data from the users table.
 */
@Service
public class AppUserDetailsService implements UserDetailsService {

	private final AppUserRepository users;

	public AppUserDetailsService(AppUserRepository users) {
		this.users = users;
	}

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) {
		AppUser user = this.users.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
		return User.withUsername(user.getUsername()).password(user.getPassword()).roles(user.getRole()).build();
	}

}
