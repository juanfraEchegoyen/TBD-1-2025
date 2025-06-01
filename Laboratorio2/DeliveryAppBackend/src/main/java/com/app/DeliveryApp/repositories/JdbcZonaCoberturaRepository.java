package com.app.DeliveryApp.repositories;

import com.app.DeliveryApp.models.ZonaCobertura;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JdbcZonaCoberturaRepository implements ZonaCoberturaRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private final WKTReader wktReader = new WKTReader();
    private final WKTWriter wktWriter = new WKTWriter();

    private static final String INSERT_ZONA_SQL_RETURNING_ID =
            "INSERT INTO ZonaCobertura (nombre_zona, descripcion, rut_empresa, area_cobertura) VALUES (?, ?, ?, ST_GeomFromText(?, 4326)) RETURNING id_zona";
    private static final String SELECT_ZONA_BY_ID_SQL =
            "SELECT id_zona, nombre_zona, descripcion, rut_empresa, ST_AsText(area_cobertura) as area_wkt FROM ZonaCobertura WHERE id_zona = ?";
    private static final String SELECT_ALL_ZONAS_SQL =
            "SELECT id_zona, nombre_zona, descripcion, rut_empresa, ST_AsText(area_cobertura) as area_wkt FROM ZonaCobertura";
    private static final String UPDATE_ZONA_SQL =
            "UPDATE ZonaCobertura SET nombre_zona = ?, descripcion = ?, rut_empresa = ?, area_cobertura = ST_GeomFromText(?, 4326) WHERE id_zona = ?";
    private static final String DELETE_ZONA_BY_ID_SQL =
            "DELETE FROM ZonaCobertura WHERE id_zona = ?";    private static final String SELECT_ZONAS_BY_EMPRESA_SQL =
            "SELECT id_zona, nombre_zona, descripcion, rut_empresa, ST_AsText(area_cobertura) as area_wkt FROM ZonaCobertura WHERE rut_empresa = ?";
    
    // Consultas espaciales
    private static final String SELECT_ZONAS_QUE_CONTIENEN_PUNTO_SQL =
            "SELECT id_zona, nombre_zona, descripcion, rut_empresa, ST_AsText(area_cobertura) as area_wkt " +
            "FROM ZonaCobertura " +
            "WHERE ST_Contains(area_cobertura, ST_GeomFromText(?, 4326))";
    
    private static final String VERIFICAR_COBERTURA_SQL =
            "SELECT COUNT(*) > 0 " +
            "FROM ZonaCobertura " +
            "WHERE rut_empresa = ? AND ST_Contains(area_cobertura, ST_GeomFromText(?, 4326))";

    private final RowMapper<ZonaCobertura> zonaCoberturaRowMapper = (rs, rowNum) -> {
        ZonaCobertura zona = new ZonaCobertura();
        zona.setIdZona(rs.getLong("id_zona"));
        zona.setNombreZona(rs.getString("nombre_zona"));
        zona.setDescripcion(rs.getString("descripcion"));
        zona.setRutEmpresa(rs.getString("rut_empresa"));
        
        // Conversión de WKT a Polygon
        String areaWkt = rs.getString("area_wkt");
        if (areaWkt != null) {
            try {
                zona.setAreaCobertura((Polygon) wktReader.read(areaWkt));
            } catch (Exception e) {
                System.err.println("Error al convertir WKT a Polygon: " + e.getMessage());
                zona.setAreaCobertura(null);
            }
        } else {
            zona.setAreaCobertura(null);
        }
        
        return zona;
    };

    @Override
    public ZonaCobertura save(ZonaCobertura zonaCobertura) {
        try {
            String areaWkt = null;
            if (zonaCobertura.getAreaCobertura() != null) {
                areaWkt = wktWriter.write(zonaCobertura.getAreaCobertura());
            }
            
            Long generatedId = jdbcTemplate.queryForObject(
                    INSERT_ZONA_SQL_RETURNING_ID,
                    Long.class,
                    zonaCobertura.getNombreZona(),
                    zonaCobertura.getDescripcion(),
                    zonaCobertura.getRutEmpresa(),
                    areaWkt
            );

            if (generatedId != null) {
                zonaCobertura.setIdZona(generatedId);
            } else {
                throw new RuntimeException("No se pudo obtener el id generado para zona de cobertura");
            }
        } catch (Exception e) {
            System.err.println("Error al guardar zona de cobertura: " + e.getMessage());
            throw new RuntimeException("Error en la BD al guardar la zona de cobertura", e);
        }

        return zonaCobertura;
    }

    @Override
    public Optional<ZonaCobertura> findById(Long id) {
        if (id == null) return Optional.empty();
        try {
            ZonaCobertura zona = jdbcTemplate.queryForObject(SELECT_ZONA_BY_ID_SQL, zonaCoberturaRowMapper, id);
            return Optional.of(zona);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<ZonaCobertura> findAll() {
        return jdbcTemplate.query(SELECT_ALL_ZONAS_SQL, zonaCoberturaRowMapper);
    }

    @Override
    public int update(ZonaCobertura zonaCobertura) {
        if (zonaCobertura == null || zonaCobertura.getIdZona() == null) {
            throw new IllegalArgumentException("ZonaCobertura o ID no pueden ser nulos para el update");
        }
        
        String areaWkt = null;
        if (zonaCobertura.getAreaCobertura() != null) {
            areaWkt = wktWriter.write(zonaCobertura.getAreaCobertura());
        }
        
        return jdbcTemplate.update(UPDATE_ZONA_SQL,
                zonaCobertura.getNombreZona(),
                zonaCobertura.getDescripcion(),
                zonaCobertura.getRutEmpresa(),
                areaWkt,
                zonaCobertura.getIdZona());
    }

    @Override
    public int deleteById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID zona no puede ser nulo para eliminar");
        }
        return jdbcTemplate.update(DELETE_ZONA_BY_ID_SQL, id);
    }    @Override
    public List<ZonaCobertura> findByEmpresaRut(String rutEmpresa) {
        if (rutEmpresa == null) {
            throw new IllegalArgumentException("RUT empresa no puede ser nulo");
        }
        return jdbcTemplate.query(SELECT_ZONAS_BY_EMPRESA_SQL, zonaCoberturaRowMapper, rutEmpresa);
    }

    @Override
    public List<ZonaCobertura> findZonasQueContienenPunto(Point punto) {
        if (punto == null) {
            throw new IllegalArgumentException("El punto no puede ser nulo");
        }
        
        try {
            String puntoWkt = wktWriter.write(punto);
            return jdbcTemplate.query(SELECT_ZONAS_QUE_CONTIENEN_PUNTO_SQL, zonaCoberturaRowMapper, puntoWkt);
        } catch (Exception e) {
            System.err.println("Error al buscar zonas que contienen punto: " + e.getMessage());
            throw new RuntimeException("Error en consulta espacial para zonas que contienen punto", e);
        }
    }

    @Override
    public boolean verificarCobertura(String rutEmpresa, Point puntoEntrega) {
        if (rutEmpresa == null || puntoEntrega == null) {
            throw new IllegalArgumentException("RUT empresa y punto de entrega no pueden ser nulos");
        }
        
        try {
            String puntoWkt = wktWriter.write(puntoEntrega);
            Boolean resultado = jdbcTemplate.queryForObject(VERIFICAR_COBERTURA_SQL, Boolean.class, rutEmpresa, puntoWkt);
            return resultado != null && resultado;
        } catch (Exception e) {
            System.err.println("Error al verificar cobertura: " + e.getMessage());
            return false;
        }
    }
}
