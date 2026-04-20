package com.datamarkets.app.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.datamarkets.app.model.Usuario;
import com.datamarkets.app.repository.UsuarioCallback;
import com.datamarkets.app.repository.UsuariosRepository;

public class LoginViewModel extends ViewModel {

    private final UsuariosRepository repositorio = new UsuariosRepository();

    private final MutableLiveData<Usuario> usuarioLogueado = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> cargando = new MutableLiveData<>();

    public LiveData<Usuario> getUsuarioLogueado() { return usuarioLogueado; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> getCargando() { return cargando; }

    public void login(String email, String password) {
        // Validaciones básicas
        if (email == null || email.trim().isEmpty()) {
            error.setValue("Introduce tu correo electrónico");
            return;
        }
        if (password == null || password.isEmpty()) {
            error.setValue("Introduce tu contraseña");
            return;
        }

        cargando.setValue(true);

        repositorio.login(email.trim(), password, new UsuarioCallback() {
            @Override
            public void onExito(Usuario usuario) {
                cargando.setValue(false);
                usuarioLogueado.setValue(usuario);
            }

            @Override
            public void onError(String mensaje) {
                cargando.setValue(false);
                error.setValue(mensaje);
            }
        });
    }
}