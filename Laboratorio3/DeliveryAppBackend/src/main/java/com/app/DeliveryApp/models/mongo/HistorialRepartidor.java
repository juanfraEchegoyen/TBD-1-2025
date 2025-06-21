package com.app.DeliveryApp.models.mongo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "historial_repartidores")
public class HistorialRepartidor {
    
    @Id
    private String id;
    
    @Field("repartidor_id")
    private String repartidorId;
    
    private List<Ruta> rutas;
    
    public HistorialRepartidor() {}
    
    public HistorialRepartidor(String repartidorId, List<Ruta> rutas) {
        this.repartidorId = repartidorId;
        this.rutas = rutas;
    }
      @Data
    public static class Ruta {
        private Double latitud;
        private Double longitud;
        private LocalDateTime timestamp;
        
        public Ruta() {}
        
        public Ruta(Double latitud, Double longitud, LocalDateTime timestamp) {
            this.latitud = latitud;
            this.longitud = longitud;
            this.timestamp = timestamp;
        }
    }
}
