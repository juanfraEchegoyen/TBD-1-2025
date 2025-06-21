package com.app.DeliveryApp.repositories.mongo;

import com.app.DeliveryApp.models.mongo.LogPedido;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LogPedidoRepository extends MongoRepository<LogPedido, String> {
    
    // find por logs id pedido
    LogPedido findByPedidoId(String pedidoId);
    
    // find por estado
    @Query("{'historial_estados.estado': ?0}")
    List<LogPedido> findByEstado(String estado);
    
    // find por rango fecha
    @Query("{'historial_estados.timestamp': {$gte: ?0, $lte: ?1}}")
    List<LogPedido> findByFechaEventosBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    // find por cambios 
    @Query("{'historial_estados': {$size: {$gt: ?0}}}")
    List<LogPedido> findPedidosConMasDeCambios(int numeroCambios);
    
    //f find por al menos n estados
    @Query("{$expr: {$gte: [{$size: '$historial_estados'}, ?0]}}")
    List<LogPedido> findPedidosConAlMenosNEstados(int numeroEstados);
}
