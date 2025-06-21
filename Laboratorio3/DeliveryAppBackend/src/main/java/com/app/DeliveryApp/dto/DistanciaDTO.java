package com.app.DeliveryApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DistanciaDTO {
        private String rutCliente;
        private int idpedido;
        private double distanciaRecorrida;
}
