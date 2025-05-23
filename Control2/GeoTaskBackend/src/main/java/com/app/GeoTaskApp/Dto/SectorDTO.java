package com.app.GeoTaskApp.Dto;

public class SectorDTO {
    private Long idSector;
    private String asignacion;
    private String comuna;
    private String calle;
    private double longitud;
    private double latitud;

    // Constructores
    public SectorDTO() {}

    public SectorDTO(Long idSector, String asignacion, String comuna, String calle,
                     double longitud, double latitud) {
        this.idSector = idSector;
        this.asignacion = asignacion;
        this.comuna = comuna;
        this.calle = calle;
        this.longitud = longitud;
        this.latitud = latitud;
    }

    // Getters y setters
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

    public String getComuna() {
        return comuna;
    }

    public void setComuna(String comuna) {
        this.comuna = comuna;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }
}