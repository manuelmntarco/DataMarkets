package com.datamarkets.app.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.datamarkets.app.MainActivity;
import com.datamarkets.app.R;
import com.datamarkets.app.repository.GestorSesion;
import com.datamarkets.app.ui.registro.RegistroActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnEntrar;
    private TextView tvIrRegistro;
    private ProgressBar progressBar;
    private LoginViewModel viewModel;
    private GestorSesion gestorSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gestorSesion = new GestorSesion(this);

        // Si ya hay sesión activa, ir directo a MainActivity
        if (gestorSesion.haySesion()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnEntrar = findViewById(R.id.btnEntrar);
        tvIrRegistro = findViewById(R.id.tvIrRegistro);
        progressBar = findViewById(R.id.progressBar);

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        viewModel.getUsuarioLogueado().observe(this, usuario -> {
            gestorSesion.guardarSesion(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getToken()
            );
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        viewModel.getError().observe(this, mensaje -> {
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
        });

        viewModel.getCargando().observe(this, estaCargando -> {
            progressBar.setVisibility(estaCargando ? View.VISIBLE : View.GONE);
            btnEntrar.setEnabled(!estaCargando);
        });

        btnEntrar.setOnClickListener(v -> {
            viewModel.login(
                etEmail.getText().toString(),
                etPassword.getText().toString()
            );
        });

        tvIrRegistro.setOnClickListener(v -> {
            startActivity(new Intent(this, RegistroActivity.class));
        });
    }
}