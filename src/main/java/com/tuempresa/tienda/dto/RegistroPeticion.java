package com.tuempresa.tienda.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull; // Importación requerida
import jakarta.validation.constraints.Min;     // Importación requerida
import jakarta.validation.constraints.Size;   // Importación requerida
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistroPeticion {

    // Asumimos que el nombre de usuario debe ser de al menos 4 caracteres
    @NotBlank(message = "El nombre de usuario es obligatorio.")
    @Size(min = 4, message = "El nombre de usuario debe tener al menos 4 caracteres.")
    private String nombreUsuario;

    @NotBlank(message = "El correo electrónico es obligatorio.")
    @Email(message = "El correo electrónico debe ser válido.")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria.")
    private String password;

    @NotBlank(message = "El nombre completo es obligatorio.")
    private String nombreCompleto;

    @NotNull(message = "La edad es obligatoria.")
    @Min(value = 18, message = "Debe ser mayor de edad.")
    private Integer edad;

    @NotBlank(message = "La región es obligatoria.")
    private String region;

    @NotBlank(message = "La comuna es obligatoria.")
    private String comuna;
}