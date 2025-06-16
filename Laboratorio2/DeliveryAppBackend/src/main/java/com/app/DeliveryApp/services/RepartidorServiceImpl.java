package com.app.DeliveryApp.services;

import com.app.DeliveryApp.dto.RepartidorRutNombreDTO;
import com.app.DeliveryApp.models.Repartidor;
import com.app.DeliveryApp.repositories.RepartidorRepository;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RepartidorServiceImpl implements RepartidorService {

    private final RepartidorRepository repartidorRepository;

    @Autowired
    public RepartidorServiceImpl(RepartidorRepository repartidorRepository) {
        this.repartidorRepository = repartidorRepository;
    }

    @Override
    public Repartidor crearRepartidor(Repartidor repartidor) {
        if (repartidorRepository.findByRut(repartidor.getRut()).isPresent()) {
            throw new IllegalArgumentException("Repartidor con RUT " + repartidor.getRut() + " ya existente");
        }
        return repartidorRepository.save(repartidor);
    }

    @Override
    public Optional<Repartidor> obtenerRepartidorPorRut(String rut) {
        return repartidorRepository.findByRut(rut);
    }

    @Override
    public List<Repartidor> obtenerTodosLosRepartidores() {
        return repartidorRepository.findAll();
    }    @Override
    public Optional<Repartidor> actualizarRepartidor(String rut, Repartidor repartidorActualizado) {
        return repartidorRepository.findByRut(rut).map(repartidorExistente -> {
            repartidorExistente.setNombre(repartidorActualizado.getNombre());
            repartidorExistente.setTelefono(repartidorActualizado.getTelefono());
            repartidorExistente.setUbicacion(repartidorActualizado.getUbicacion());
            repartidorExistente.setDistanciaRecorrida(repartidorActualizado.getDistanciaRecorrida());
            // puntuacion y cantidad se calculan por otros procesos, no se pueden actualizar
            repartidorRepository.update(repartidorExistente);
            return repartidorExistente;
        });
    }

    @Override
    public boolean eliminarRepartidor(String rut) {
        if (repartidorRepository.findByRut(rut).isPresent()) {
            int filasAfectadas = repartidorRepository.deleteByRut(rut);
            return filasAfectadas > 0;        }
        return false;
    }
    
    @Override
    public Optional<Repartidor> actualizarUbicacionRepartidor(String rut, Point nuevaUbicacion) {
        Optional<Repartidor> repartidorOpt = repartidorRepository.findByRut(rut);
        if (repartidorOpt.isPresent()) {
            Repartidor repartidor = repartidorOpt.get();
            repartidor.setUbicacion(nuevaUbicacion);
            repartidorRepository.update(repartidor);
            return Optional.of(repartidor);
        }
        
        return Optional.empty();
    }
    
    @Override
    public Optional<Repartidor> actualizarDistanciaRecorrida(String rut, Double nuevaDistancia) {
        Optional<Repartidor> repartidorOpt = repartidorRepository.findByRut(rut);
        if (repartidorOpt.isPresent()) {
            Repartidor repartidor = repartidorOpt.get();
            repartidor.setDistanciaRecorrida(nuevaDistancia);
            repartidorRepository.update(repartidor);
            return Optional.of(repartidor);
        }
        return Optional.empty();
    }
    @Override
    public List<RepartidorRutNombreDTO> ObtenerRutYnombresRepartidor(){
        List<RepartidorRutNombreDTO> repartidores = repartidorRepository.ObtenerRutYnombresRepartidor();
        return repartidores;
    }
}