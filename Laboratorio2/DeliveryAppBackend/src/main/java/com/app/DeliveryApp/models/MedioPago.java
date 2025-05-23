package com.app.DeliveryApp.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedioPago {
    private Long idMedioPago;
    private String nombreMedioPago;

    // FK
    private String rutCliente;
    private Long idPedido; // FK agregada
}
