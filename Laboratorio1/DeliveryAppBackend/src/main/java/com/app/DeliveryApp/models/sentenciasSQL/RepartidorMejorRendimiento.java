package com.app.DeliveryApp.models.sentenciasSQL;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepartidorMejorRendimiento {
    private String nombreRepartidor;
    private Double puntuacion;
    private Integer entregas;
}
