package com.app.DeliveryApp.repositories;

import com.app.DeliveryApp.models.*;
import com.app.DeliveryApp.services.OSMRService;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
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
public class JdbcPedidoRepository implements PedidoRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private OSMRService osmrService;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private RepartidorRepository repartidorRepository;

    private final WKTReader wktReader = new WKTReader();
    private final WKTWriter wktWriter = new WKTWriter();
    
    private static final String INSERT_PEDIDO_SQL_RETURNING_ID =
            "INSERT INTO Pedido (estado_entrega, prioridad_pedido, problema_critico, rut_cliente, rut_empresa, rut_repartidor, rutas_estimadas) VALUES (?, ?, ?, ?, ?, ?, ST_GeomFromText(?, 4326)) RETURNING id_pedido";
    private static final String SELECT_PEDIDO_BY_ID_SQL =
            "SELECT id_pedido, estado_entrega, prioridad_pedido, problema_critico, rut_cliente, rut_empresa, rut_repartidor, ST_AsText(rutas_estimadas) as rutas_wkt FROM Pedido WHERE id_pedido = ?";
    private static final String SELECT_ALL_PEDIDOS_SQL =
            "SELECT id_pedido, estado_entrega, prioridad_pedido, problema_critico, rut_cliente, rut_empresa, rut_repartidor, ST_AsText(rutas_estimadas) as rutas_wkt FROM Pedido";
    private static final String UPDATE_PEDIDO_SQL =
            "UPDATE Pedido SET estado_entrega = ?, prioridad_pedido = ?, problema_critico = ?, rut_cliente = ?, rut_empresa = ?, rut_repartidor = ?, rutas_estimadas = ST_GeomFromText(?, 4326) WHERE id_pedido = ?";
    private static final String DELETE_PEDIDO_BY_ID_SQL =
            "DELETE FROM Pedido WHERE id_pedido = ?";

    private final RowMapper<Pedido> pedidoRowMapper = (rs, rowNum) -> {
        Pedido pedido = new Pedido();
        pedido.setIdPedido(rs.getLong("id_pedido"));
        pedido.setEstadoEntrega(rs.getString("estado_entrega"));
        pedido.setPrioridadPedido(rs.getString("prioridad_pedido"));
        pedido.setProblemaCritico(rs.getBoolean("problema_critico"));
        pedido.setRutCliente(rs.getString("rut_cliente"));
        pedido.setRutEmpresa(rs.getString("rut_empresa"));
        pedido.setRutRepartidor(rs.getString("rut_repartidor"));
        
        // Conversión de WKT a LineString para rutas estimadas
        String rutasWkt = rs.getString("rutas_wkt");
        if (rutasWkt != null) {
            try {
                pedido.setRutasEstimadas((LineString) wktReader.read(rutasWkt));
            } catch (Exception e) {
                System.err.println("Error al convertir WKT a LineString: " + e.getMessage());
                pedido.setRutasEstimadas(null);
            }
        } else {
            pedido.setRutasEstimadas(null);
        }
        
        return pedido;
    };

    @Override
    public Pedido save(Pedido pedido) {
        try {
            String rutasWkt = null;
            if (pedido.getRutasEstimadas() != null) {
                rutasWkt = wktWriter.write(pedido.getRutasEstimadas());
            }
            
            Long generatedId = jdbcTemplate.queryForObject(
                    INSERT_PEDIDO_SQL_RETURNING_ID,
                    Long.class,
                    pedido.getEstadoEntrega(),
                    pedido.getPrioridadPedido(),
                    pedido.isProblemaCritico(),
                    pedido.getRutCliente(),
                    pedido.getRutEmpresa(),
                    pedido.getRutRepartidor(),
                    rutasWkt
            );

            if (generatedId != null) {
                pedido.setIdPedido(generatedId);
            } else {
                System.err.println("error queryForObject con returning devolvio null");
                throw new RuntimeException("No se pudo obtener el id generado para pedido");
            }
        } catch (Exception e) {
            System.err.println("Error al guardar pedido y obtener el id con returning: " + e.getMessage());
            throw new RuntimeException("Error en la BD al guardar el pedido", e);
        }

        return pedido;
    }


    public void RegistrarPedido(Pedido pedido, DetallePedido detalle, MedioPago medioPago) {
        String sql = "CALL registrar_pedido(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ST_GeomFromText(?, 4326))";

        // Convertir Double a Integer para precio_total
        Integer precioTotal = detalle.getPrecioTotal().intValue();

        // Convertir java.util.Date a java.sql.Date
        java.sql.Date sqlFechaEntrega = new java.sql.Date(detalle.getFechaEntrega().getTime());

        // Convertir Long a Integer para idProducto
        Integer idProducto = detalle.getIdProducto().intValue();

        // Calcular ruta estimada automáticamente
        LineString rutaCalculada = calcularRutaEstimada(pedido.getRutCliente(), pedido.getRutRepartidor());

        // Convertir LineString a WKT
        String rutaCalculadaWkt = null;
        if (rutaCalculada != null) {
            rutaCalculadaWkt = wktWriter.write(rutaCalculada);
            pedido.setRutasEstimadas(rutaCalculada);
        }

        jdbcTemplate.update(sql,
                pedido.getEstadoEntrega(),
                pedido.getPrioridadPedido(),
                pedido.isProblemaCritico(),
                pedido.getRutCliente(),
                pedido.getRutEmpresa(),
                pedido.getRutRepartidor(),
                precioTotal,           // Double → Integer
                detalle.getTiempoEntrega(),
                sqlFechaEntrega,       // java.util.Date → java.sql.Date
                detalle.getCantidad(),
                idProducto,             // Long → Integer
                medioPago.getNombreMedioPago(),
                rutaCalculadaWkt
        );
    }

    public void actualizarEstadoPedido(Integer idPedido, String nuevoEstado) {
        String sql = "CALL actualizar_estado_pedido(?, ?)";
        jdbcTemplate.update(sql, idPedido, nuevoEstado);
    }

    public void Aumentar_stock_al_fallar(Integer idPedido) {
        String sql = "CALL Aumentar_stock_al_fallar(?)";
        jdbcTemplate.update(sql, idPedido);
    }

    public void descontar_stock_al_confirmar(Integer idPedido) {
        String sql = "CALL descontar_stock_al_confirmar(?)";
        jdbcTemplate.update(sql, idPedido);
    }

    public LineString calcularRutaEstimada(String rutCliente, String rutRepartidor) {
        System.out.println("Calculando ruta estimada entre cliente: " + rutCliente + " y repartidor: " + rutRepartidor); //**
        try {
            if (rutCliente == null || rutRepartidor == null) {
                System.out.println("RUT de cliente o repartidor es nulo, no se puede calcular ruta");
                return null;
            }

            // Obtener ubicaciones del cliente y repartidor usando sus repositorios
            Optional<Cliente> clienteOpt = clienteRepository.findByRut(rutCliente);
            Optional<Repartidor> repartidorOpt = repartidorRepository.findByRut(rutRepartidor);

            if (clienteOpt.isEmpty() || repartidorOpt.isEmpty()) {
                System.err.println("No se encontró cliente o repartidor para calcular ruta");
                return null;
            }
            System.out.println("Cliente y repartidor encontrados, calculando ruta..."); //**
            Cliente cliente = clienteOpt.get();
            Repartidor repartidor = repartidorOpt.get();

            Point ubicacionCliente = cliente.getUbicacion();
            Point ubicacionRepartidor = repartidor.getUbicacion();

            System.out.println("Ubicación del cliente: " + ubicacionCliente); //**
            System.out.println("Ubicación del repartidor: " + ubicacionRepartidor); //**

            if (ubicacionCliente == null || ubicacionRepartidor == null) {
                System.err.println("Cliente o repartidor no tienen ubicación definida");
                return null;
            }

            // Coordenadas del repartidor (origen)
            double repartidorLat = ubicacionRepartidor.getY();
            double repartidorLon = ubicacionRepartidor.getX();

            // Coordenadas del cliente (destino)
            double clienteLat = ubicacionCliente.getY();
            double clienteLon = ubicacionCliente.getX();

            System.out.println("Calculando ruta desde repartidor (" + repartidorLat + "," + repartidorLon +
                    ") hacia cliente (" + clienteLat + "," + clienteLon + ")");

            // Calcular ruta usando OSMR
            return osmrService.obtenerRutaLineString(repartidorLat, repartidorLon, clienteLat, clienteLon);

        } catch (Exception e) {
            System.err.println("Error al calcular ruta estimada: " + e.getMessage());
            return null;
        }
    }


    @Override
    public Optional<Pedido> findById(Long id) {
        if (id == null) return Optional.empty();
        try {
            Pedido pedido = jdbcTemplate.queryForObject(SELECT_PEDIDO_BY_ID_SQL, pedidoRowMapper, id);
            return Optional.of(pedido);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Pedido> findAll() {
        return jdbcTemplate.query(SELECT_ALL_PEDIDOS_SQL, pedidoRowMapper);
    }

    @Override
    public int update(Pedido pedido) {
        if (pedido == null || pedido.getIdPedido() == null) {
            throw new IllegalArgumentException("Pedido o Id pedido no pueden ser nulos para el update");
        }

        try {
            // Calcular ruta estimada automáticamente
            LineString rutaCalculada = calcularRutaEstimada(pedido.getRutCliente(), pedido.getRutRepartidor());
            if (rutaCalculada != null) {
                pedido.setRutasEstimadas(rutaCalculada);
            }

            // Convertir rutas estimadas a WKT
            String rutasWkt = null;
            if (pedido.getRutasEstimadas() != null) {
                rutasWkt = wktWriter.write(pedido.getRutasEstimadas());
            }

            // Actualizar el pedido en la base de datos
            return jdbcTemplate.update(UPDATE_PEDIDO_SQL,
                    pedido.getEstadoEntrega(),
                    pedido.getPrioridadPedido(),
                    pedido.isProblemaCritico(),
                    pedido.getRutCliente(),
                    pedido.getRutEmpresa(),
                    pedido.getRutRepartidor(),
                    rutasWkt,
                    pedido.getIdPedido());
        } catch (Exception e) {
            System.err.println("Error al actualizar el pedido: " + e.getMessage());
            throw new RuntimeException("Error en la BD al actualizar el pedido", e);
        }
    }

    @Override
    public int deleteById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID pedido no puede ser nulo para eliminar");
        }
        return jdbcTemplate.update(DELETE_PEDIDO_BY_ID_SQL, id);
    }

    @Override
    public int countByRutCliente(String rutCliente) {
        String sql = "SELECT COUNT(*) FROM Pedido WHERE rut_cliente = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, rutCliente);
        return count != null ? count : 0;
    }

    @Override
    public int countByRutClienteAndEstado(String rutCliente, String estado) {
        String sql = "SELECT COUNT(*) FROM Pedido WHERE rut_cliente = ? AND estado_entrega = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, rutCliente, estado);
        return count != null ? count : 0;
    }
}