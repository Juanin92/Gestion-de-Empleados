package com.javaFX.fx.service;

import com.javaFX.fx.Entity.DTO.EmployeeDTO;
import com.javaFX.fx.Entity.Employee;
import com.javaFX.fx.repository.EmployeeRepo;
import com.javaFX.fx.service.interfaces.IEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmployeeService implements IEmployeeService {

    @Autowired private EmployeeRepo employeeRepo;

    @Override
    public void createEmployee(EmployeeDTO employeeDTO) {
        if (!employeeDTO.getName().trim().isEmpty()
                && !employeeDTO.getSurname().trim().isEmpty() && !employeeDTO.getEmail().trim().isEmpty()){

            Employee employee = new Employee();
            employee.setName(employeeDTO.getName());
            employee.setSurname(employeeDTO.getSurname());
            employee.setEmail(employeeDTO.getEmail());

            if (employeeDTO.getIncome() < 500000){
                throw  new IllegalArgumentException("Sueldo debe ser mayor a sueldo mínimo");
            }else {
                employee.setIncome(employeeDTO.getIncome());
            }

            employee.setStatus(true);
            employee.setEntryDate(LocalDate.now());
            employeeRepo.save(employee);

            throw  new IllegalArgumentException("Empleado creado exitosamente");
        }else {
            throw new IllegalArgumentException("Faltan datos del empleado");
        }
    }

    @Override
    public void eliminateEmployee(Employee employee) {
        employee.setStatus(false);
        employee.setExitDate(LocalDate.now());
        employeeRepo.save(employee);
    }

    @Override
    public void updateEmployee(Employee employee){
        employeeRepo.save(employee);
    }

    @Override
    public Employee getById(int id) {
        return employeeRepo.findById(id).orElse(null);
    }

    @Override
    public List<Employee> getAll() {
        return employeeRepo.findAll();
    }

    @Override
    public EmployeeDTO selectedIdUser(int idUsuario) {
        Employee employee = employeeRepo.findById(idUsuario).orElse(null);
        if (employee != null) {
            EmployeeDTO employeeDTO = new EmployeeDTO();
            employeeDTO.setId(employee.getId());
            employeeDTO.setName(employee.getName());
            employeeDTO.setEmail(employee.getEmail());
            return employeeDTO;
        } else {
            return null;
        }
    }
}
