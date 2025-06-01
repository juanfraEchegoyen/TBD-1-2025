package com.app.DeliveryApp.repositories;

import com.app.DeliveryApp.models.PuntoInteres;

import java.util.List;
import java.util.Optional;

public interface PuntoInteresRepository {
    PuntoInteres save(PuntoInteres puntoInteres);
    Optional<PuntoInteres> findById(Long id);
    List<PuntoInteres> findAll();
    int update(PuntoInteres puntoInteres);
    int deleteById(Long id);
    List<PuntoInteres> findByTipo(String tipo);
}
