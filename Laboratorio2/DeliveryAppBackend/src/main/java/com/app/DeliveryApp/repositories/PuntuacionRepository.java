package com.app.DeliveryApp.repositories;

import com.app.DeliveryApp.models.Puntuacion;
import java.util.List;
import java.util.Optional;

public interface PuntuacionRepository {
    Puntuacion save(Puntuacion puntuacion); 
    Optional<Puntuacion> findById(Long id);
    List<Puntuacion> findAll();
    int update(Puntuacion puntuacion);
    int deleteById(Long id);

    List<Puntuacion> findByRutRepartidor(String rutRepartidor);
}