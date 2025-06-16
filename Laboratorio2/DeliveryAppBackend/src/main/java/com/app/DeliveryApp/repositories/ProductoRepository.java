package com.app.DeliveryApp.repositories;

import com.app.DeliveryApp.models.Producto;
import java.util.List;
import java.util.Optional;

public interface ProductoRepository {
    Producto save(Producto producto);

    Optional<Producto> findById(Long id);
    List<Producto> findAll();
    int update(Producto producto);
    int deleteById(Long id);

    Optional<Producto> findByNombre(String nombreProducto);
}