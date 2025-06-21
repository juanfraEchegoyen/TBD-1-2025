package com.app.DeliveryApp.services;

import com.app.DeliveryApp.models.Puntuacion;
import java.util.List;
import java.util.Optional;

public interface PuntuacionService {
    Puntuacion crearPuntuacion(Puntuacion puntuacion);
    Optional<Puntuacion> obtenerPuntuacionPorId(Long id);
    List<Puntuacion> obtenerTodasLasPuntuaciones();
    List<Puntuacion> obtenerPuntuacionesPorRepartidor(String rutRepartidor);
    Optional<Puntuacion> actualizarPuntuacion(Long id, Puntuacion puntuacionActualizada);
    boolean eliminarPuntuacion(Long id);
}