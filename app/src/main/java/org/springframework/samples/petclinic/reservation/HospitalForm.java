package org.springframework.samples.petclinic.reservation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class HospitalForm {

	@NotBlank
	@Size(max = 80)
	private String name;

	@NotBlank
	@Size(max = 30)
	private String region;

	@Size(max = 255)
	private String address;

	@Pattern(regexp = "[0-9-]{0,20}", message = "{admin.telephone.invalid}")
	private String telephone;

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

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
