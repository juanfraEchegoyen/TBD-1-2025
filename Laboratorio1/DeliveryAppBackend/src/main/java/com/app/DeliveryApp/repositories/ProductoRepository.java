package com.app.DeliveryApp.repositories; // Asegúrate que este sea tu paquete correcto

import com.app.DeliveryApp.models.Producto; // Asegúrate que la ruta a tu modelo Producto sea correcta
import java.util.List;
import java.util.Optional;

public interface ProductoRepository {
    Producto save(Producto producto);

    Optional<Producto> findById(Long id);
    List<Producto> findAll();
    int update(Producto producto);
    int deleteById(Long id);
}