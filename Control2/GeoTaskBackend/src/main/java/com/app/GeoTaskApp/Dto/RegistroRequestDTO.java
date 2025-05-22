package com.app.GeoTaskApp.Dto;

import com.app.GeoTaskApp.Models.Sector;
import com.app.GeoTaskApp.Models.Usuario;

public class RegistroRequestDTO {
    private String nombre;
    private String password;
    private String asignacion;
    private String comuna;
    private String calle;
    private String ubicacion;

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

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Sector toSector() {
        Sector sector = new Sector();
        sector.setAsignacion(this.asignacion != null ? this.asignacion : "usuario");
        sector.setComuna(this.comuna);
        sector.setCalle(this.calle);
        sector.setUbicacionWkt(this.ubicacion); // Use the conversion method in Sector
        return sector;
    }

    public Usuario toUsuario() {
        Usuario usuario = new Usuario();
        usuario.setNombre(this.nombre);
        usuario.setPassword(this.password);
        return usuario;
    }

}