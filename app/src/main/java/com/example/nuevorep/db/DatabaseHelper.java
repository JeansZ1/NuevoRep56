package com.example.nuevorep.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String NOMBRE_BASE_DE_DATOS = "UserDB";
    private static final int VERSION_BASE_DE_DATOS = 1;

    // Nombre de la tabla y columnas
    private static final String TABLA_USUARIOS = "usuarios";
    private static final String COLUMNA_ID = "id";
    private static final String COLUMNA_NOMBRE_USUARIO = "nombre_usuario";
    private static final String COLUMNA_CONTRASEÑA = "contraseña";

    // Comando SQL para crear la tabla
    private static final String CREAR_TABLA_USUARIOS =
            "CREATE TABLE " + TABLA_USUARIOS + "("
                    + COLUMNA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMNA_NOMBRE_USUARIO + " TEXT,"
                    + COLUMNA_CONTRASEÑA + " TEXT"
                    + ")";



    public DatabaseHelper(Context context) {
        super(context, NOMBRE_BASE_DE_DATOS, null, VERSION_BASE_DE_DATOS);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREAR_TABLA_USUARIOS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_USUARIOS);
        onCreate(db);
    }
    public boolean verificarUsuario(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase(); // Acceso a la base de datos actual
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLA_USUARIOS +
                " WHERE " + COLUMNA_NOMBRE_USUARIO + " = ? AND " +
                COLUMNA_CONTRASEÑA + " = ?", new String[]{username, password});

        boolean usuarioExiste = cursor.getCount() > 0;

        cursor.close();
        db.close(); // Importante cerrar la base de datos
        return usuarioExiste;
    }

    // Agregar un método para insertar datos de usuario en la base de datos
    public long agregarUsuario(String nombreUsuario, String contraseña) {
        SQLiteDatabase db = this.getWritableDatabase();
        // ContentValues para almacenar valores para la inserción
        ContentValues valores = new ContentValues();
        valores.put(COLUMNA_NOMBRE_USUARIO, nombreUsuario);
        valores.put(COLUMNA_CONTRASEÑA, contraseña);

        // Insertar fila
        long resultado = db.insert(TABLA_USUARIOS, null, valores);
        db.close();
        return resultado;
    }
}