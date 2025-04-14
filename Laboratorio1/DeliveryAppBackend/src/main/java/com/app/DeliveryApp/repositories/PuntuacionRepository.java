package com.app.DeliveryApp.repositories;

import com.app.DeliveryApp.models.Puntuacion;
import java.util.List;
import java.util.Optional;

public interface PuntuacionRepository {
    Puntuacion save(Puntuacion puntuacion); // Devuelve con ID
    Optional<Puntuacion> findById(Long id); // PK es ID (Long)
    List<Puntuacion> findAll();
    int update(Puntuacion puntuacion);
    int deleteById(Long id); // PK es ID (Long)

    List<Puntuacion> findByRutRepartidor(String rutRepartidor);
}