package com.app.DeliveryApp.services;

import com.app.DeliveryApp.models.views.DesempenoRepartidor;
import com.app.DeliveryApp.repositories.DesempenoRepartidorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DesempenoRepartidorService {
    @Autowired
    private DesempenoRepartidorRepository repository;

    public List<DesempenoRepartidor> getResumen() {
        return repository.findAll();
    }
}
