package com.ratacheski.minhasfinancas.service;

import com.ratacheski.minhasfinancas.model.entity.Usuario;

public interface UsuarioService {
    Usuario autenticarUsuario(String email, String senha);

    Usuario salvarUsuario(Usuario usuario);

    void validarEmail(String email);
}
