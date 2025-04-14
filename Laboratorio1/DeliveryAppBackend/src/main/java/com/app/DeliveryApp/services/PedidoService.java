package com.app.DeliveryApp.services;

import com.app.DeliveryApp.models.DetallePedido; // Importar
import com.app.DeliveryApp.models.Pedido;
import java.util.List;
import java.util.Optional;

public interface PedidoService {
    Pedido crearPedido(Pedido pedido, List<DetallePedido> detalles);
    Optional<Pedido> obtenerPedidoPorId(Long id);
    List<Pedido> obtenerTodosLosPedidos();
    Optional<Pedido> actualizarPedido(Long id, Pedido pedidoActualizado); // Podría necesitar lógica para actualizar detalles también
    boolean eliminarPedido(Long id);
}