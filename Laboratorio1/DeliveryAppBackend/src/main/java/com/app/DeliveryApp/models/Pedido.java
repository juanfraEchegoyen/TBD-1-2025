package com.app.DeliveryApp.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pedido {
    private Long idPedido;
    private String estadoEntrega;
    private String prioridadPedido;

    // FK
    private String rutCliente;
    private String rutEmpresa;
    private String rutRepartidor;

}
