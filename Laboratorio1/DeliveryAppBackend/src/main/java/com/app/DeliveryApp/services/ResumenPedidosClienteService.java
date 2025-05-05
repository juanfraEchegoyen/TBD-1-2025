package com.app.DeliveryApp.services;

import com.app.DeliveryApp.models.views.ResumenPedidosCliente;
import com.app.DeliveryApp.repositories.ResumenPedidosClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ResumenPedidosClienteService {
    @Autowired
    private ResumenPedidosClienteRepository repository;

    public List<ResumenPedidosCliente> getResumen() {
        return repository.findAll();
    }
}