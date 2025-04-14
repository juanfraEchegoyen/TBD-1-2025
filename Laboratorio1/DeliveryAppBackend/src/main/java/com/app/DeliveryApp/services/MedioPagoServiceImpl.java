package com.app.DeliveryApp.services;

import com.app.DeliveryApp.models.MedioPago;
import com.app.DeliveryApp.repositories.MedioPagoRepository;
import com.app.DeliveryApp.repositories.ClienteRepository; // Para validar cliente
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedioPagoServiceImpl implements MedioPagoService {

    private final MedioPagoRepository medioPagoRepository;
    private final ClienteRepository clienteRepository; // Inyectar para validación

    @Autowired
    public MedioPagoServiceImpl(MedioPagoRepository medioPagoRepository, ClienteRepository clienteRepository) {
        this.medioPagoRepository = medioPagoRepository;
        this.clienteRepository = clienteRepository;
    }

    @Override
    public MedioPago crearMedioPago(MedioPago medioPago) {
        // Validar que el cliente asociado exista
        if (clienteRepository.findByRut(medioPago.getRutCliente()).isEmpty()){
            throw new IllegalArgumentException("Cliente con RUT " + medioPago.getRutCliente() + " no existe.");
        }
        return medioPagoRepository.save(medioPago);
    }

    @Override
    public Optional<MedioPago> obtenerMedioPagoPorId(Long id) {
        return medioPagoRepository.findById(id);
    }

    @Override
    public List<MedioPago> obtenerTodosLosMediosPago() {
        return medioPagoRepository.findAll();
    }

    @Override
    public List<MedioPago> obtenerMediosPagoPorCliente(String rutCliente) {
        // Opcional: Validar que el cliente exista primero
        // if(clienteRepository.findByRut(rutCliente).isEmpty()){ ... }
        return medioPagoRepository.findByRutCliente(rutCliente);
    }

    @Override
    public Optional<MedioPago> actualizarMedioPago(Long id, MedioPago medioPagoActualizado) {
        Optional<MedioPago> medioPagoExistenteOpt = medioPagoRepository.findById(id);

        if (medioPagoExistenteOpt.isPresent()) {
            // Validar que el nuevo cliente exista si se cambia
            if (!medioPagoExistenteOpt.get().getRutCliente().equals(medioPagoActualizado.getRutCliente()) &&
                    clienteRepository.findByRut(medioPagoActualizado.getRutCliente()).isEmpty()) {
                throw new IllegalArgumentException("Nuevo Cliente con RUT " + medioPagoActualizado.getRutCliente() + " no existe.");
            }

            MedioPago medioPagoParaActualizar = medioPagoExistenteOpt.get();
            medioPagoParaActualizar.setNombreMedioPago(medioPagoActualizado.getNombreMedioPago());
            medioPagoParaActualizar.setRutCliente(medioPagoActualizado.getRutCliente());

            medioPagoRepository.update(medioPagoParaActualizar);
            return Optional.of(medioPagoParaActualizar);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public boolean eliminarMedioPago(Long id) {
        if (medioPagoRepository.findById(id).isPresent()) {
            int filasAfectadas = medioPagoRepository.deleteById(id);
            return filasAfectadas > 0;
        } else {
            return false;
        }
    }
}