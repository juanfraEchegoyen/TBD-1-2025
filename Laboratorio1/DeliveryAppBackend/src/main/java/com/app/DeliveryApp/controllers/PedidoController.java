package com.app.DeliveryApp.controllers;

import com.app.DeliveryApp.dto.RegistroPedidoDTO;
import com.app.DeliveryApp.models.Pedido;
import com.app.DeliveryApp.models.DetallePedido;
import com.app.DeliveryApp.dto.PedidoRequestDTO;
import com.app.DeliveryApp.repositories.JdbcPedidoRepository;
import com.app.DeliveryApp.services.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/pedidos")
@CrossOrigin(origins="http://localhost:3000")
public class PedidoController {

    @Autowired
    private final PedidoService pedidoService;
    @Autowired
    private final JdbcPedidoRepository jdbcPedidoRepository;

    @Autowired
    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
        this.jdbcPedidoRepository = new JdbcPedidoRepository();
    }

    @PostMapping
    public ResponseEntity<?> crearPedido(@RequestBody PedidoRequestDTO pedidoRequest) {
        try {
            Pedido pedidoModel = pedidoRequest.toPedidoModel();
            List<DetallePedido> detalles = pedidoRequest.getDetalles();
            Pedido nuevoPedido = pedidoService.crearPedido(pedidoModel, detalles);
            return new ResponseEntity<>(nuevoPedido, HttpStatus.CREATED);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear pedido: " + e.getMessage());
        }
    }

    @PostMapping("/registrar")
    public ResponseEntity<String> RegistrarPedido(@RequestBody RegistroPedidoDTO registroDTO) {
        Pedido pedido = new Pedido();
        pedido.setPrioridadPedido(registroDTO.getPrioridadPedido());
        pedido.setRutCliente(registroDTO.getRutCliente());

        DetallePedido detallePedido = new DetallePedido();
        detallePedido.setCantidad(registroDTO.getCantidad());
        detallePedido.setIdProducto(registroDTO.getIdProducto());

        String resultado = pedidoService.registrarPedido(pedido, detallePedido);
        if (resultado != null) {
            return ResponseEntity.badRequest().body(resultado);
        }
        return ResponseEntity.ok("Pedido registrado correctamente");
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<String> actualizarEstadoPedido(@PathVariable Integer id, @RequestParam String nuevoEstado) {
        System.out.println("id: " + id +",Estado " +nuevoEstado);
        jdbcPedidoRepository.actualizarEstadoPedido(id, nuevoEstado);
        return ResponseEntity.ok("Estado actualizado correctamente");
    }


    @PutMapping("/{id}/confirmar")
    public ResponseEntity<String> descontarStockAlConfirmar(@PathVariable Integer id) {
        jdbcPedidoRepository.descontar_stock_al_confirmar(id);
        return ResponseEntity.ok("Stock descontado correctamente");
    }

    @PutMapping("/{id}/fallado")
    public ResponseEntity<String> aumentarStockAlFallar(@PathVariable Integer id) {
        jdbcPedidoRepository.Aumentar_stock_al_fallar(id);
        return ResponseEntity.ok("Stock aumentado por pedido fallido");
    }


    @GetMapping("/{id}")
    public ResponseEntity<Pedido> obtenerPedidoPorId(@PathVariable Long id) {
        return pedidoService.obtenerPedidoPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Pedido>> obtenerTodosLosPedidos() {
        List<Pedido> pedidos = pedidoService.obtenerTodosLosPedidos();
        return ResponseEntity.ok(pedidos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarPedido(@PathVariable Long id, @RequestBody Pedido pedido) {
        try {
            return pedidoService.actualizarPedido(id, pedido)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar pedido");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPedido(@PathVariable Long id) {
        try {
            boolean eliminado = pedidoService.eliminarPedido(id);
            return eliminado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);         }
    }
}