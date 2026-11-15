package org.springframework.samples.petclinic.account;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class SignupForm {

	@NotBlank
	@Pattern(regexp = "[A-Za-z0-9_]{3,30}", message = "{signup.username.invalid}")
	private String username;

	@NotBlank
	@Size(min = 8, max = 64, message = "{signup.password.invalid}")
	private String password;

	@NotBlank
	@Size(max = 30)
	private String displayName;

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

}
