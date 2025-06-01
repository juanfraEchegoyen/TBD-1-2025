package com.app.DeliveryApp.services;

import com.app.DeliveryApp.models.ZonaCobertura;
import org.locationtech.jts.geom.Point;
import java.util.List;
import java.util.Optional;

public interface ZonaCoberturaService {
    ZonaCobertura crearZonaCobertura(ZonaCobertura zonaCobertura);
    Optional<ZonaCobertura> obtenerZonaCoberturaPorId(Long id);
    List<ZonaCobertura> obtenerTodasLasZonasCobertura();
    List<ZonaCobertura> obtenerZonasCoberturaByEmpresa(String rutEmpresa);
    Optional<ZonaCobertura> actualizarZonaCobertura(Long id, ZonaCobertura zonaActualizada);
    boolean eliminarZonaCobertura(Long id);
    List<ZonaCobertura> obtenerZonasQueContienenPunto(Point punto);
    boolean estaEnZonaCobertura(String rutEmpresa, Point puntoEntrega);
}
