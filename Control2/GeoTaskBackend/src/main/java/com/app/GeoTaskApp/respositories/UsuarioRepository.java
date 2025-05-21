package com.app.GeoTaskApp.respositories;


import com.app.GeoTaskApp.Models.Usuario;

import java.util.Optional;

public interface UsuarioRepository {

    Optional<Usuario> findByNombre(String nombre);
    Usuario save(Usuario usuario);
    Optional<Usuario> findById(Integer id);
    int update(Usuario usuario);
    int deleteById(Integer id);
}
