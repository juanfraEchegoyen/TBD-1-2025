package com.app.DeliveryApp.services;

import com.app.DeliveryApp.models.DetallePedido;
import com.app.DeliveryApp.models.MedioPago;
import com.app.DeliveryApp.models.Pedido;
import org.locationtech.jts.geom.LineString;
import java.util.List;
import java.util.Optional;

public interface PedidoService {
    Pedido crearPedido(Pedido pedido, List<DetallePedido> detalles);
    Optional<Pedido> obtenerPedidoPorId(Long id);
    List<Pedido> obtenerTodosLosPedidos();
    Optional<Pedido> actualizarPedido(Long id, Pedido pedidoActualizado); 
    boolean eliminarPedido(Long id);
    String registrarPedido(Pedido pedido, DetallePedido detallePedido, MedioPago medioPago);
    LineString calcularRutaEstimada(String rutCliente, String rutEmpresa);
}