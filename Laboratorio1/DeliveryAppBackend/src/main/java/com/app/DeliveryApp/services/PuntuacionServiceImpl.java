package com.app.DeliveryApp.services;

import com.app.DeliveryApp.models.Puntuacion;
import com.app.DeliveryApp.repositories.PuntuacionRepository;
import com.app.DeliveryApp.repositories.RepartidorRepository; // Para validar repartidor
// Podría necesitar RepartidorService si la lógica de actualizar promedio es compleja
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PuntuacionServiceImpl implements PuntuacionService {

    private final PuntuacionRepository puntuacionRepository;
    private final RepartidorRepository repartidorRepository; // Para validar

    @Autowired
    public PuntuacionServiceImpl(PuntuacionRepository puntuacionRepository, RepartidorRepository repartidorRepository) {
        this.puntuacionRepository = puntuacionRepository;
        this.repartidorRepository = repartidorRepository;
    }

    @Override
    @Transactional // Si se actualiza el promedio del repartidor
    public Puntuacion crearPuntuacion(Puntuacion puntuacion) {
        // Validar que el repartidor exista
        if(repartidorRepository.findByRut(puntuacion.getRutRepartidor()).isEmpty()){
            throw new IllegalArgumentException("El repartidor con RUT " + puntuacion.getRutRepartidor() + " no existe");
        }
        // TODO: Validar rango de puntaje (ej. 1.0 a 5.0)
        Puntuacion nuevaPuntuacion = puntuacionRepository.save(puntuacion);
        // TODO: Llamar a método para recalcular y guardar promedio del repartidor
        return nuevaPuntuacion;
    }

    @Override
    public Optional<Puntuacion> obtenerPuntuacionPorId(Long id) {
        return puntuacionRepository.findById(id);
    }

    @Override
    public List<Puntuacion> obtenerTodasLasPuntuaciones() {
        return puntuacionRepository.findAll();
    }

    @Override
    public List<Puntuacion> obtenerPuntuacionesPorRepartidor(String rutRepartidor) {
        return puntuacionRepository.findByRutRepartidor(rutRepartidor);
    }

    @Override
    @Transactional
    public Optional<Puntuacion> actualizarPuntuacion(Long id, Puntuacion puntuacionActualizada) {
        Optional<Puntuacion> puntuacionExistenteOpt = puntuacionRepository.findById(id);

        if(puntuacionExistenteOpt.isPresent()) {
            Puntuacion puntuacionParaActualizar = puntuacionExistenteOpt.get();
            String rutOriginal = puntuacionParaActualizar.getRutRepartidor();

            if (!rutOriginal.equals(puntuacionActualizada.getRutRepartidor()) &&
                    repartidorRepository.findByRut(puntuacionActualizada.getRutRepartidor()).isEmpty()) {
                throw new IllegalArgumentException("Nuevo Repartidor con RUT " + puntuacionActualizada.getRutRepartidor() + " no existente");
            }

            puntuacionParaActualizar.setPuntaje(puntuacionActualizada.getPuntaje());
            puntuacionParaActualizar.setComentario(puntuacionActualizada.getComentario());
            puntuacionParaActualizar.setRutRepartidor(puntuacionActualizada.getRutRepartidor());

            puntuacionRepository.update(puntuacionParaActualizar);

            return Optional.of(puntuacionParaActualizar);
        } else {
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public boolean eliminarPuntuacion(Long id) {
        Optional<Puntuacion> puntuacionOpt = puntuacionRepository.findById(id);
        if (puntuacionOpt.isPresent()) {
            String rutRepartidor = puntuacionOpt.get().getRutRepartidor();
            int filasAfectadas = puntuacionRepository.deleteById(id);
            if (filasAfectadas > 0) {
                // TODO: Llamar y crear un metodo para recalcular y guardar el promedio del repartidor
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}