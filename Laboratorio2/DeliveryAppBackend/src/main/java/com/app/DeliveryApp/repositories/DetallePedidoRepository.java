package com.app.DeliveryApp.repositories;

import com.app.DeliveryApp.models.DetallePedido;
import java.util.List;
import java.util.Optional;

public interface DetallePedidoRepository {
    DetallePedido save(DetallePedido detallePedido);
    Optional<DetallePedido> findById(Long id);
    List<DetallePedido> findAll();
    int update(DetallePedido detallePedido);
    int deleteById(Long id);
    List<DetallePedido> findByPedidoId(Long idPedido);
    int deleteByPedidoId(Long pedidoId);

}