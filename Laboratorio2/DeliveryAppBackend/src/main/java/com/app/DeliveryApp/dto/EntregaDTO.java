package com.app.DeliveryApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntregaDTO {
        private String rutCliente;
        private int idPedido;
        private String distanciaLinea; // Almacena la geometría como texto WKT
}
