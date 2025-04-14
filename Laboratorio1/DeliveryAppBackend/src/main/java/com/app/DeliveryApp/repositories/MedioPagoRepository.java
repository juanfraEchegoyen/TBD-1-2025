package com.app.DeliveryApp.repositories;

import com.app.DeliveryApp.models.MedioPago;
import java.util.List;
import java.util.Optional;

public interface MedioPagoRepository {
    MedioPago save(MedioPago medioPago);
    Optional<MedioPago> findById(Long id); // PK es ID (Long)
    List<MedioPago> findAll();
    int update(MedioPago medioPago);
    int deleteById(Long id); // PK es ID (Long)

    List<MedioPago> findByRutCliente(String rutCliente);
}