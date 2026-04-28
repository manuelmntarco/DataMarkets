package com.datamarkets.app.ui.registro;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.datamarkets.app.model.Usuario;
import com.datamarkets.app.repository.UsuarioCallback;
import com.datamarkets.app.repository.UsuariosRepository;

public class RegistroViewModel extends ViewModel {

    private final UsuariosRepository repositorio = new UsuariosRepository();

    private final MutableLiveData<Usuario> usuarioRegistrado = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> cargando = new MutableLiveData<>();

    public LiveData<Usuario> getUsuarioRegistrado() { return usuarioRegistrado; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> getCargando() { return cargando; }

    public void registrar(String nombre, String email, String password, String password2) {
        if (nombre == null || nombre.trim().isEmpty()) {
            error.setValue("Introduce tu nombre");
            return;
        }
        if (email == null || email.trim().isEmpty()) {
            error.setValue("Introduce tu correo electrónico");
            return;
        }
        if (password == null || password.length() < 8) {
            error.setValue("La contraseña debe tener al menos 8 caracteres");
            return;
        }
        if (!password.equals(password2)) {
            error.setValue("Las contraseñas no coinciden");
            return;
        }

        cargando.setValue(true);

        repositorio.registrar(nombre.trim(), email.trim(), password, new UsuarioCallback() {
            @Override
            public void onExito(Usuario usuario) {
                cargando.setValue(false);
                usuarioRegistrado.setValue(usuario);
            }

            @Override
            public void onError(String mensaje) {
                cargando.setValue(false);
                error.setValue(mensaje);
            }
        });
    }
}