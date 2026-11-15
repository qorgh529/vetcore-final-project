package org.springframework.samples.petclinic.reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TreatmentScheduleRepository extends JpaRepository<TreatmentSchedule, Integer> {

	List<TreatmentSchedule> findByTreatmentDateGreaterThanEqualOrderByTreatmentDateAscStartTimeAsc(LocalDate from);

	List<TreatmentSchedule> findByHospitalIdAndTreatmentDateGreaterThanEqualOrderByTreatmentDateAscStartTimeAsc(
			Integer hospitalId, LocalDate from);

	/**
	 * Takes one seat if any is left. The check and the increment are one statement, so
	 * the database decides the winner when many requests arrive together.
	 * @return 1 if a seat was taken, 0 if the schedule is full
	 */
	@Modifying
	@Query("update TreatmentSchedule s set s.reservedCount = s.reservedCount + 1 "
			+ "where s.id = :id and s.reservedCount < s.capacity")
	int tryReserveSeat(@Param("id") Integer id);

	/**
	 * Gives one seat back.
	 * @return 1 if a seat was released
	 */
	@Modifying
	@Query("update TreatmentSchedule s set s.reservedCount = s.reservedCount - 1 "
			+ "where s.id = :id and s.reservedCount > 0")
	int releaseSeat(@Param("id") Integer id);

	List<TreatmentSchedule> findAllByOrderByTreatmentDateDescStartTimeAsc();

	/**
	 * Changes a schedule from the admin page. The capacity is only accepted while it
	 * still covers the seats already taken, checked in the same statement as the change.
	 * @return 1 if changed, 0 if the new capacity is below the reserved count
	 */
	@Modifying
	@Query("update TreatmentSchedule s set s.hospital = :hospital, s.treatmentName = :treatmentName, "
			+ "s.treatmentDate = :treatmentDate, s.startTime = :startTime, s.capacity = :capacity, "
			+ "s.bookingOpensAt = :bookingOpensAt where s.id = :id and s.reservedCount <= :capacity")
	int updateSchedule(@Param("id") Integer id, @Param("hospital") PartnerHospital hospital,
			@Param("treatmentName") String treatmentName, @Param("treatmentDate") LocalDate treatmentDate,
			@Param("startTime") LocalTime startTime, @Param("capacity") int capacity,
			@Param("bookingOpensAt") LocalDateTime bookingOpensAt);

}
