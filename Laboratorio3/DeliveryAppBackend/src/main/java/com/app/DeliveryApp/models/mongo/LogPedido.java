package com.app.DeliveryApp.models.mongo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "logs_pedidos")
public class LogPedido {
      @Id
    private String id;
    
    @Field("pedido_id")
    private String pedidoId;
    
    @Field("historial_estados")
    private List<EventoPedido> historialEstados;
    
    public LogPedido() {}
    
    public LogPedido(String pedidoId, List<EventoPedido> historialEstados) {
        this.pedidoId = pedidoId;
        this.historialEstados = historialEstados;
    }
    
    @Data
    public static class EventoPedido {
        private LocalDateTime timestamp;
        private String estado;
        
        public EventoPedido() {}
        
        public EventoPedido(LocalDateTime timestamp, String estado) {
            this.timestamp = timestamp;
            this.estado = estado;
        }
    }
}
