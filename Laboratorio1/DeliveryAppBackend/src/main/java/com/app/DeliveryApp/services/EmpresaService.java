package com.app.DeliveryApp.services;

import com.app.DeliveryApp.models.Empresa;
import java.util.List;
import java.util.Optional;

public interface EmpresaService {
    Empresa crearEmpresa(Empresa empresa);
    Optional<Empresa> obtenerEmpresaPorRut(String rut);
    List<Empresa> obtenerTodasLasEmpresas();
    Optional<Empresa> actualizarEmpresa(String rut, Empresa empresaActualizada);
    boolean eliminarEmpresa(String rut);
}