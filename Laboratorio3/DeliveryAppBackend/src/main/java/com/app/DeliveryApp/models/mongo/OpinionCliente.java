package com.app.DeliveryApp.models.mongo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

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

    private Instant fecha;

}
