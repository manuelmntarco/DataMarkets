package com.datamarkets.app.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.datamarkets.app.R;
import com.datamarkets.app.ui.registro.RegistroActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnEntrar;
    private TextView tvIrRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnEntrar = findViewById(R.id.btnEntrar);
        tvIrRegistro = findViewById(R.id.tvIrRegistro);

        btnEntrar.setOnClickListener(v -> {
            // TODO: validar campos y llamar al ViewModel
        });

        tvIrRegistro.setOnClickListener(v -> {
            startActivity(new Intent(this, RegistroActivity.class));
        });
    }
}