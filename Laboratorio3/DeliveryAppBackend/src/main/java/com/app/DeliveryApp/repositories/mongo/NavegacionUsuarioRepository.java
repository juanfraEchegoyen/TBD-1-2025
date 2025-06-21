package com.app.DeliveryApp.repositories.mongo;

import com.app.DeliveryApp.models.mongo.NavegacionUsuario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NavegacionUsuarioRepository extends MongoRepository<NavegacionUsuario, String> {
    
    // find por cliente id
    List<NavegacionUsuario> findByClienteId(String clienteId);
    
    // find por tipo
    @Query("{'eventos.tipo': ?0}")
    List<NavegacionUsuario> findByTipoEvento(String tipo);
    
    // find valor evento espec
    @Query("{'eventos.valor': {$regex: ?0, $options: 'i'}}")
    List<NavegacionUsuario> findByValorEvento(String valor);
    
    // find por rnago fecha
    @Query("{'eventos.timestamp': {$gte: ?0, $lte: ?1}}")
    List<NavegacionUsuario> findByFechaEventosBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    // find por clientes con busquedas realizadas
    @Query("{'eventos.tipo': 'busqueda'}")
    List<NavegacionUsuario> findClientesConBusquedas();
    
    // find por clientes con click realizados
    @Query("{'eventos.tipo': 'click'}")
    List<NavegacionUsuario> findClientesConClicks();
}
