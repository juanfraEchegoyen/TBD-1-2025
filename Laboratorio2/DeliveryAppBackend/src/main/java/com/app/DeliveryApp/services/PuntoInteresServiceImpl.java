package com.app.DeliveryApp.services;

import com.app.DeliveryApp.models.PuntoInteres;
import com.app.DeliveryApp.repositories.PuntoInteresRepository;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PuntoInteresServiceImpl implements PuntoInteresService {

    private final PuntoInteresRepository puntoInteresRepository;

    @Autowired
    public PuntoInteresServiceImpl(PuntoInteresRepository puntoInteresRepository) {
        this.puntoInteresRepository = puntoInteresRepository;
    }

    @Override
    public PuntoInteres crearPuntoInteres(PuntoInteres puntoInteres) {
        return puntoInteresRepository.save(puntoInteres);
    }

    @Override
    public Optional<PuntoInteres> obtenerPuntoInteresPorId(Long id) {
        return puntoInteresRepository.findById(id);
    }

    @Override
    public List<PuntoInteres> obtenerTodosLosPuntosInteres() {
        return puntoInteresRepository.findAll();
    }

    @Override
    public List<PuntoInteres> obtenerPuntosInteresPorTipo(String tipo) {
        return puntoInteresRepository.findByTipo(tipo);
    }

    @Override
    public Optional<PuntoInteres> actualizarPuntoInteres(Long id, PuntoInteres puntoActualizado) {
        Optional<PuntoInteres> puntoExistenteOpt = puntoInteresRepository.findById(id);

        if (puntoExistenteOpt.isPresent()) {
            PuntoInteres puntoParaActualizar = puntoExistenteOpt.get();
            puntoParaActualizar.setNombre(puntoActualizado.getNombre());
            puntoParaActualizar.setTipo(puntoActualizado.getTipo());
            puntoParaActualizar.setDescripcion(puntoActualizado.getDescripcion());
            puntoParaActualizar.setUbicacion(puntoActualizado.getUbicacion());

            puntoInteresRepository.update(puntoParaActualizar);
            return Optional.of(puntoParaActualizar);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public boolean eliminarPuntoInteres(Long id) {
        if (puntoInteresRepository.findById(id).isPresent()) {
            int filasAfectadas = puntoInteresRepository.deleteById(id);
            return filasAfectadas > 0;
        } else {
            return false;
        }
    }

    @Override
    public List<PuntoInteres> obtenerPuntosInteresEnRadio(Point centro, double radioMetros) {
        // Este método requerirá una implementación específica en el repositorio con consultas espaciales
        // Por ahora retornamos una lista vacía, se implementará cuando se añadan las consultas espaciales
        return List.of();
    }

    @Override
    public PuntoInteres obtenerPuntoInteresmasCercano(Point ubicacion, String tipo) {
        // Este método requerirá una implementación específica en el repositorio con consultas espaciales
        // Por ahora retornamos null, se implementará cuando se añadan las consultas espaciales
        return null;
    }
}
