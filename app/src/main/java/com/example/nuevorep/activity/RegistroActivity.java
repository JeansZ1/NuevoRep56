package com.example.nuevorep.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nuevorep.R;
import com.example.nuevorep.db.DatabaseHelper;

public class RegistroActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);


        databaseHelper = new DatabaseHelper(this);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button botonCrearCuenta = findViewById(R.id.botonCrearCuenta);
        botonCrearCuenta.setOnClickListener(v -> crearNuevoUsuario());


    }

    private void crearNuevoUsuario() {
        EditText editTextNuevoNombreUsuario = findViewById(R.id.editTextNombreUsuario);
        EditText editTextNuevaContraseña = findViewById(R.id.editTextContraseña);

        String nuevoNombreUsuario = editTextNuevoNombreUsuario.getText().toString().trim();
        String nuevaContraseña = editTextNuevaContraseña.getText().toString().trim();

        if (!nuevoNombreUsuario.isEmpty() && !nuevaContraseña.isEmpty()) {
            long resultado = databaseHelper.agregarUsuario(nuevoNombreUsuario, nuevaContraseña);
            if (resultado > 0) {
                Toast.makeText(RegistroActivity.this, "Usuario creado exitosamente", Toast.LENGTH_SHORT).show();
                // Redirige a la actividad de inicio de sesión o a otra actividad según tu flujo de la aplicación
                Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(RegistroActivity.this, "Error al crear usuario", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d("RegistroActivity", "Completa todos los campos");
            Toast.makeText(RegistroActivity.this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
        }
    }
}