package com.app.GeoTaskApp.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTReader;


@AllArgsConstructor
@NoArgsConstructor
public class Usuario {
    private Integer idUsuario;
    private String nombre;
    private String password;
    private Geometry ubicacion;


    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Geometry getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(Geometry ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public void setUbicacionFromString(String ubicacionStr) throws Exception {
        WKTReader reader = new WKTReader();
        Geometry geom = reader.read(ubicacionStr);
        this.ubicacion = geom;
    }
}
