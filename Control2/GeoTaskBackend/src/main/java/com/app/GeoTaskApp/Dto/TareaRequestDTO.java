package com.app.GeoTaskApp.Dto;

import com.app.GeoTaskApp.Models.Sector;
import com.app.GeoTaskApp.Models.Tarea;

import java.util.Date;

public class TareaRequestDTO {
    private String titulo;
    private String descripcion;
    private Date fechaVencimiento;
    private String estado;
    private String asignacion;
    private String comuna;
    private String calle;
    private String ubicacion;
    private Long idUsuario;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
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
    public Long getIdUsuario() {
        return idUsuario;
    }
    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Sector toSector() {
        Sector sector = new Sector();
        sector.setAsignacion(this.asignacion != null ? this.asignacion : "usuario");
        sector.setComuna(this.comuna);
        sector.setCalle(this.calle);
        sector.setUbicacionWkt(this.ubicacion); // Use the conversion method in Sector
        return sector;
    }

    public Tarea toTarea() {
        Tarea tarea = new Tarea();
        tarea.setTitulo(this.titulo);
        tarea.setDescripcion(this.descripcion);
        tarea.setFechaVencimiento(this.fechaVencimiento);
        tarea.setEstado(this.estado);
        tarea.setIdUsuario(this.idUsuario);
        return tarea;
    }
}
