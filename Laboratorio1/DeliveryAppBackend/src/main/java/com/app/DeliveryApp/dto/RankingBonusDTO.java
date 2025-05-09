package com.app.DeliveryApp.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class RankingBonusDTO {
    private String nombre_producto;
    private String categoria;
    private int devoluciones;
    private int cancelaciones;
    private int total_problemas;


    public String getProducto() {
        return nombre_producto;
    }

    public int getProblemas() {
        return total_problemas;
    }

    public int getCancelaciones() {
        return cancelaciones;
    }

    public int getDevoluciones() {
        return devoluciones;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setProducto(String producto) {
        this.nombre_producto = producto;
    }

    public void setProblemas(int problemas) {
        this.total_problemas = problemas;
    }

    public void setCancelaciones(int cancelaciones) {
        this.cancelaciones = cancelaciones;
    }

    public void setDevoluciones(int devoluciones) {
        this.devoluciones = devoluciones;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}

