package com.app.DeliveryApp.repositories;

import com.app.DeliveryApp.models.PuntoInteres;
import org.locationtech.jts.geom.Point;

import java.util.List;
import java.util.Optional;

public interface PuntoInteresRepository {
    PuntoInteres save(PuntoInteres puntoInteres);
    Optional<PuntoInteres> findById(Long id);
    List<PuntoInteres> findAll();
    int update(PuntoInteres puntoInteres);
    int deleteById(Long id);
    List<PuntoInteres> findByTipo(String tipo);
    List<PuntoInteres> findPuntosInteresEnRadio(Point centro, double radioMetros);
    PuntoInteres findPuntoInteresmasCercano(Point ubicacion, String tipo);
    List<PuntoInteres> findByDistanceWithin(Point punto, double distanciaMaxima);
    Optional<PuntoInteres> findNearestByType(Point punto, String tipo);
}
