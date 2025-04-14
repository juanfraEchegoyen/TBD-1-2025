package com.app.DeliveryApp.services;

import com.app.DeliveryApp.models.DetallePedido;
import com.app.DeliveryApp.repositories.DetallePedidoRepository;
import com.app.DeliveryApp.repositories.ProductoRepository;
import com.app.DeliveryApp.repositories.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class DetallePedidoServiceImpl implements DetallePedidoService {

    private final DetallePedidoRepository detallePedidoRepository;
    private final ProductoRepository productoRepository;
    private final PedidoRepository pedidoRepository;

    @Autowired
    public DetallePedidoServiceImpl(DetallePedidoRepository detallePedidoRepository,
                                    ProductoRepository productoRepository,
                                    PedidoRepository pedidoRepository) {
        this.detallePedidoRepository = detallePedidoRepository;
        this.productoRepository = productoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @Override
    @Transactional
    public DetallePedido crearDetalle(DetallePedido detallePedido) {
        pedidoRepository.findById(detallePedido.getIdPedido())
                .orElseThrow(() -> new NoSuchElementException("Pedido con ID " + detallePedido.getIdPedido() + " no encontrado"));
        productoRepository.findById(detallePedido.getIdProducto())
                .orElseThrow(() -> new NoSuchElementException("Producto con ID " + detallePedido.getIdProducto() + " no encontrado"));
        if (detallePedido.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad del detalle debe ser mayor a 0");
        }

        return detallePedidoRepository.save(detallePedido);
    }

    @Override
    public Optional<DetallePedido> obtenerDetallePorId(Long id) {
        return detallePedidoRepository.findById(id);
    }

    @Override
    public List<DetallePedido> obtenerDetallesPorPedidoId(Long pedidoId) {
        pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new NoSuchElementException("Pedido con ID " + pedidoId + " no encontrado"));
        return detallePedidoRepository.findByPedidoId(pedidoId);
    }

    @Override
    @Transactional
    public Optional<DetallePedido> actualizarDetalle(Long id, DetallePedido detalleActualizado) {
        return detallePedidoRepository.findById(id).map(detalleExistente -> {
            if (detalleActualizado.getCantidad() <= 0) {
                throw new IllegalArgumentException("La cantidad del detalle debe ser mayor a 0");
            }
            if (!detalleExistente.getIdPedido().equals(detalleActualizado.getIdPedido())) {
                throw new IllegalArgumentException("No se puede cambiar el ID del pedido asociado a un detalle existente");
            }
            if (!detalleExistente.getIdProducto().equals(detalleActualizado.getIdProducto())) {
                throw new IllegalArgumentException("No se puede cambiar el ID del producto asociado a un detalle que existe");
            }
            detalleExistente.setCantidad(detalleActualizado.getCantidad());
            detalleExistente.setPrecioTotal(detalleActualizado.getPrecioTotal());
            detalleExistente.setTiempoEntrega(detalleActualizado.getTiempoEntrega());
            detalleExistente.setFechaEntrega(detalleActualizado.getFechaEntrega());

            detallePedidoRepository.update(detalleExistente);
            return detalleExistente;
        });
    }

    @Override
    @Transactional
    public boolean eliminarDetalle(Long id) {
        return detallePedidoRepository.findById(id).map(detalle -> {
            int filasAfectadas = detallePedidoRepository.deleteById(id);
            return filasAfectadas > 0;
        }).orElse(false);
    }

    @Override
    @Transactional
    public boolean eliminarDetallesPorPedidoId(Long pedidoId) {
        pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new NoSuchElementException("Pedido con ID " + pedidoId + " no encontrado"));


        int filasAfectadas = detallePedidoRepository.deleteByPedidoId(pedidoId);
        return filasAfectadas >= 0;
    }
}