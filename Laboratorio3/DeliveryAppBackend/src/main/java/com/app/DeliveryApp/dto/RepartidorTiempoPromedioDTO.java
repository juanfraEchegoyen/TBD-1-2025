package com.app.DeliveryApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepartidorTiempoPromedioDTO {
    private String rutRepartidor;
    private String nombreRepartidor;
    private Double tiempoPromedio;
}
