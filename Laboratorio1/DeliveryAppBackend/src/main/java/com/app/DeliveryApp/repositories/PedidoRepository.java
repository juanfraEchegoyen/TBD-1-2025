package com.app.DeliveryApp.repositories;

import com.app.DeliveryApp.models.Pedido;
import java.util.List;
import java.util.Optional;

public interface PedidoRepository {
    Pedido save(Pedido pedido); // Debería devolver el pedido con el ID asignado
    Optional<Pedido> findById(Long id); // PK es ID (Long)
    List<Pedido> findAll();
    int update(Pedido pedido);
    int deleteById(Long id); // PK es ID (Long)
}