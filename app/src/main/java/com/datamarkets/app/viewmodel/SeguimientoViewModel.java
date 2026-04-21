package com.datamarkets.app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.datamarkets.app.model.Activo;
import com.datamarkets.app.ui.seguimiento.MockData;
import java.util.List;

public class SeguimientoViewModel extends ViewModel {

    // MutableLiveData es el altavoz que puede cambiar su valor
    // LiveData es la versión de solo lectura que ve el Fragment
    private MutableLiveData<List<Activo>> favoritos =
            new MutableLiveData<>();

    // El Fragment observa este LiveData
    public LiveData<List<Activo>> getFavoritos() {
        return favoritos;
    }

    // Carga los favoritos del usuario
    // Por ahora usa MockData, luego llamará al Repository real
    public void cargarFavoritos() {
        favoritos.setValue(MockData.getActivosFavoritos());
    }

    // Elimina un activo de la lista de favoritos
    // Por ahora solo lo quita de la lista local
    // Luego también llamará al backend para eliminarlo de MySQL
    public void eliminarFavorito(Activo activo) {
        List<Activo> listaActual = favoritos.getValue();
        if (listaActual != null) {
            listaActual.remove(activo);
            favoritos.setValue(listaActual);
        }
    }
}