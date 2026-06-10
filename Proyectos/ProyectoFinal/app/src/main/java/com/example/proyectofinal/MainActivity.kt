package com.example.proyectofinal

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputLayout
import androidx.core.content.edit


class MainActivity : AppCompatActivity() {

    private lateinit var btnSesion: Button;
    private lateinit var campoUsuario: TextInputLayout
    private lateinit var campoContrasena: TextInputLayout
    private lateinit var campoError: TextView
    private lateinit var dbHelper: DbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DbHelper(this)

        campoUsuario = findViewById<TextInputLayout>(R.id.correo);
        campoError = findViewById<TextView>(R.id.errorSesion);
        campoContrasena = findViewById<TextInputLayout>(R.id.contrasena)

        btnSesion = findViewById<Button>(R.id.btnSesion);
        btnSesion.setOnClickListener { btn ->
            val db = dbHelper.readableDatabase
            val usuario = campoUsuario.editText?.text.toString();
            if(usuario.isEmpty()) {
                campoError.text = getString(R.string.error_sesion_usuario);
                return@setOnClickListener
            }
            val contrasena = campoContrasena.editText?.text.toString()

            val where = "${DbHelper.USER_TABLE_FIELD_USER} = ?"
            val whereArgs = arrayOf(usuario)
            val cursor = db.query(
                DbHelper.USER_TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                where,              // The columns for the WHERE clause
                whereArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null              // The sort order
            )
            if(cursor.count == 0) {
                campoError.text = getString(R.string.error_sesion_contrasena)
                return@setOnClickListener
            }
            cursor.moveToNext()
            val contra = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.USER_TABLE_FIELD_PASSWORD))
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.USER_TABLE_FIELD_ID))
            cursor.close()
            if(contrasena == contra) {
                val intent = Intent(this, CatalogoActivity::class.java)
                getSharedPreferences("sesion_usuario", MODE_PRIVATE).edit {
                    putString("usuario", usuario)
                    putInt("id", id)
                }
                startActivity(intent)
            } else {
                campoError.text = getString(R.string.error_sesion_contrasena)
            }
        }
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}