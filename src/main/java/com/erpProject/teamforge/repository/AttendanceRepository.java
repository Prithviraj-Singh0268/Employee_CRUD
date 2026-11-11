package com.erpProject.teamforge.repository;

import com.erpProject.teamforge.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByEmployeeIdAndWorkDate(Long employeeId, LocalDate workDate);

    boolean existsByEmployeeId(Long employeeId);

    @Query("""
        SELECT a FROM Attendance a
        WHERE (:employeeId IS NULL OR a.employee.id = :employeeId)
          AND a.workDate >= :startDate
          AND a.workDate <= :endDate
        """)
    Page<Attendance> search(@Param("employeeId") Long employeeId,
                            @Param("startDate") LocalDate startDate,
                            @Param("endDate") LocalDate endDate,
                            Pageable pageable);
}