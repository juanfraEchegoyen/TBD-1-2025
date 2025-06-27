package com.app.DeliveryApp.repositories.mongo;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class NavegacionUsuarioRepo {

    @Autowired
    private NavegacionUsuarioRepository navegacionUsuarioRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Consulta 5
     * Obtiene los IDs de clientes que no han realizado un evento de compra.
     * @return Lista de IDs de clientes con evento de compra.
     */
    public List<String> getClienteIdsSinEventoCompra() {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("eventos.tipo").ne("compra")),
                Aggregation.project("cliente_id")
        );
        AggregationResults<Document> results = mongoTemplate.aggregate(agg, "navegacion_usuarios", Document.class);
        return results.getMappedResults()
                .stream()
                .map(doc -> doc.getString("cliente_id"))
                .collect(Collectors.toList());
    }
}
