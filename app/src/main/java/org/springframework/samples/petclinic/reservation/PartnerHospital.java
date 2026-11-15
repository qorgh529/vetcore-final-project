package org.springframework.samples.petclinic.reservation;

import org.springframework.samples.petclinic.model.NamedEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * A hospital that is allowed to give the treatment.
 */
@Entity
@Table(name = "partner_hospitals")
public class PartnerHospital extends NamedEntity {

	@Column(nullable = false)
	private String region;

	private String address;

	private String telephone;

	public String getRegion() {
		return this.region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTelephone() {
		return this.telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

}
