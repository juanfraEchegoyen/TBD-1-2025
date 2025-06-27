// src/main/java/com/app/DeliveryApp/repositories/mongo/OpinionClienteRepo.java
package com.app.DeliveryApp.repositories.mongo;

import com.app.DeliveryApp.models.mongo.OpinionCliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.AggregationSpELExpression;
import org.springframework.data.mongodb.core.aggregation.DateOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class OpinionClienteRepo {

    @Autowired
    private OpinionClienteRepository opinionClienteRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<OpinionCliente> findByEmpresaId(String empresaId) {
        return opinionClienteRepository.findByEmpresaId(empresaId);
    }

    public List<Map> getPromedioPuntuacionPorEmpresa() {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.group("empresa_id").avg("puntuacion").as("promedio_puntuacion")
        );
        AggregationResults<Map> results = mongoTemplate.aggregate(agg, "opiniones_clientes", Map.class);
        return results.getMappedResults();
    }

    public List<OpinionCliente> getOpinionesConDemoraOError() {
        Query query = new Query(Criteria.where("comentario").regex("demora|error", "i"));
        return mongoTemplate.find(query, OpinionCliente.class, "opiniones_clientes");
    }

    public List<Map> getOpinionesAgrupadasPorHora() {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.project("puntuacion")
                        .and(AggregationSpELExpression.expressionOf("toDate(fecha)")).as("fecha_convertida"),
                Aggregation.project("puntuacion", "fecha_convertida")
                        .and(DateOperators.Hour.hourOf("fecha_convertida")).as("hora"),
                Aggregation.group("hora")
                        .avg("puntuacion").as("promedio_puntaje")
                        .count().as("cantidad_opiniones"),
                Aggregation.project("promedio_puntaje", "cantidad_opiniones")
                        .and("_id").as("hora"),
                Aggregation.sort(Sort.Direction.ASC, "hora")
        );

        AggregationResults<Map> results = mongoTemplate.aggregate(agg, "opiniones_clientes", Map.class);
        return results.getMappedResults();
    }






}