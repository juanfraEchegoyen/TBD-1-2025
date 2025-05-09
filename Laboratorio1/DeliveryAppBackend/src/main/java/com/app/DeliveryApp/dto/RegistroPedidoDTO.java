package com.app.DeliveryApp.dto;

import lombok.Data;

@Data

public class RegistroPedidoDTO {
    // De Pedido
    private String prioridadPedido;
    private String rutCliente;

    // De DetallePedido
    private int cantidad;
    private Long idProducto;




}
