package com.datamarkets.app.ui.registro;

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

public class RegistroActivity extends AppCompatActivity {

    private EditText etNombre, etEmail, etPassword, etPassword2;
    private Button btnCrearCuenta;
    private TextView tvIrLogin;
    private ProgressBar progressBar;
    private RegistroViewModel viewModel;
    private GestorSesion gestorSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        gestorSesion = new GestorSesion(this);

        etNombre = findViewById(R.id.etNombre);
        etEmail = findViewById(R.id.etEmailRegistro);
        etPassword = findViewById(R.id.etPasswordRegistro);
        etPassword2 = findViewById(R.id.etPassword2);
        btnCrearCuenta = findViewById(R.id.btnCrearCuenta);
        tvIrLogin = findViewById(R.id.tvIrLogin);
        progressBar = findViewById(R.id.progressBarRegistro);

        viewModel = new ViewModelProvider(this).get(RegistroViewModel.class);

        viewModel.getUsuarioRegistrado().observe(this, usuario -> {
            gestorSesion.guardarSesion(
                    usuario.getId(),
                    usuario.getNombre(),
                    usuario.getEmail(),
                    usuario.getToken()
            );
            Toast.makeText(this, "Cuenta creada correctamente", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        viewModel.getError().observe(this, mensaje -> {
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
        });

        viewModel.getCargando().observe(this, estaCargando -> {
            progressBar.setVisibility(estaCargando ? View.VISIBLE : View.GONE);
            btnCrearCuenta.setEnabled(!estaCargando);
        });

        btnCrearCuenta.setOnClickListener(v -> {
            viewModel.registrar(
                    etNombre.getText().toString(),
                    etEmail.getText().toString(),
                    etPassword.getText().toString(),
                    etPassword2.getText().toString()
            );
        });

        tvIrLogin.setOnClickListener(v -> {
            finish();
        });
    }
}