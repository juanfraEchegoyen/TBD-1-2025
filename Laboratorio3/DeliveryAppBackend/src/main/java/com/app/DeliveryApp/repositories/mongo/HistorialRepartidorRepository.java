package com.app.DeliveryApp.repositories.mongo;

import com.app.DeliveryApp.models.mongo.HistorialRepartidor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HistorialRepartidorRepository extends MongoRepository<HistorialRepartidor, String> {
    
    // find por id repartidor
    List<HistorialRepartidor> findByRepartidorId(String repartidorId);
    
    // find  por fehca
    @Query("{'rutas.timestamp': {$gte: ?0, $lte: ?1}}")
    List<HistorialRepartidor> findByFechaRutasBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    // find por zona
    @Query("{'rutas': {$elemMatch: {'latitud': {$gte: ?0, $lte: ?1}, 'longitud': {$gte: ?2, $lte: ?3}}}}")
    List<HistorialRepartidor> findByZonaGeografica(Double latitudMin, Double latitudMax, Double longitudMin, Double longitudMax);
    
    // find por active from date
    @Query("{'rutas.timestamp': {$gte: ?0}}")
    List<HistorialRepartidor> findRepartidoresActivosDesde(LocalDateTime fecha);
}
