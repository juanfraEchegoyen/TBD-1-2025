package com.app.DeliveryApp.services;

import com.app.DeliveryApp.models.Repartidor;
import org.locationtech.jts.geom.Point;
import java.util.List;
import java.util.Optional;

public interface RepartidorService {
    Repartidor crearRepartidor(Repartidor repartidor);
    Optional<Repartidor> obtenerRepartidorPorRut(String rut);
    List<Repartidor> obtenerTodosLosRepartidores();
    Optional<Repartidor> actualizarRepartidor(String rut, Repartidor repartidorActualizado);
    boolean eliminarRepartidor(String rut);
    Optional<Repartidor> actualizarUbicacionRepartidor(String rut, Point nuevaUbicacion);
    Optional<Repartidor> actualizarDistanciaRecorrida(String rut, Double nuevaDistancia);
    List<Repartidor> obtenerRepartidoresEnRadio(Point centro, double radioMetros);
    List<Repartidor> obtenerRepartidoresDisponiblesCercanos(Point ubicacion, int limite);
}