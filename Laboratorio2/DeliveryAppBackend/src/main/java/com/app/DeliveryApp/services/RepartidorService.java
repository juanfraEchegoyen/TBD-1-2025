package com.app.DeliveryApp.services;

import com.app.DeliveryApp.models.Repartidor;
import java.util.List;
import java.util.Optional;

public interface RepartidorService {
    Repartidor crearRepartidor(Repartidor repartidor);
    Optional<Repartidor> obtenerRepartidorPorRut(String rut);
    List<Repartidor> obtenerTodosLosRepartidores();
    Optional<Repartidor> actualizarRepartidor(String rut, Repartidor repartidorActualizado);
    boolean eliminarRepartidor(String rut);
}