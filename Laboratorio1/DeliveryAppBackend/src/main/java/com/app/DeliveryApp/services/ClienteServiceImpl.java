package com.app.DeliveryApp.services;

import com.app.DeliveryApp.models.Cliente;
import com.app.DeliveryApp.repositories.ClienteRepository;
import com.app.DeliveryApp.repositories.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    public ClienteServiceImpl(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public Cliente crearCliente(Cliente cliente) {
        if (clienteRepository.findByRut(cliente.getRut()).isPresent()) {
            throw new IllegalArgumentException("Cliente con RUT " + cliente.getRut() + " ya existente");
        }
        return clienteRepository.save(cliente);
    }

    @Override
    public Optional<Cliente> obtenerClientePorRut(String rut) {

        return clienteRepository.findByRut(rut);
    }

    @Override
    public List<Cliente> obtenerTodosLosClientes() {
        return clienteRepository.findAll();
    }

    @Override
    public Optional<Cliente> actualizarCliente(String rut, Cliente clienteActualizado) {
        Optional<Cliente> clienteExistenteOpt = clienteRepository.findByRut(rut);

        if (clienteExistenteOpt.isPresent()) {
            Cliente clienteParaActualizar = clienteExistenteOpt.get();
            clienteParaActualizar.setRut(rut);
            clienteParaActualizar.setNombre(clienteActualizado.getNombre());
            clienteParaActualizar.setTelefono(clienteActualizado.getTelefono());
            clienteParaActualizar.setDireccion(clienteActualizado.getDireccion());
            clienteParaActualizar.setComuna(clienteActualizado.getComuna());

            clienteRepository.update(clienteParaActualizar);
            return Optional.of(clienteParaActualizar);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public boolean eliminarCliente(String rut) {
        if (clienteRepository.findByRut(rut).isPresent()) {
            int filasAfectadas = clienteRepository.deleteByRut(rut);
            // true si se elimino
            return filasAfectadas > 0;
        } else {
            return false;
        }
    }

    @Override
    public double calcularRiesgoCliente(String rutCliente) {
        int totalPedidos = pedidoRepository.countByRutCliente(rutCliente);
        int pedidosFallidos = pedidoRepository.countByRutClienteAndEstado(rutCliente, "Entrega fallida");

        if (totalPedidos == 0) return 0.0;
        
        double riesgo = ((double) pedidosFallidos / totalPedidos) * 100;

        return Math.round(riesgo);
    }
}