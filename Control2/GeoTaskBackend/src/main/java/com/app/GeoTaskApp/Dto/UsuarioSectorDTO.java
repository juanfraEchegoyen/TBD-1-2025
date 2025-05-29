package com.app.GeoTaskApp.Dto;

public class UsuarioSectorDTO {
    private String nombre;
    private Long idUsuario;
    private Long idSector;
    private int cantidadTareas;

    public UsuarioSectorDTO(String nombre,Long idUsuario, Long idSector, int cantidadTareas) {
        this.nombre = nombre;
        this.idUsuario = idUsuario;
        this.idSector = idSector;
        this.cantidadTareas = cantidadTareas;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
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
