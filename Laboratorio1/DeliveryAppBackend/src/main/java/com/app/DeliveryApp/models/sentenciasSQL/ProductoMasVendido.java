package com.app.DeliveryApp.models.sentenciasSQL;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoMasVendido {
    private String categoria;
    private String nombreProducto;
    private Integer totalPedidos;
}
