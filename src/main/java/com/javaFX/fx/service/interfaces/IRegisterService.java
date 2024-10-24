package com.javaFX.fx.service.interfaces;

import com.javaFX.fx.Entity.DTO.RegisterDTO;
import com.javaFX.fx.Entity.Employee;
import com.javaFX.fx.Entity.Register;

import java.util.List;

public interface IRegisterService {

    void create(RegisterDTO registerDTO, Employee employee);
    void editRegister(Register register);
    Register getById(Long id);
    List<Register> getAll();
    List<Register> getEmployeeId(int id);
}
