package com.app.DeliveryApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetodoPagoFrecuenteDTO {
    private String nombreMedioDePago;
    private int cantidadUsos;
}
