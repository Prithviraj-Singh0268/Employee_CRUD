package com.erpProject.teamforge.repository;

import com.erpProject.teamforge.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByEmail(String email);
    
    boolean existsByJobRoleId(Long jobRoleId);

    boolean existsByDepartmentId(Long departmentId);

    @Query("""
        SELECT e FROM Employee e
        WHERE (LOWER(e.firstName) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(e.lastName)  LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(e.email)     LIKE LOWER(CONCAT('%', :keyword, '%')))
          AND (:departmentId IS NULL OR e.department.id = :departmentId)
        """)
    Page<Employee> search(@Param("keyword") String keyword,
                          @Param("departmentId") Long departmentId,
                          Pageable pageable);
}