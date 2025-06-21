package com.app.DeliveryApp.repositories.mongo;

import com.app.DeliveryApp.models.mongo.OpinionCliente;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OpinionClienteRepository extends MongoRepository<OpinionCliente, String> {
    
    // opiniones por empresa
    List<OpinionCliente> findByEmpresaId(String empresaId);
    
    // por cliente
    List<OpinionCliente> findByClienteId(String clienteId);
    
    // por puntuacion
    List<OpinionCliente> findByPuntuacion(Integer puntuacion);
    
    // por rango fecha
    List<OpinionCliente> findByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    // palabras especificas entregando palabra
    @Query("{'comentario': {$regex: ?0, $options: 'i'}}")
    List<OpinionCliente> findByComentarioContaining(String palabra);
    
    // por puntuacion mayor a 
    List<OpinionCliente> findByPuntuacionGreaterThanEqual(Integer puntuacion);
    
    // por puntuacion menor a
    List<OpinionCliente> findByPuntuacionLessThanEqual(Integer puntuacion);
}
