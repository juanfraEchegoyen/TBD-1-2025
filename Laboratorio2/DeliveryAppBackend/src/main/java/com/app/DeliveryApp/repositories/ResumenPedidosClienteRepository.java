package com.app.DeliveryApp.repositories;

import com.app.DeliveryApp.models.views.ResumenPedidosCliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class ResumenPedidosClienteRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<ResumenPedidosCliente> findAll() {
        String sql = "SELECT rut_cliente, nombre_cliente, cantidad_pedidos, monto_total FROM vista_pedidos_por_cliente";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            ResumenPedidosCliente resumen = new ResumenPedidosCliente();
            resumen.setRutCliente(rs.getString("rut_cliente"));
            resumen.setNombreCliente(rs.getString("nombre_cliente"));
            resumen.setCantidadPedidos(rs.getInt("cantidad_pedidos"));
            resumen.setMontoTotal(rs.getInt("monto_total"));
            return resumen;
        });
    }
}