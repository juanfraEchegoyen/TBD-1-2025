package com.app.DeliveryApp.services;

import com.app.DeliveryApp.models.Cliente;
import java.util.List;
import java.util.Optional;

public interface ClienteService {
    Cliente crearCliente(Cliente cliente);
    Optional<Cliente> obtenerClientePorRut(String rut);
    List<Cliente> obtenerTodosLosClientes();
    Optional<Cliente> actualizarCliente(String rut, Cliente clienteActualizado);
    boolean eliminarCliente(String rut);
    double calcularRiesgoCliente(String rutCliente);
}