package com.app.DeliveryApp.models.mongo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "navegacion_usuarios")
public class NavegacionUsuario {
    
    @Id
    private String id;    @Field("cliente_id")
    private String clienteId;
    
    private List<EventoNavegacion> eventos;
    
    public NavegacionUsuario() {}
    
    public NavegacionUsuario(String clienteId, List<EventoNavegacion> eventos) {
        this.clienteId = clienteId;
        this.eventos = eventos;
    }
    
    @Data
    public static class EventoNavegacion {
        private String tipo;
        private String valor;
        private LocalDateTime timestamp;
        
        public EventoNavegacion() {}
        
        public EventoNavegacion(String tipo, String valor, LocalDateTime timestamp) {
            this.tipo = tipo;
            this.valor = valor;
            this.timestamp = timestamp;
        }
    }
}
