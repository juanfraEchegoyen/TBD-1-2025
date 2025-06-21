package com.app.DeliveryApp.repositories;

import com.app.DeliveryApp.dto.RepartidorRutNombreDTO;
import com.app.DeliveryApp.models.Repartidor;
import java.util.List;
import java.util.Optional;

public interface RepartidorRepository {
    Repartidor save(Repartidor repartidor);
    Optional<Repartidor> findByRut(String rut); 
    List<Repartidor> findAll();
    int update(Repartidor repartidor);
    int deleteByRut(String rut);
    List<RepartidorRutNombreDTO> ObtenerRutYnombresRepartidor();
}