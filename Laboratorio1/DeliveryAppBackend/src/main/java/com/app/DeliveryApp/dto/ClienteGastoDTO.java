package com.app.DeliveryApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteGastoDTO {
    private String nombreCliente;
    private Integer dineroGastado;

    public String getNombreCliente() {
        return nombreCliente;
    }

    public Integer getDineroGastado() {
        return dineroGastado;
    }
}
