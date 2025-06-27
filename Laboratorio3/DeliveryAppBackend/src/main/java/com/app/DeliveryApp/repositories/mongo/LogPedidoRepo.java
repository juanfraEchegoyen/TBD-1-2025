package com.app.DeliveryApp.repositories.mongo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
@Repository
public class LogPedidoRepo {
    @Autowired
    private MongoTemplate mongoTemplate;
    public List<Map<String, Object>> findPedidosConMasDe3CambiosEn10Min() {
        List<Map> pedidos = mongoTemplate.findAll(Map.class, "logs_pedidos");
        List<Map<String, Object>> result = new java.util.ArrayList<>();
        for (Map pedido : pedidos) {
            List<Map> historial = (List<Map>) pedido.get("historial_estados");
            if (historial == null || historial.size() < 4) continue;
            historial.sort((a, b) -> ((String)a.get("timestamp")).compareTo((String)b.get("timestamp")));
            for (int i = 0; i <= historial.size() - 4; i++) {
                Instant t0 = Instant.parse((String) historial.get(i).get("timestamp"));
                Instant t3 = Instant.parse((String) historial.get(i + 3).get("timestamp"));
                if (Duration.between(t0, t3).toMinutes() < 10) {
                    Map<String, Object> match = new java.util.HashMap<>();
                    match.put("pedido_id", pedido.get("pedido_id"));
                    match.put("historial_estados", historial);
                    result.add(match);
                    break;
                }
            }
        }
        return result;
    }
}
