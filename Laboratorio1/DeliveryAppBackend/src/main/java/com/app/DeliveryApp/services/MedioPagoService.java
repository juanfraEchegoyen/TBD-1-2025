package com.app.DeliveryApp.services;

import com.app.DeliveryApp.models.MedioPago;
import java.util.List;
import java.util.Optional;

public interface MedioPagoService {
    MedioPago crearMedioPago(MedioPago medioPago);
    Optional<MedioPago> obtenerMedioPagoPorId(Long id);
    List<MedioPago> obtenerTodosLosMediosPago();
    List<MedioPago> obtenerMediosPagoPorCliente(String rutCliente);
    Optional<MedioPago> actualizarMedioPago(Long id, MedioPago medioPagoActualizado);
    boolean eliminarMedioPago(Long id);
    List<MedioPago> obtenerMediosPagoPorPedido(Long idPedido);
}