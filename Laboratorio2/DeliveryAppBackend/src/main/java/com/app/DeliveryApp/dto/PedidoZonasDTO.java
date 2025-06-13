package com.app.DeliveryApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoZonasDTO {
    private int id_pedido;
    private String rut_repartidor;
    private int zonas_cruzadas;
}
