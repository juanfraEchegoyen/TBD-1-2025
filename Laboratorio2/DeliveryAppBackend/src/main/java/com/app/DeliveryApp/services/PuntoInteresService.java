package com.app.DeliveryApp.services;

import com.app.DeliveryApp.models.PuntoInteres;
import org.locationtech.jts.geom.Point;
import java.util.List;
import java.util.Optional;

public interface PuntoInteresService {
    PuntoInteres crearPuntoInteres(PuntoInteres puntoInteres);
    Optional<PuntoInteres> obtenerPuntoInteresPorId(Long id);
    List<PuntoInteres> obtenerTodosLosPuntosInteres();
    List<PuntoInteres> obtenerPuntosInteresPorTipo(String tipo);
    Optional<PuntoInteres> actualizarPuntoInteres(Long id, PuntoInteres puntoActualizado);
    boolean eliminarPuntoInteres(Long id);
    
    // Métodos geoespaciales
    List<PuntoInteres> obtenerPuntosInteresEnRadio(Point centro, double radioMetros);
    PuntoInteres obtenerPuntoInteresmasCercano(Point ubicacion, String tipo);
}
