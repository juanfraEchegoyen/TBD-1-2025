package com.app.DeliveryApp.repositories;

import com.app.DeliveryApp.models.Repartidor;
import java.util.List;
import java.util.Optional;

public interface RepartidorRepository {
    Repartidor save(Repartidor repartidor);
    Optional<Repartidor> findByRut(String rut); // PK es RUT
    List<Repartidor> findAll();
    int update(Repartidor repartidor);
    int deleteByRut(String rut); // PK es RUT
}