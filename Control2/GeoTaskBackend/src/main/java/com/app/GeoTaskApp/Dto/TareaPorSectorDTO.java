package com.app.GeoTaskApp.Dto;

public class TareaPorSectorDTO {
    private String nombre;
    private Long idSector;
    private int cantidadTareas;

    public TareaPorSectorDTO(Long idSector, int cantidadTareas) {
        this.idSector = idSector;
        this.cantidadTareas = cantidadTareas;
    }

    public TareaPorSectorDTO(String nombre,Long idSector) {
        this.nombre = nombre;
        this.idSector = idSector;

    }

    public TareaPorSectorDTO(String nombre, Long idSector, int cantidadTareas) {
        this.nombre = nombre;
        this.idSector = idSector;
        this.cantidadTareas = cantidadTareas;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getIdSector() {
        return idSector;
    }

    public void setIdSector(Long idSector) {
        this.idSector = idSector;
    }

    public int getCantidadTareas() {
        return cantidadTareas;
    }

    public void setCantidadTareas(int cantidadTareas) {
        this.cantidadTareas = cantidadTareas;
    }
}