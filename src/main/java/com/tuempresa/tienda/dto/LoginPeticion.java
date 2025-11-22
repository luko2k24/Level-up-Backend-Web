package com.tuempresa.tienda.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginPeticion {

    @NotBlank
    private String nombreUsuario;

    @NotBlank
    private String password;
}