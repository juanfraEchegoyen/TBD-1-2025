package com.app.DeliveryApp.repositories;
import com.app.DeliveryApp.models.views.EmpresaPedidos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public class EmpresaPedidosRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    public List<EmpresaPedidos> findAll() {
        String sql = "SELECT rut_empresa, nombre_empresa, cantidad_pedidos FROM vista_empresas_mayor_pedidos";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            EmpresaPedidos resumen = new EmpresaPedidos();
            resumen.setRutEmpresa(rs.getString("rut_empresa"));
            resumen.setNombreEmpresa(rs.getString("nombre_empresa"));
            resumen.setCantidadPedidos(rs.getInt("cantidad_pedidos"));
            return resumen;
        });
    }

}
