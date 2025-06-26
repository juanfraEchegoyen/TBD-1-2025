// src/main/java/com/app/DeliveryApp/repositories/mongo/OpinionClienteRepo.java
package com.app.DeliveryApp.repositories.mongo;

import com.app.DeliveryApp.models.mongo.OpinionCliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
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
}