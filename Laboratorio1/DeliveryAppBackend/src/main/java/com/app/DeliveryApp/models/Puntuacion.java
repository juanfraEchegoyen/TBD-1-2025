package com.app.DeliveryApp.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Puntuacion {
    private Long idPuntuacion;
    private int puntaje;
    private String comentario;

    // FK
    private String rutRepartidor;
}
