package com.app.DeliveryApp.services;

import com.app.DeliveryApp.models.Producto;
import java.util.List;
import java.util.Optional;

public interface ProductoService {
    Producto crearProducto(Producto producto);
    Optional<Producto> obtenerProductoPorId(Long id);
    List<Producto> obtenerTodosLosProductos();
    Optional<Producto> actualizarProducto(Long id, Producto productoActualizado);
    boolean eliminarProducto(Long id);
}