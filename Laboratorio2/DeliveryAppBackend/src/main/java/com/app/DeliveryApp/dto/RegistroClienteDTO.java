package com.app.DeliveryApp.dto;

import com.app.DeliveryApp.models.Cliente;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKTReader;

public class RegistroClienteDTO {
    private String rut;
    private String nombre;
    private String telefono;
    private String password;
    private String direccion;
    private String comuna;
    private String ubicacion; // WKT format
    
    // Getters y setters
    public String getRut() { return rut; }
    public void setRut(String rut) { this.rut = rut; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    
    public String getComuna() { return comuna; }
    public void setComuna(String comuna) { this.comuna = comuna; }
    
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    
    public Cliente toCliente() {
        Cliente cliente = new Cliente();
        cliente.setRut(this.rut);
        cliente.setNombre(this.nombre);
        cliente.setTelefono(this.telefono);
        cliente.setPassword(this.password);
        cliente.setDireccion(this.direccion);
        cliente.setComuna(this.comuna);
        
        // Convertir WKT a Point usando JTS
        if (this.ubicacion != null && !this.ubicacion.trim().isEmpty()) {
            try {
                WKTReader wktReader = new WKTReader();
                Point point = (Point) wktReader.read(this.ubicacion);
                point.setSRID(4326);
                cliente.setUbicacion(point);
            } catch (Exception e) {
                throw new RuntimeException("Error al procesar ubicación: " + e.getMessage());
            }
        }
        
        return cliente;
    }
}