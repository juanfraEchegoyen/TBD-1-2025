package com.app.DeliveryApp.repositories;

import com.app.DeliveryApp.models.views.DesempenoRepartidor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DesempenoRepartidorRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<DesempenoRepartidor> findAll() {
        String sql = "SELECT rut_repartidor, nombre_repartidor, entregas_realizadas, puntuacion_promedio " +
                "FROM vista_desempeno_repartidor";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            DesempenoRepartidor resumen = new DesempenoRepartidor();
            resumen.setRutRepartidor(rs.getString("rut_repartidor"));
            resumen.setNombreRepartidor(rs.getString("nombre_repartidor"));
            resumen.setEntregasRealizadas(rs.getInt("entregas_realizadas"));
            resumen.setPuntuacionPromedio(rs.getDouble("puntuacion_promedio"));
            return resumen;
        });
    }
}