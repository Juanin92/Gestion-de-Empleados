package com.javaFX.fx.Entity.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EmployeeDTO {

    private int id;
    private String name;
    private String surname;
    private String email;
    private int income;
    private boolean status;
    private LocalDate creation;

}
