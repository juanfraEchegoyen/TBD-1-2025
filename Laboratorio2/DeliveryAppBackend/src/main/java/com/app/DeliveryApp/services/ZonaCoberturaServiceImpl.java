package com.app.DeliveryApp.services;

import com.app.DeliveryApp.models.ZonaCobertura;
import com.app.DeliveryApp.repositories.ZonaCoberturaRepository;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ZonaCoberturaServiceImpl implements ZonaCoberturaService {

    private final ZonaCoberturaRepository zonaCoberturaRepository;

    @Autowired
    public ZonaCoberturaServiceImpl(ZonaCoberturaRepository zonaCoberturaRepository) {
        this.zonaCoberturaRepository = zonaCoberturaRepository;
    }

    @Override
    public ZonaCobertura crearZonaCobertura(ZonaCobertura zonaCobertura) {
        return zonaCoberturaRepository.save(zonaCobertura);
    }

    @Override
    public Optional<ZonaCobertura> obtenerZonaCoberturaPorId(Long id) {
        return zonaCoberturaRepository.findById(id);
    }

    @Override
    public List<ZonaCobertura> obtenerTodasLasZonasCobertura() {
        return zonaCoberturaRepository.findAll();
    }

    @Override
    public List<ZonaCobertura> obtenerZonasCoberturaByEmpresa(String rutEmpresa) {
        return zonaCoberturaRepository.findByEmpresaRut(rutEmpresa);
    }

    @Override
    public Optional<ZonaCobertura> actualizarZonaCobertura(Long id, ZonaCobertura zonaActualizada) {
        Optional<ZonaCobertura> zonaExistenteOpt = zonaCoberturaRepository.findById(id);

        if (zonaExistenteOpt.isPresent()) {
            ZonaCobertura zonaParaActualizar = zonaExistenteOpt.get();
            zonaParaActualizar.setNombre(zonaActualizada.getNombre());
            zonaParaActualizar.setAreaCobertura(zonaActualizada.getAreaCobertura());
            zonaParaActualizar.setRutEmpresa(zonaActualizada.getRutEmpresa());

            zonaCoberturaRepository.update(zonaParaActualizar);
            return Optional.of(zonaParaActualizar);
        } else {
            return Optional.empty();
        }
    }    @Override
    public boolean eliminarZonaCobertura(Long id) {
        if (zonaCoberturaRepository.findById(id).isPresent()) {
            int filasAfectadas = zonaCoberturaRepository.deleteById(id);
            return filasAfectadas > 0;
        } else {
            return false;
        }
    }

    @Override
    public List<ZonaCobertura> obtenerZonasQueContienenPunto(Point punto) {
        return zonaCoberturaRepository.findZonasQueContienenPunto(punto);
    }

    @Override
    public boolean estaEnZonaCobertura(String rutEmpresa, Point puntoEntrega) {
        return zonaCoberturaRepository.verificarCobertura(rutEmpresa, puntoEntrega);
    }
}
