package com.app.DeliveryApp.repositories;

import com.app.DeliveryApp.models.Pedido;
import java.util.List;
import java.util.Optional;

public interface PedidoRepository {
    Pedido save(Pedido pedido); 
    Optional<Pedido> findById(Long id);
    List<Pedido> findAll();
    int update(Pedido pedido);
    int deleteById(Long id);
}