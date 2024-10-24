package com.javaFX.fx.service.interfaces;


import com.javaFX.fx.Entity.DTO.EmployeeDTO;
import com.javaFX.fx.Entity.Employee;

import java.util.List;

public interface IEmployeeService {

    void createEmployee(EmployeeDTO employeeDTO);
    void eliminateEmployee(Employee employee);
    void updateEmployee(Employee employee);
    Employee getById(int id);
    List<Employee> getAll();
    EmployeeDTO selectedIdUser(int Idusuario);
}
