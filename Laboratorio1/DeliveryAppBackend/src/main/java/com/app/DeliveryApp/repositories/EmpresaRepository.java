package com.app.DeliveryApp.repositories;

import com.app.DeliveryApp.models.Empresa;
import java.util.List;
import java.util.Optional;

public interface EmpresaRepository {
    Empresa save(Empresa empresa);
    Optional<Empresa> findByRut(String rut); // PK es RUT
    List<Empresa> findAll();
    int update(Empresa empresa);
    int deleteByRut(String rut); // PK es RUT

}