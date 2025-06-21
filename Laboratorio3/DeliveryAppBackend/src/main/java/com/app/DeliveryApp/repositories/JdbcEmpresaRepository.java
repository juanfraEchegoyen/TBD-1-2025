package com.app.DeliveryApp.repositories;

import com.app.DeliveryApp.dto.EmpresaNombreRutDTO;
import com.app.DeliveryApp.models.Empresa;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JdbcEmpresaRepository implements EmpresaRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private final WKTReader wktReader = new WKTReader();
    private final WKTWriter wktWriter = new WKTWriter();

    private static final String INSERT_EMPRESA_SQL =
            "INSERT INTO EmpresaAsociada (rut_empresa, nombre_empresa, ubicacion) VALUES (?, ?, ST_GeomFromText(?, 4326))";
    private static final String SELECT_EMPRESA_BY_RUT_SQL =
            "SELECT rut_empresa, nombre_empresa, ST_AsText(ubicacion) as ubicacion_wkt FROM EmpresaAsociada WHERE rut_empresa = ?";
    private static final String SELECT_ALL_EMPRESAS_SQL =
            "SELECT rut_empresa, nombre_empresa, ST_AsText(ubicacion) as ubicacion_wkt FROM EmpresaAsociada";
    private static final String UPDATE_EMPRESA_SQL =
            "UPDATE EmpresaAsociada SET nombre_empresa = ?, ubicacion = ST_GeomFromText(?, 4326) WHERE rut_empresa = ?";    private static final String DELETE_EMPRESA_BY_RUT_SQL =
            "DELETE FROM EmpresaAsociada WHERE rut_empresa = ?";
            
    private static final String OBTENERNOMBRES_SQL = "SELECT rut_empresa, nombre_empresa FROM empresaasociada ORDER BY rut_empresa ASC "   ;

    private final RowMapper<Empresa> empresaRowMapper = (rs, rowNum) -> {
        Empresa empresa = new Empresa();
        empresa.setRut(rs.getString("rut_empresa"));
        empresa.setNombre(rs.getString("nombre_empresa"));
        
        // Conversión de WKT a Point
        String ubicacionWkt = rs.getString("ubicacion_wkt");
        if (ubicacionWkt != null) {
            try {
                empresa.setUbicacion((Point) wktReader.read(ubicacionWkt));
            } catch (Exception e) {
                System.err.println("Error al convertir WKT a Point: " + e.getMessage());
                empresa.setUbicacion(null);
            }
        } else {
            empresa.setUbicacion(null);
        }
          return empresa;
    };

    @Override
    public Empresa save(Empresa empresa) {
        String ubicacionWkt = null;
        if (empresa.getUbicacion() != null) {
            ubicacionWkt = wktWriter.write(empresa.getUbicacion());
        }

        // Verificar si el punto está dentro de algún polígono
        String checkSql = "SELECT COUNT(*) > 0 FROM ZonaCobertura WHERE ST_Contains(area_cobertura, ST_GeomFromText(?, 4326))";
        Boolean validate = jdbcTemplate.queryForObject(checkSql, Boolean.class, ubicacionWkt);

        if (!validate){
            throw new IllegalArgumentException("La ubicación de la empresa no está dentro de ninguna zona de cobertura válida.");
        }
        
        jdbcTemplate.update(INSERT_EMPRESA_SQL,
                empresa.getRut(),
                empresa.getNombre(),
                ubicacionWkt);
        return empresa;
    }

    @Override
    public Optional<Empresa> findByRut(String rut) {
        try {
            Empresa empresa = jdbcTemplate.queryForObject(SELECT_EMPRESA_BY_RUT_SQL, new Object[]{rut}, empresaRowMapper);
            return Optional.of(empresa);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Empresa> findAll() {
        return jdbcTemplate.query(SELECT_ALL_EMPRESAS_SQL, empresaRowMapper);
    }    @Override
    public int update(Empresa empresa) {
        if (empresa == null || empresa.getRut() == null) { 
            throw new IllegalArgumentException("Empresa o RUT empresa no pueden ser nulos para el update");
        }
        
        String ubicacionWkt = null;
        if (empresa.getUbicacion() != null) {
            ubicacionWkt = wktWriter.write(empresa.getUbicacion());
        }

        // Verificar si el punto está dentro de algún polígono
        String checkSql = "SELECT COUNT(*) > 0 FROM ZonaCobertura WHERE ST_Contains(area_cobertura, ST_GeomFromText(?, 4326))";
        Boolean validate = jdbcTemplate.queryForObject(checkSql, Boolean.class, ubicacionWkt);

        if (!validate){
            throw new IllegalArgumentException("La ubicación de la empresa no está dentro de ninguna zona de cobertura válida.");
        }
        
        return jdbcTemplate.update(UPDATE_EMPRESA_SQL,
                empresa.getNombre(),
                ubicacionWkt,
                empresa.getRut());
    }

    @Override
    public int deleteByRut(String rut) {
        if (rut == null) {
            throw new IllegalArgumentException("RUT empresa no puede ser nulo para eliminar");
        }
        return jdbcTemplate.update(DELETE_EMPRESA_BY_RUT_SQL, rut);
    }

    @Override
    public List<EmpresaNombreRutDTO> ObtenerRutYnombres(){
        try{
            return jdbcTemplate.query(OBTENERNOMBRES_SQL,
                    (rs, rowNum) -> new EmpresaNombreRutDTO(
                            rs.getString("rut_empresa"),
                            rs.getString("nombre_empresa")
                    ));
        }catch (DataAccessException ex) {
            throw new RuntimeException("Error al obtener la lista de empresas", ex);
        }
    }
}