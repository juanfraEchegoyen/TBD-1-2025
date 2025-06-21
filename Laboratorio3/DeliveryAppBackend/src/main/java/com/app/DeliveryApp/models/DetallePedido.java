package com.app.DeliveryApp.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedido {
    private Long idDetalle;
    private Double precioTotal;
    private Integer tiempoEntrega;
    private Date fechaEntrega;
    private int cantidad;

    // FK
    private Long idPedido;
    private Long idProducto;


}
