package com.app.DeliveryApp.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Producto {
    private Long idProducto;
    private String nombre;
    private Double precio;
    private String categoria;
    private String tipoProducto;
    private int stock;
}
