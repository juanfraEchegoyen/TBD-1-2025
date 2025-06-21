package com.app.DeliveryApp.repositories;

import com.app.DeliveryApp.models.ZonaCobertura;
import org.locationtech.jts.geom.Point;

import java.util.List;
import java.util.Optional;

public interface ZonaCoberturaRepository {
    ZonaCobertura save(ZonaCobertura zonaCobertura);
    Optional<ZonaCobertura> findById(Long id);
    List<ZonaCobertura> findAll();
    int update(ZonaCobertura zonaCobertura);
    int deleteById(Long id);
    List<ZonaCobertura> findByEmpresaRut(String rutEmpresa);
    List<ZonaCobertura> findZonasQueContienenPunto(Point punto);
    boolean verificarCobertura(String rutEmpresa, Point puntoEntrega);
}
