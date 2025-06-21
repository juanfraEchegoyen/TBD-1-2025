package com.app.DeliveryApp.models.mongo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@Document(collection = "opiniones_clientes")
public class OpinionCliente {
    
    @Id
    private String id;
    
    @Field("cliente_id")
    private String clienteId;
    
    @Field("empresa_id")
    private String empresaId;
    
    private String comentario;
    
    private Integer puntuacion;
    
    private LocalDateTime fecha;
    
    public OpinionCliente() {}
    
    public OpinionCliente(String clienteId, String empresaId, String comentario, Integer puntuacion, LocalDateTime fecha) {
        this.clienteId = clienteId;
        this.empresaId = empresaId;
        this.comentario = comentario;
        this.puntuacion = puntuacion;
        this.fecha = fecha;
    }
}
