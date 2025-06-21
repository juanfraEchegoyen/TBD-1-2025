package com.app.DeliveryApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntregaLejanaDTO {
    private String rutEmpresa;
    private String nombreEmpresa;
    private String rutCliente;
    private String nombreCliente;
    private String ubicacionClienteWkt;
    private String ubicacionEmpresaWkt;
    private int idPedido;
    private double distancia;
}