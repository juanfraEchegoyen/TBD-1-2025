package com.app.GeoTaskApp.Dto;

public class DistanciaPromedioDTO {
    private String nombre;
    private double promedioDistancia;

    public DistanciaPromedioDTO(String nombre,double promedioDistancia) {
        this.nombre = nombre;
        this.promedioDistancia = promedioDistancia;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public double getPromedioDistancia() {
        return promedioDistancia;
    }
    public void setPromedioDistancia(double promedioDistancia) {
        this.promedioDistancia = promedioDistancia;
    }
}
