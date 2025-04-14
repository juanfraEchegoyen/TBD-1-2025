package com.app.DeliveryApp.repositories;

import com.app.DeliveryApp.models.DetallePedido;
import java.util.List;
import java.util.Optional;

public interface DetallePedidoRepository {
    DetallePedido save(DetallePedido detallePedido); // Devuelve con ID
    Optional<DetallePedido> findById(Long id); // PK es ID (Long)
    List<DetallePedido> findAll();
    int update(DetallePedido detallePedido);
    int deleteById(Long id); // PK es ID (Long)

    // Muy útil: buscar detalles por ID de pedido
    List<DetallePedido> findByPedidoId(Long idPedido);
}