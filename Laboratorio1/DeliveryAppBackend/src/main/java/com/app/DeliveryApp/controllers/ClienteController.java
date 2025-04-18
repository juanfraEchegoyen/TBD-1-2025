package com.app.DeliveryApp.controllers;

import com.app.DeliveryApp.models.Cliente;
import com.app.DeliveryApp.services.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/clientes")
@CrossOrigin(origins="http://localhost:3000")
public class ClienteController {

    private final ClienteService clienteService;

    @Autowired
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // crear un cliente
    @PostMapping
    public ResponseEntity<Cliente> crearCliente(@RequestBody Cliente cliente) {
        try {
            Cliente nuevoCliente = clienteService.crearCliente(cliente);
            return new ResponseEntity<>(nuevoCliente, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    // obtener cliente por rut
    @GetMapping("/{rut}")
    public ResponseEntity<Cliente> obtenerClientePorRut(@PathVariable String rut) {
        Optional<Cliente> clienteOpt = clienteService.obtenerClientePorRut(rut);

        return clienteOpt.map(cliente -> ResponseEntity.ok(cliente))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // obtener todos los clientes
    @GetMapping
    public ResponseEntity<List<Cliente>> obtenerTodosLosClientes() {
        List<Cliente> clientes = clienteService.obtenerTodosLosClientes();
        return ResponseEntity.ok(clientes);
    }

    // actualizar un cliente por rut
    @PutMapping("/{rut}")
    public ResponseEntity<Cliente> actualizarCliente(@PathVariable String rut, @RequestBody Cliente clienteActualizado) {
        Optional<Cliente> clienteOpt = clienteService.actualizarCliente(rut, clienteActualizado);

        return clienteOpt.map(cliente -> ResponseEntity.ok(cliente))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // eliminar un cliente
    @DeleteMapping("/{rut}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable String rut) {
        boolean eliminado = clienteService.eliminarCliente(rut);

        if (eliminado) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}