package com.app.DeliveryApp.repositories;

import com.app.DeliveryApp.models.Producto; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcProductoRepository implements ProductoRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String INSERT_PRODUCTO_SQL =
            "INSERT INTO Producto (nombre_producto, precio, categoria, tipo_producto, stock) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_PRODUCTO_BY_ID_SQL =
            "SELECT id_producto, nombre_producto, precio, categoria, tipo_producto, stock FROM Producto WHERE id_producto = ?";
    private static final String SELECT_ALL_PRODUCTOS_SQL =
            "SELECT id_producto, nombre_producto, precio, categoria, tipo_producto, stock FROM Producto";
    private static final String UPDATE_PRODUCTO_SQL =
            "UPDATE Producto SET nombre_producto = ?, precio = ?, categoria = ?, tipo_producto = ?, stock = ? WHERE id_producto = ?";
    private static final String DELETE_PRODUCTO_BY_ID_SQL =
            "DELETE FROM Producto WHERE id_producto = ?";

    private final RowMapper<Producto> productoRowMapper = (rs, rowNum) -> {
        Producto producto = new Producto();
        producto.setIdProducto(rs.getLong("id_producto"));

        producto.setNombre(rs.getString("nombre_producto"));

        int precioInt = rs.getInt("precio");
        if (!rs.wasNull()) {
            producto.setPrecio((double) precioInt);
        } else {
            producto.setPrecio(null);
        }

        producto.setCategoria(rs.getString("categoria"));
        producto.setTipoProducto(rs.getString("tipo_producto"));

        producto.setStock(rs.getInt("stock"));


        return producto;
    };

    @Override
    public Producto save(Producto producto) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_PRODUCTO_SQL, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, producto.getNombre());

            if (producto.getPrecio() != null) {
                ps.setInt(2, producto.getPrecio().intValue());
            } else {
                ps.setNull(2, Types.INTEGER);
            }


            ps.setString(3, producto.getCategoria());
            ps.setString(4, producto.getTipoProducto());
            ps.setInt(5, producto.getStock());


            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            producto.setIdProducto(keyHolder.getKey().longValue());
        } else {
            System.err.println("No se pudo obtener el ID generado para el producto");
        }
        return producto;
    }

    @Override
    public Optional<Producto> findById(Long id) {
        try {
            Producto producto = jdbcTemplate.queryForObject(SELECT_PRODUCTO_BY_ID_SQL, new Object[]{id}, productoRowMapper);
            return Optional.of(producto);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Producto> findAll() {
        return jdbcTemplate.query(SELECT_ALL_PRODUCTOS_SQL, productoRowMapper);
    }

    @Override
    public int update(Producto producto) {
        if (producto == null || producto.getIdProducto() == null) {
            throw new IllegalArgumentException("Producto o id producto no pueden ser nulos para la actualización");
        }
        return jdbcTemplate.update(UPDATE_PRODUCTO_SQL,
                producto.getNombre(),
                producto.getPrecio() != null ? producto.getPrecio().intValue() : null,
                producto.getCategoria(),
                producto.getTipoProducto(),
                producto.getStock(),
                producto.getIdProducto());
    }

    @Override
    public int deleteById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID producto no puede ser nulo para eliminar");
        }
        return jdbcTemplate.update(DELETE_PRODUCTO_BY_ID_SQL, id);
    }
}