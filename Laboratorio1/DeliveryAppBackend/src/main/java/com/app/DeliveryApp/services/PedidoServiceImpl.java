package com.app.DeliveryApp.services;

import com.app.DeliveryApp.models.*;
import com.app.DeliveryApp.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;
    private final EmpresaRepository empresaRepository;
    private final RepartidorRepository repartidorRepository;

    @Autowired
    public PedidoServiceImpl(PedidoRepository pedidoRepository,
                             DetallePedidoRepository detallePedidoRepository,
                             ProductoRepository productoRepository,
                             ClienteRepository clienteRepository,
                             EmpresaRepository empresaRepository,
                             RepartidorRepository repartidorRepository) {
        this.pedidoRepository = pedidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.productoRepository = productoRepository;
        this.clienteRepository = clienteRepository;
        this.empresaRepository = empresaRepository;
        this.repartidorRepository = repartidorRepository;
    }

    @Override
    @Transactional
    public Pedido crearPedido(Pedido pedido, List<DetallePedido> detalles) {
        validarCliente(pedido.getRutCliente());
        if (pedido.getRutEmpresa() != null) {
            validarEmpresa(pedido.getRutEmpresa());
        }
        if (pedido.getRutRepartidor() != null) {
            validarRepartidor(pedido.getRutRepartidor());
        }

        if (detalles == null || detalles.isEmpty()) {
            throw new IllegalArgumentException("El pedido debe contener un detalle como minimo");
        }
        validarDetallesYStock(detalles);

        if (pedido.getEstadoEntrega() == null) {
            pedido.setEstadoEntrega("PENDIENTE");
        }
        Pedido nuevoPedido = pedidoRepository.save(pedido);

        for (DetallePedido detalle : detalles) {
            detalle.setIdPedido(nuevoPedido.getIdPedido());
            if (detalle.getPrecioTotal() == null) {
                Producto producto = productoRepository.findById(detalle.getIdProducto()).get();
                detalle.setPrecioTotal(producto.getPrecio() * detalle.getCantidad());
            }
            detallePedidoRepository.save(detalle);
        }

        actualizarStock(detalles, false);

        return nuevoPedido;
    }

    @Override
    public Optional<Pedido> obtenerPedidoPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    @Override
    public List<Pedido> obtenerTodosLosPedidos() {
        return pedidoRepository.findAll();
    }

    @Override
    @Transactional
    public Optional<Pedido> actualizarPedido(Long id, Pedido pedidoActualizado) {
        return pedidoRepository.findById(id).map(pedidoExistente -> {

            validarTransicionEstado(pedidoExistente.getEstadoEntrega(), pedidoActualizado.getEstadoEntrega());

            if (!pedidoExistente.getRutCliente().equals(pedidoActualizado.getRutCliente())) {
                validarCliente(pedidoActualizado.getRutCliente());
            }
            if (pedidoActualizado.getRutEmpresa() != null && !pedidoActualizado.getRutEmpresa().equals(pedidoExistente.getRutEmpresa())) {
                validarEmpresa(pedidoActualizado.getRutEmpresa());
            }
            if (pedidoActualizado.getRutRepartidor() != null && !pedidoActualizado.getRutRepartidor().equals(pedidoExistente.getRutRepartidor())) {
                validarRepartidor(pedidoActualizado.getRutRepartidor());
            }

            pedidoExistente.setEstadoEntrega(pedidoActualizado.getEstadoEntrega());
            pedidoExistente.setPrioridadPedido(pedidoActualizado.getPrioridadPedido());
            pedidoExistente.setProblemaCritico(pedidoActualizado.isProblemaCritico());
            pedidoExistente.setRutCliente(pedidoActualizado.getRutCliente());
            pedidoExistente.setRutEmpresa(pedidoActualizado.getRutEmpresa());
            pedidoExistente.setRutRepartidor(pedidoActualizado.getRutRepartidor());

            pedidoRepository.update(pedidoExistente);
            return pedidoExistente;
        });
    }

    @Override
    @Transactional
    public boolean eliminarPedido(Long id) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(id);
        if (pedidoOpt.isPresent()) {
            List<DetallePedido> detalles = detallePedidoRepository.findByPedidoId(id);
            if (!detalles.isEmpty()) {
                actualizarStock(detalles, true);
            }

            detallePedidoRepository.deleteByPedidoId(id);

            int filasAfectadas = pedidoRepository.deleteById(id);
            return filasAfectadas > 0;

        } else {
            return false;
        }
    }

    private void validarCliente(String rutCliente) {
        if (rutCliente == null || clienteRepository.findByRut(rutCliente).isEmpty()) {
            throw new NoSuchElementException("El cliente con RUT " + rutCliente + " no existe");
        }
    }

    private void validarEmpresa(String rutEmpresa) {
        if (empresaRepository.findByRut(rutEmpresa).isEmpty()) {
            throw new NoSuchElementException("La empresa con RUT " + rutEmpresa + " no existe");
        }
    }

    private void validarRepartidor(String rutRepartidor) {
        if (repartidorRepository.findByRut(rutRepartidor).isEmpty()) {
            throw new NoSuchElementException(" El Repartidor con RUT " + rutRepartidor + " no existe");
        }
    }

    private void validarDetallesYStock(List<DetallePedido> detalles) {
        for (DetallePedido detalle : detalles) {
            if (detalle.getCantidad() <= 0) {
                throw new IllegalArgumentException("La cantidad para el producto ID " + detalle.getIdProducto() + " debe ser positiva");
            }
            Producto producto = productoRepository.findById(detalle.getIdProducto())
                    .orElseThrow(() -> new NoSuchElementException("El producto con ID " + detalle.getIdProducto() + " no se encontro en el pedido"));

            if (producto.getStock() < detalle.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre() + ". hay disponible: " + producto.getStock());
            }
        }
    }

    private void actualizarStock(List<DetallePedido> detalles, boolean restaurar) {
        for (DetallePedido detalle : detalles) {
            Producto producto = productoRepository.findById(detalle.getIdProducto())
                    .orElseThrow(() -> new IllegalStateException("Producto ID " + detalle.getIdProducto() + " no encontrado para la actualizacion"));

            int cantidadCambio = detalle.getCantidad();
            int stockActual = producto.getStock();
            int nuevoStock;

            if (restaurar) {
                nuevoStock = stockActual + cantidadCambio;
            } else {
                nuevoStock = stockActual - cantidadCambio;
                if (nuevoStock < 0) {
                    throw new IllegalStateException("Stock erroneo detectado para producto: " + producto.getNombre());
                }
            }

            producto.setStock(nuevoStock);
            productoRepository.update(producto);
        }
    }

    private void validarTransicionEstado(String estadoActual, String estadoNuevo) {
        if (("ENTREGADO".equals(estadoActual) || "CANCELADO".equals(estadoActual))
                && !estadoActual.equals(estadoNuevo)) {
            throw new IllegalArgumentException("No se pudo cambiar el estado desde '" + estadoActual + "' a '" + estadoNuevo + "'.");
        }
    }
}