package com.app.DeliveryApp.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.locationtech.jts.geom.Coordinate;

public class GeoJsonParser {

    private final ObjectMapper objectMapper;

    public GeoJsonParser() {
        this.objectMapper = new ObjectMapper();
    }

    public Coordinate[] parseCoordinates(String geoJson) throws Exception {
        System.out.println("Parseando GeoJSON: " + geoJson); // **
        // Parsear el GeoJSON
        JsonNode root = objectMapper.readTree(geoJson);
        JsonNode coordinatesNode = root.path("routes").get(0).path("geometry").path("coordinates");

        if (!coordinatesNode.isArray()) {
            throw new IllegalArgumentException("El formato GeoJSON no contiene coordenadas válidas.");
        }

        // Convertir las coordenadas en objetos Coordinate de JTS
        Coordinate[] coordinates = new Coordinate[coordinatesNode.size()];
        for (int i = 0; i < coordinatesNode.size(); i++) {
            JsonNode coordinate = coordinatesNode.get(i);
            double lon = coordinate.get(0).asDouble();
            double lat = coordinate.get(1).asDouble();
            coordinates[i] = new Coordinate(lon, lat);
        }
        System.out.print("Coordenadas parseadas: " + coordinates); // **
        return coordinates;
    }
}