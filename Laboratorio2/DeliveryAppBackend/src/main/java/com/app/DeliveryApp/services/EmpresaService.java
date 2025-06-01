package com.app.DeliveryApp.services;

import com.app.DeliveryApp.models.Empresa;
import org.locationtech.jts.geom.Point;
import java.util.List;
import java.util.Optional;

public interface EmpresaService {
    Empresa crearEmpresa(Empresa empresa);
    Optional<Empresa> obtenerEmpresaPorRut(String rut);
    List<Empresa> obtenerTodasLasEmpresas();
    Optional<Empresa> actualizarEmpresa(String rut, Empresa empresaActualizada);
    boolean eliminarEmpresa(String rut);
    
    Optional<Empresa> actualizarUbicacionEmpresa(String rut, Point nuevaUbicacion);
    List<Empresa> obtenerEmpresasEnRadio(Point centro, double radioMetros);
    Empresa obtenerEmpresaMasCercana(Point ubicacion);
}