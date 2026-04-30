package com.datamarkets.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.datamarkets.app.model.Activo;
import com.datamarkets.app.repository.SeguimientoRepository;

import java.util.List;

public class SeguimientoViewModel extends AndroidViewModel {

    private SeguimientoRepository repository;
    private MutableLiveData<List<Activo>> favoritos = new MutableLiveData<>();
    private MutableLiveData<String> mensaje = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();

    // Constructor: recibe Application para poder pasar el contexto
    // al Repository (necesario para leer SharedPreferences)
    public SeguimientoViewModel(@NonNull Application application) {
        super(application);
        repository = new SeguimientoRepository(application.getApplicationContext());
    }

    // El Fragment observa estos tres LiveData
    public LiveData<List<Activo>> getFavoritos() { return favoritos; }
    public LiveData<String> getMensaje()         { return mensaje; }
    public LiveData<String> getError()           { return error; }

    // Pide los favoritos al backend
    public void cargarFavoritos() {
        repository.obtenerFavoritos(new SeguimientoRepository.OnFavoritosListener() {
            @Override
            public void onExito(List<Activo> lista) {
                favoritos.setValue(lista);
            }

            @Override
            public void onError(String msg) {
                error.setValue(msg);
            }
        });
    }

    // Añade un activo a favoritos y recarga la lista
    public void anyadirFavorito(String idExterno) {
        repository.anyadirFavorito(idExterno,
                new SeguimientoRepository.OnOperacionListener() {
                    @Override
                    public void onExito(String msg) {
                        mensaje.setValue(msg);
                        cargarFavoritos(); // Recarga la lista tras añadir
                    }

                    @Override
                    public void onError(String msg) {
                        error.setValue(msg);
                    }
                });
    }

    // Elimina un activo de favoritos y recarga la lista
    public void eliminarFavorito(String idExterno) {
        repository.eliminarFavorito(idExterno,
                new SeguimientoRepository.OnOperacionListener() {
                    @Override
                    public void onExito(String msg) {
                        mensaje.setValue(msg);
                        cargarFavoritos(); // Recarga la lista tras eliminar
                    }

                    @Override
                    public void onError(String msg) {
                        error.setValue(msg);
                    }
                });
    }
}