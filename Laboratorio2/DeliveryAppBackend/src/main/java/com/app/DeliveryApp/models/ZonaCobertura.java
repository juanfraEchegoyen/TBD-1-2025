package com.app.DeliveryApp.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Polygon;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZonaCobertura {
    private Long idZona;
    private String nombreZona;
    private String descripcion;
    private Polygon areaCobertura; 
    private Boolean activa;
    private LocalDateTime fechaCreacion;
}
