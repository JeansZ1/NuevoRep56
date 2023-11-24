package com.example.nuevorep.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nuevorep.R;
import com.example.nuevorep.db.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d("LoginActivity", "onCreate() called");

        databaseHelper = new DatabaseHelper(this);
        if (isLoggedIn()) {
            startActivity(new Intent(LoginActivity.this, Carpeta.class));
            finish();
            return;
        }
        Button botonCrearUsuario = findViewById(R.id.botonCrearUsuario);
        botonCrearUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
                startActivity(intent);
            }
        });



        usernameEditText = findViewById(R.id.editTextNombreUsuario);
        passwordEditText = findViewById(R.id.editTextContraseña);
        loginButton = findViewById(R.id.botonIniciarSesion);

        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            boolean usuarioValido = databaseHelper.verificarUsuario(username, password);
            Log.d("LoginActivity", "Username: " + username);
            Log.d("LoginActivity", "Password: " + password);

            if (usuarioValido) {
                Toast.makeText(LoginActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, Carpeta.class));
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "Usuario o Contraseña no existentes", Toast.LENGTH_SHORT).show();
            }
        });


    }
    private boolean isLoggedIn() {
        SharedPreferences preferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        return preferences.getBoolean("isLoggedIn", false);
    }
}
