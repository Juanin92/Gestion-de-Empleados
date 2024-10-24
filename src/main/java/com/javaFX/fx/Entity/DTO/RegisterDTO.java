package com.javaFX.fx.Entity.DTO;

import com.javaFX.fx.Entity.Departamentos;
import lombok.Data;

@Data
public class RegisterDTO {

    private Long id;
    private String username;
    private String password;
    private String passwordConfirm;
    private String sucursal;
    private Departamentos departamento;
}
