package com.app.GeoTaskApp.Dto;

public class TareaPorSectorDTO {
    private Long idSector;
    private int cantidadTareas;

    public TareaPorSectorDTO(Long idSector, int cantidadTareas) {
        this.idSector = idSector;
        this.cantidadTareas = cantidadTareas;
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