package com.app.DeliveryApp.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

@Data
@NoArgsConstructor
@AllArgsConstructor

// para la consulta " Crear una tabla de puntos de interés cercanos"

public class PuntoInteres {    private Long idPunto;
    private String nombre;  
    private String tipo;  
    private Point ubicacion;  
    private String descripcion;
    private Boolean activo;
}
