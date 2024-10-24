package com.javaFX.fx.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "registro")
@Data
public class Register {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String sucursal;

    @Enumerated(EnumType.STRING)
    private Departamentos departamento;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
}
