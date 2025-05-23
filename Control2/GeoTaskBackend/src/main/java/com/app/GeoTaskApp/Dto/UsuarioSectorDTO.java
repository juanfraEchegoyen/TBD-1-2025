package com.app.GeoTaskApp.Dto;

public class UsuarioSectorDTO {
    private Long idUsuario;
    private Long idSector;
    private int cantidadTareas;

    public UsuarioSectorDTO(Long idUsuario, Long idSector, int cantidadTareas) {
        this.idUsuario = idUsuario;
        this.idSector = idSector;
        this.cantidadTareas = cantidadTareas;
    }
    public Long getIdUsuario() {
        return idUsuario;
    }
    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
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
