package com.app.DeliveryApp.services;

import com.app.DeliveryApp.models.Producto;
import com.app.DeliveryApp.repositories.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    @Autowired
    public ProductoServiceImpl(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public Producto crearProducto(Producto producto) {
        // TODO: validaciones: precio positivo stock positivo
        return productoRepository.save(producto);
    }

    @Override
    public Optional<Producto> obtenerProductoPorId(Long id) {
        return productoRepository.findById(id);
    }

    @Override
    public List<Producto> obtenerTodosLosProductos() {
        return productoRepository.findAll();
    }

    @Override
    public Optional<Producto> actualizarProducto(Long id, Producto productoActualizado) {
        Optional<Producto> productoExistenteOpt = productoRepository.findById(id);

        if (productoExistenteOpt.isPresent()) {
            Producto productoParaActualizar = productoExistenteOpt.get();
            productoParaActualizar.setNombre(productoActualizado.getNombre());
            productoParaActualizar.setPrecio(productoActualizado.getPrecio());
            productoParaActualizar.setCategoria(productoActualizado.getCategoria());
            productoParaActualizar.setTipoProducto(productoActualizado.getTipoProducto());
            productoParaActualizar.setStock(productoActualizado.getStock());
            // TODO : validaciones precio positivo y stock positivo al actualizar

            productoRepository.update(productoParaActualizar);
            return Optional.of(productoParaActualizar);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public boolean eliminarProducto(Long id) {
        if (productoRepository.findById(id).isPresent()) {
            int filasAfectadas = productoRepository.deleteById(id);
            return filasAfectadas > 0;
        } else {
            return false;
        }
    }
}