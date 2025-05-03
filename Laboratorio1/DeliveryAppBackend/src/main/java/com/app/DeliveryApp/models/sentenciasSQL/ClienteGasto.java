package com.app.DeliveryApp.models.sentenciasSQL;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteGasto {
    private String nombre_cliente;
    private Integer dinero_gastado;
}
