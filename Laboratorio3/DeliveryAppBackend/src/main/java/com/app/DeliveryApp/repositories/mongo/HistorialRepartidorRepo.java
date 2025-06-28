package com.app.DeliveryApp.repositories.mongo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Repository
public class HistorialRepartidorRepo {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Consulta 4: Analiza las zonas más frecuentes por cantidad de repartidores únicos
     * Retorna: zona_lat, zona_lng, cantidad_repartidores, total_visitas (SIN repartidores_unicos)
     */
    public List<Map> getRutasFrecuentesUltimos7Dias() {
        LocalDateTime fechaLimite = LocalDateTime.now().minusDays(7);
        String fechaLimiteStr = fechaLimite.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));

        Aggregation agg = Aggregation.newAggregation(
                Aggregation.unwind("rutas"),
                Aggregation.project("repartidor_id")
                        .and("rutas.timestamp").as("timestamp")
                        .and("rutas.latitud").as("zona_lat")
                        .and("rutas.longitud").as("zona_lng"),
                Aggregation.match(Criteria.where("timestamp").gte(fechaLimiteStr)),
                Aggregation.group("zona_lat", "zona_lng")
                        .addToSet("repartidor_id").as("repartidores_unicos")
                        .count().as("total_visitas"),
                Aggregation.project()
                        .and("_id.zona_lat").as("latitudZona")
                        .and("_id.zona_lng").as("longitudZona")
                        .and("total_visitas").as("visitasFrecuentes")
                        .andExpression("size(repartidores_unicos)").as("cantidadRepartidores").andExclude("_id"),
                Aggregation.match(Criteria.where("cantidadRepartidores").gte(2)),
                Aggregation.sort(org.springframework.data.domain.Sort.Direction.DESC, "visitasFrecuentes", "cantidadRepartidores"),
                Aggregation.limit(10)
        );

        AggregationResults<Map> results = mongoTemplate.aggregate(agg, "historial_repartidores", Map.class);
        return results.getMappedResults();
    }


    /**
     * Consulta 4 alternativa: Para análisis por repartidor específico
     */
    public List<Map> getRutasPorRepartidor(String repartidorId) {
        LocalDateTime fechaLimite = LocalDateTime.now().minusDays(7);
        String fechaLimiteStr = fechaLimite.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));

        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("repartidor_id").is(repartidorId)),
                Aggregation.unwind("rutas"),
                Aggregation.match(Criteria.where("rutas.timestamp").gte(fechaLimiteStr)),
                Aggregation.project()
                        .and("repartidor_id").as("repartidorId")
                        .and("rutas.latitud").as("latitudZona")
                        .and("rutas.longitud").as("longitudZona")
                        .and("rutas.timestamp").as("timestamp"),
                Aggregation.group("latitudZona", "longitudZona")
                        .first("repartidorId").as("repartidorId")
                        .first("latitudZona").as("latitudZona")
                        .first("longitudZona").as("longitudZona")
                        .count().as("visitasFrecuentes"),
                Aggregation.sort(org.springframework.data.domain.Sort.Direction.DESC, "visitasFrecuentes")
        );

        AggregationResults<Map> results = mongoTemplate.aggregate(agg, "historial_repartidores", Map.class);
        return results.getMappedResults();
    }

    /**
     * Método de debug: Ver todas las rutas sin filtros (para pruebas)
     */
    public List<Map> getAllRutasForDebug() {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.unwind("rutas"),
                Aggregation.project()
                        .and("repartidor_id").as("repartidorId")
                        .and("rutas.latitud").as("latitud")
                        .and("rutas.longitud").as("longitud")
                        .and("rutas.timestamp").as("timestamp"),
                Aggregation.limit(20)
        );

        AggregationResults<Map> results = mongoTemplate.aggregate(agg, "historial_repartidores", Map.class);
        return results.getMappedResults();
    }

    
}