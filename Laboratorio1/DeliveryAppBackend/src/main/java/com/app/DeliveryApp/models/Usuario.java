package com.app.DeliveryApp.models;

import lombok.Data;

@Data
public class Usuario {
    private Integer idUsuario;
    private String nombre;
    private String email;
    private String password;
}
