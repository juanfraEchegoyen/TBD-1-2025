package com.app.DeliveryApp.repositories;

import com.app.DeliveryApp.dto.EmpresaNombreRutDTO;
import com.app.DeliveryApp.models.Empresa;
import java.util.List;
import java.util.Optional;

public interface EmpresaRepository {
    Empresa save(Empresa empresa);
    Optional<Empresa> findByRut(String rut); 
    List<Empresa> findAll();
    int update(Empresa empresa);
    int deleteByRut(String rut);
    public List<EmpresaNombreRutDTO> ObtenerRutYnombres();

}