package com.datamarkets.app.repository;

import com.datamarkets.app.model.Usuario;

public interface UsuarioCallback {
    void onExito(Usuario usuario);
    void onError(String mensaje);
}