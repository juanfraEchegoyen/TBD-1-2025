package com.app.DeliveryApp.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZonaCobertura {
    private Long idZona;
    private String nombreComuna;
    private String nombreZona;
    private String descripcion;
    private String rutEmpresa;
    private MultiPolygon areaCobertura;
    private Boolean activa;
    private LocalDateTime fechaCreacion;
    
    public String getNombre() {
        return this.nombreZona;
    }
    public void setNombre(String nombre) {
        this.nombreZona = nombre;
    }
}
