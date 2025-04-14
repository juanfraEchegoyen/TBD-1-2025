package com.app.DeliveryApp.controllers;

import com.app.DeliveryApp.models.DetallePedido;
import com.app.DeliveryApp.services.DetallePedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pedidos/{pedidoId}/detalles") // Ruta anidada
public class DetallePedidoController {

    private final DetallePedidoService detallePedidoService;

    @Autowired
    public DetallePedidoController(DetallePedidoService detallePedidoService) {
        this.detallePedidoService = detallePedidoService;
    }

    @GetMapping
    public ResponseEntity<List<DetallePedido>> obtenerDetallesPorPedido(@PathVariable Long pedidoId) {
        List<DetallePedido> detalles = detallePedidoService.obtenerDetallesPorPedidoId(pedidoId);
        return ResponseEntity.ok(detalles);
    }

    @GetMapping("/{detalleId}")
    public ResponseEntity<DetallePedido> obtenerDetalleEspecifico(@PathVariable Long pedidoId, @PathVariable Long detalleId) {
        return detallePedidoService.obtenerDetallePorId(detalleId)
                .filter(detalle -> detalle.getIdPedido().equals(pedidoId))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}