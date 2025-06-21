package com.app.DeliveryApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoMasVendidoDTO {
    private String categoria;
    private String nombreProducto;
    private Integer totalPedidos;
}
