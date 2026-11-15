package org.springframework.samples.petclinic.account;

import java.time.LocalDateTime;

import org.springframework.samples.petclinic.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * A person who can sign in and make reservations.
 */
@Entity
@Table(name = "users")
public class AppUser extends BaseEntity {

	public static final String ROLE_USER = "USER";

	public static final String ROLE_STAFF = "STAFF";

	@Column(nullable = false, unique = true)
	private String username;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String displayName;

	@Column(nullable = false)
	private String role = ROLE_USER;

	@Column(nullable = false)
	private LocalDateTime createdAt;

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

	public String getRole() {
		return this.role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public LocalDateTime getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

}
