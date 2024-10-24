package com.javaFX.fx.Entity.DTO;

import com.javaFX.fx.Entity.Departamentos;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DataDto {

    private int idEmployee;
    private Long idRegister;
    private String name;
    private String surname;
    private boolean status;
    private String branch;
    private Departamentos depto;
    private int income;
    private LocalDate entryDate;
    private LocalDate exitDate;
    private String calculateTime;
}
