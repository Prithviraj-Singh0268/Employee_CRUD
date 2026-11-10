package com.erpProject.teamforge.repository;

import com.erpProject.teamforge.entity.LeaveRequest;
import com.erpProject.teamforge.entity.LeaveStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    boolean existsByEmployeeId(Long employeeId);

    // Two date ranges overlap when each one starts before the other ends
    @Query("""
        SELECT COUNT(l) FROM LeaveRequest l
        WHERE l.employee.id = :employeeId
          AND l.status IN :statuses
          AND l.startDate <= :endDate
          AND l.endDate >= :startDate
        """)
    long countOverlapping(@Param("employeeId") Long employeeId,
                          @Param("statuses") List<LeaveStatus> statuses,
                          @Param("startDate") LocalDate startDate,
                          @Param("endDate") LocalDate endDate);

    @Query("""
        SELECT l FROM LeaveRequest l
        WHERE (:employeeId IS NULL OR l.employee.id = :employeeId)
          AND (:status IS NULL OR l.status = :status)
        """)
    Page<LeaveRequest> search(@Param("employeeId") Long employeeId,
                              @Param("status") LeaveStatus status,
                              Pageable pageable);
}