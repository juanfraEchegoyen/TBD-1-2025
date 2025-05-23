package com.app.DeliveryApp.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Repartidor {
    private String rut;
    private String password;
    private String nombre;
    private String telefono;
    private Double puntuacionPromedio;
    private Integer cantidadEntregas;
}