package com.tuempresa.tienda.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRespuesta {

    private String token;
    private String tipo = "Bearer";

    public LoginRespuesta(String accessToken) {
        this.token = accessToken;
    }
}