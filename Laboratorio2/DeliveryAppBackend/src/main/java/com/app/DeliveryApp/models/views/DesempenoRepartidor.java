package com.app.DeliveryApp.models.views;

import lombok.Data;
@Data
public class DesempenoRepartidor {
    private String rutRepartidor;
    private String nombreRepartidor;
    private int entregasRealizadas;
    private double puntuacionPromedio;
}
