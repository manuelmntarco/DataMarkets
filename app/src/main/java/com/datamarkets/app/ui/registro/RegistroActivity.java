package com.datamarkets.app.ui.registro;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.datamarkets.app.R;

public class RegistroActivity extends AppCompatActivity {

    private EditText etNombre, etEmail, etPassword, etPassword2;
    private Button btnCrearCuenta;
    private TextView tvIrLogin;
    private ProgressBar progressBar;
    private RegistroViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        etNombre = findViewById(R.id.etNombre);
        etEmail = findViewById(R.id.etEmailRegistro);
        etPassword = findViewById(R.id.etPasswordRegistro);
        etPassword2 = findViewById(R.id.etPassword2);
        btnCrearCuenta = findViewById(R.id.btnCrearCuenta);
        tvIrLogin = findViewById(R.id.tvIrLogin);
        progressBar = findViewById(R.id.progressBarRegistro);

        viewModel = new ViewModelProvider(this).get(RegistroViewModel.class);

        viewModel.getUsuarioRegistrado().observe(this, usuario -> {
            Toast.makeText(this, "Cuenta creada correctamente", Toast.LENGTH_SHORT).show();
            finish(); // Vuelve a LoginActivity
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