package com.app.DeliveryApp.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.LineString;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pedido {
    private Long idPedido;
    private String estadoEntrega;
    private String prioridadPedido;
    private boolean problemaCritico;
    private LineString rutasEstimadas;  // Campo geoespacial para ruta estimada (geometry(LineString, 4326))

    // FK
    private String rutCliente;
    private String rutEmpresa;
    private String rutRepartidor;

}
