package org.springframework.samples.petclinic.reservation;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PartnerHospitalRepository extends JpaRepository<PartnerHospital, Integer> {

	List<PartnerHospital> findAllByOrderByRegionAscNameAsc();

	List<PartnerHospital> findByRegionOrderByNameAsc(String region);

	@Query("select distinct h.region from PartnerHospital h order by h.region")
	List<String> findRegions();

}
