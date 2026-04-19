package se.voizter.felparkering.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import se.voizter.felparkering.api.enums.Status;
import se.voizter.felparkering.api.model.AttendantGroup;
import se.voizter.felparkering.api.model.Report;
import se.voizter.felparkering.api.model.User;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Optional<Report> findById(Long id);
    List<Report> findByAttendantGroup(AttendantGroup attendantGroup);
    List<Report> findByCreatedBy(User user);

    @Query("""
            SELECT r FROM Report r
            WHERE (:status IS NULL OR r.status = :status)
                AND (
                    :search IS NULL
                    OR LOWER(r.address.street) LIKE LOWER(CONCAT('%', CAST(:search as string), '%'))
                    OR LOWER(r.licensePlate) LIKE LOWER(CONCAT('%', CAST(:search as string), '%'))
                )
            """)
    Page<Report> findByFilters(@Param("status") Status status, @Param("search") String search, Pageable pageable);

    @Query("""
            SELECT r FROM Report r
            WHERE r.attendantGroup = :attendantGroup
                AND (:status IS NULL OR r.status = :status)
                AND (:attendant IS NULL OR r.assignedTo = :attendant)
                AND (
                    :search IS NULL
                    OR LOWER(r.address.street) LIKE LOWER(CONCAT('%', CAST(:search as string), '%'))
                    OR LOWER(r.licensePlate) LIKE LOWER(CONCAT('%', CAST(:search as string), '%'))
                )
            """)
    Page<Report> findByFiltersInGroup(
        @Param("status") Status status, 
        @Param("attendant") User attendant, 
        @Param("attendantGroup") AttendantGroup attendantGroup, 
        @Param("search") String search,
        Pageable pageable
    );

    @Query("""
            SELECT r FROM Report r
            WHERE r.createdBy = :user
                AND (:status IS NULL OR r.status = :status)
                AND (
                    :search IS NULL
                    OR LOWER(r.address.street) LIKE LOWER(CONCAT('%', CAST(:search as string), '%'))
                    OR LOWER(r.licensePlate) LIKE LOWER(CONCAT('%', CAST(:search as string), '%'))
                )
            """)
    Page<Report> findByFiltersCreatedBy(
        @Param("status") Status status, 
        @Param("user") User user, 
        @Param("search") String search, 
        Pageable pageable);
}