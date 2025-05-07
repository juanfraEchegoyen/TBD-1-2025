package com.app.DeliveryApp.services;

import com.app.DeliveryApp.models.views.EmpresaPedidos;
import com.app.DeliveryApp.repositories.EmpresaPedidosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmpresaPedidosService {
    @Autowired
    private EmpresaPedidosRepository repository;

    public List<EmpresaPedidos> getResumen() {
        return repository.findAll();
    }
}
