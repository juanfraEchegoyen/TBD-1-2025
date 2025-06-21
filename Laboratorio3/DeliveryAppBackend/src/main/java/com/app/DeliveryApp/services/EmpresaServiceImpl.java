package com.app.DeliveryApp.services;

import com.app.DeliveryApp.dto.EmpresaNombreRutDTO;
import com.app.DeliveryApp.models.Empresa;
import com.app.DeliveryApp.repositories.EmpresaRepository;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EmpresaServiceImpl implements EmpresaService {

    private final EmpresaRepository empresaRepository;

    @Autowired
    public EmpresaServiceImpl(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    @Override
    public Empresa crearEmpresa(Empresa empresa) {
        if (empresaRepository.findByRut(empresa.getRut()).isPresent()) {
            throw new IllegalArgumentException("Empresa con RUT " + empresa.getRut() + " ya existe.");
        }
        return empresaRepository.save(empresa);
    }

    @Override
    public Optional<Empresa> obtenerEmpresaPorRut(String rut) {
        return empresaRepository.findByRut(rut);
    }

    @Override
    public List<Empresa> obtenerTodasLasEmpresas() {
        return empresaRepository.findAll();
    }    @Override
    public Optional<Empresa> actualizarEmpresa(String rut, Empresa empresaActualizada) {
        return empresaRepository.findByRut(rut).map(empresaExistente -> {
            empresaExistente.setNombre(empresaActualizada.getNombre());
            empresaExistente.setUbicacion(empresaActualizada.getUbicacion());
            empresaRepository.update(empresaExistente);
            return empresaExistente;
        });
    }

    @Override
    public boolean eliminarEmpresa(String rut) {
        if (empresaRepository.findByRut(rut).isPresent()) {
            int filasAfectadas = empresaRepository.deleteByRut(rut);
            return filasAfectadas > 0;        }
        return false;
    }

    @Override
    public List<EmpresaNombreRutDTO> ObtenerRutYnombres(){
        List<EmpresaNombreRutDTO> empresas = empresaRepository.ObtenerRutYnombres();
        return empresas;
    }

}