package com.app.DeliveryApp.services;

import com.app.DeliveryApp.utils.GeoJsonParser;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class OSMRService {

    private final RestTemplate restTemplate;
    private final GeometryFactory geometryFactory;

    public OSMRService() {
        this.restTemplate = new RestTemplate();
        this.geometryFactory = new GeometryFactory();
    }

    public LineString obtenerRutaLineString(double inicioLat, double inicioLon, double finLat, double finLon) {
        System.out.println("Obteniendo ruta desde OSMR: " + "Inicio (" + inicioLat + ", " + inicioLon + "), " + "Fin (" + finLat + ", " + finLon + ")"); //**
        // Construir la URL para OSMR
        String url = UriComponentsBuilder.fromHttpUrl("http://router.project-osrm.org/route/v1/driving/")
                .pathSegment(inicioLon + "," + inicioLat + ";" + finLon + "," + finLat)
                .queryParam("overview", "full")
                .queryParam("geometries", "geojson")
                .toUriString();

        try {
            // Realizar la solicitud HTTP
            String response = restTemplate.getForObject(url, String.class);
            System.out.println("Respuesta de OSMR: " + response); //**

            // Parsear la respuesta (GeoJSON)
            GeoJsonParser parser = new GeoJsonParser();
            Coordinate[] coordinates = parser.parseCoordinates(response);

            // Crear LineString JTS con las coordenadas
            System.out.println("LineString a crear:" + geometryFactory.createLineString(coordinates)); //**
            return geometryFactory.createLineString(coordinates);
        } catch (Exception e) {
            throw new RuntimeException("Error al parsear la respuesta GeoJSON: " + e.getMessage(), e);
        }
    }
}