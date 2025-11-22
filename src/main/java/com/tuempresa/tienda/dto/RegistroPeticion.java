package com.tuempresa.tienda.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistroPeticion {

    @NotBlank
    private String nombreUsuario;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
}