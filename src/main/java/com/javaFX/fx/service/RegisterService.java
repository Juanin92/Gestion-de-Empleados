package com.javaFX.fx.service;

import com.javaFX.fx.Entity.DTO.RegisterDTO;
import com.javaFX.fx.Entity.Employee;
import com.javaFX.fx.Entity.Register;
import com.javaFX.fx.repository.RegisterRepo;
import com.javaFX.fx.service.interfaces.IRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegisterService implements IRegisterService {

    @Autowired private RegisterRepo registerRepo;
    @Autowired private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void create(RegisterDTO registerDTO,Employee employee) {
        Register register = new Register();
        register.setDepartamento(registerDTO.getDepartamento());
        register.setSucursal(registerDTO.getSucursal());
        register.setUsername(registerDTO.getUsername());
        if (registerDTO.getPassword().equals(registerDTO.getPasswordConfirm())){
            register.setPassword(bCryptPasswordEncoder.encode(registerDTO.getPassword()));
        }else {
            throw new IllegalArgumentException("Clave no son iguales");
        }
        register.setEmployee(employee);
        registerRepo.save(register);
    }

    @Override
    public void editRegister(Register register) {
        registerRepo.save(register);
    }

    @Override
    public Register getById(Long id) {
        return registerRepo.findById(id).get();
    }

    @Override
    public List<Register> getAll() {
        return registerRepo.findAll();
    }

    @Override
    public List<Register> getEmployeeId(int id) {
        return registerRepo.findByEmployeeId(id);
    }
}
