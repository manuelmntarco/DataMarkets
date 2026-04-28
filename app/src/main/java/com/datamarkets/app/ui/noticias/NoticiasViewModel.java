package com.datamarkets.app.ui.noticias;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.datamarkets.app.model.Noticia;
import com.datamarkets.app.repository.NoticiasRepository;
import java.util.List;

public class NoticiasViewModel extends ViewModel {

    private final NoticiasRepository repository;

    public NoticiasViewModel() {
        repository = new NoticiasRepository();
    }

    public LiveData<List<Noticia>> getNoticias() {
        return repository.getNoticias();
    }

    public LiveData<Boolean> getCargando() {
        return repository.getCargando();
    }

    public LiveData<String> getError() {
        return repository.getError();
    }

    public void cargarNoticias() {
        repository.cargarNoticias();
    }
}
