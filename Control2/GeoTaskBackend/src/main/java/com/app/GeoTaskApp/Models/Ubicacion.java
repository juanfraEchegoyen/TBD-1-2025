package com.app.GeoTaskApp.Models;

public class Ubicacion {
    private Long idUbicacion;
    private String region;
    private String provincia;
    private String comuna;
    private String coordenadas;

    public Long getIdUbicacion() {
        return idUbicacion;
    }

    public void setIdUbicacion(Long idUbicacion) {
        this.idUbicacion = idUbicacion;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getComuna() {
        return comuna;
    }

    public void setComuna(String comuna) {
        this.comuna = comuna;
    }

    public String getCoordenadas() {
        return coordenadas;
    }

    public void setCoordenadas(String coordenadas) {
        this.coordenadas = coordenadas;
    }

}