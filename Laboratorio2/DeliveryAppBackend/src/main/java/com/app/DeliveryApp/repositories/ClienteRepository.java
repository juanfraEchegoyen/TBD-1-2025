package com.app.DeliveryApp.repositories;

import com.app.DeliveryApp.models.Cliente;
import java.util.List;
import java.util.Optional;

public interface ClienteRepository {
    Cliente save(Cliente cliente);
    Optional<Cliente> findByRut(String rut); 
    List<Cliente> findAll();
    int update(Cliente cliente);
    int deleteByRut(String rut);
}