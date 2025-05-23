package com.app.DeliveryApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepartidorMejorRendimientoDTO {
    private String nombreRepartidor;
    private Double puntuacion;
    private Integer entregas;
}
