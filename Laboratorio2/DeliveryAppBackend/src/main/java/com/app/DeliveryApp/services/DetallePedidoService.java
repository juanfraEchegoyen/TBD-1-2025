package com.app.DeliveryApp.services;

import com.app.DeliveryApp.models.DetallePedido;
import java.util.List;
import java.util.Optional;

public interface DetallePedidoService {
    DetallePedido crearDetalle(DetallePedido detallePedido);
    Optional<DetallePedido> obtenerDetallePorId(Long id);
    List<DetallePedido> obtenerDetallesPorPedidoId(Long pedidoId);
    Optional<DetallePedido> actualizarDetalle(Long id, DetallePedido detalleActualizado);
    boolean eliminarDetalle(Long id);
    boolean eliminarDetallesPorPedidoId(Long pedidoId);
}