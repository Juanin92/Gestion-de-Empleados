package com.javaFX.fx.repository;

import com.javaFX.fx.Entity.Register;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegisterRepo extends JpaRepository<Register,Long> {

    @Query("SELECT r FROM Register r WHERE r.employee.id = :employeeId")
    List<Register> findByEmployeeId(@Param("employeeId")int id);
}
