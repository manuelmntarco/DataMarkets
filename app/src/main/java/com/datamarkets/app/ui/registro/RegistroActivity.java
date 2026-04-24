package com.datamarkets.app.ui.registro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.datamarkets.app.R;
import com.datamarkets.app.ui.login.LoginActivity;

public class RegistroActivity extends AppCompatActivity {

    private EditText etNombre, etEmail, etPassword, etPassword2;
    private Button btnCrearCuenta;
    private TextView tvIrLogin;

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

        btnCrearCuenta.setOnClickListener(v -> {
            // TODO: validar campos y llamar al ViewModel
        });

        tvIrLogin.setOnClickListener(v -> {
            finish(); // Vuelve a LoginActivity
        });
    }
}