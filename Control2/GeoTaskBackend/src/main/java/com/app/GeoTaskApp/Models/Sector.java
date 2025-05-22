package com.app.GeoTaskApp.Models;

import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;

public class Sector {
    private Long idSector;
    private String asignacion;
    private String comuna;
    private String calle;
    private Point ubicacion;

    public Long getIdSector() {
        return idSector;
    }

    public void setIdSector(Long idSector) {
        this.idSector = idSector;
    }

    public String getAsignacion() {
        return asignacion;
    }

    public void setAsignacion(String asignacion) {
        this.asignacion = asignacion;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getComuna() {
        return comuna;
    }

    public void setComuna(String comuna) {
        this.comuna = comuna;
    }

    public Point getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(Point ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getUbicacionWkt() {
        if (ubicacion == null) return null;
        return new WKTWriter().write(ubicacion);
    }

    public void setUbicacionWkt(String wkt) {
        if (wkt == null || wkt.isEmpty()) return;
        try {
            this.ubicacion = (Point) new WKTReader().read(wkt);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Formato de coordenada inválido: " + wkt);
        }
    }
}