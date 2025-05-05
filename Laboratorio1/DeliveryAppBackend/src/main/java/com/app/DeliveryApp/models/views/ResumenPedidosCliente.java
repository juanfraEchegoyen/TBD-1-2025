package com.app.DeliveryApp.models.views;

import lombok.Data;

@Data
public class ResumenPedidosCliente {
    private String rutCliente;
    private String nombreCliente;
    private Integer cantidadPedidos;
    private Integer montoTotal;
}