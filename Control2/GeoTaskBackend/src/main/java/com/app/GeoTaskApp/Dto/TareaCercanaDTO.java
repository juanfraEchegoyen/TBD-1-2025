package com.app.GeoTaskApp.Dto;

public class TareaCercanaDTO {
    private String nombre;
    private Long idTarea;
    private String titulo;
    private double distancia;

    public TareaCercanaDTO(String nombre,Long idTarea, String titulo, double distancia) {
        this.nombre = nombre;
        this.idTarea = idTarea;
        this.titulo = titulo;
        this.distancia = distancia;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public Long getIdTarea() {
        return idTarea;
    }
    public void setIdTarea(Long idTarea) {
        this.idTarea = idTarea;
    }
    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    public double getDistancia() {
        return distancia;
    }
    public void setDistancia(double distancia) {
        this.distancia = distancia;
    }
}
