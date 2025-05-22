package com.app.GeoTaskApp.Dto;

public class UsuarioRegistroDTO {
    private String nombre;
    private String password;
    private Long id_sector;

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public Long getId_sector() {
        return id_sector;
    }
    public void setId_sector(Long id_sector) {
        this.id_sector = id_sector;
    }
}
