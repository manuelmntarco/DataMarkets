package com.datamarkets.app.ui.mercados;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.datamarkets.app.model.Activo;

import java.util.List;

public class MercadosViewModel extends ViewModel {

    private final MercadosRepository repository;

    // Guardamos el resultado aquí para no repetir la llamada a la API
    private LiveData<List<Activo>> activosLiveData;

    public MercadosViewModel() {
        repository = new MercadosRepository();
    }

    public LiveData<List<Activo>> obtenerActivos() {
        // Solo llamamos a la API si no tenemos datos todavía
        if (activosLiveData == null) {
            activosLiveData = repository.obtenerActivos();
        }
        return activosLiveData;
    }
}