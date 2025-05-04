package com.app.DeliveryApp.models.sentenciasSQL;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepartidorTiempoPromedio {
    private String rutRepartidor;
    private String nombreRepartidor;
    private Double tiempoPromedio;
}
