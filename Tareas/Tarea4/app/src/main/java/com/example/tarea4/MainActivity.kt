package com.example.tarea4

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputLayout
import androidx.core.content.edit


class MainActivity : AppCompatActivity() {

    private lateinit var btnSesion: Button;

    private lateinit var campoUsuario: TextInputLayout
    private lateinit var campoContrasena: TextInputLayout
    private lateinit var campoError: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        campoUsuario = findViewById<TextInputLayout>(R.id.correo);
        campoError = findViewById<TextView>(R.id.errorSesion);
        campoContrasena = findViewById<TextInputLayout>(R.id.contrasena)

        btnSesion = findViewById<Button>(R.id.btnSesion);
        btnSesion.setOnClickListener { btn ->
            val usuario = campoUsuario.editText?.text.toString();
            if(usuario.isEmpty()) {
                campoError.text = getString(R.string.error_sesion_usuario);
                return@setOnClickListener
            }
            val contrasena = campoContrasena.editText?.text.toString()
            if(contrasena == "123456789") {
                val intent = Intent(this, CatalogoActivity::class.java)
                getSharedPreferences("sesion_usuario", MODE_PRIVATE).edit {
                    putString("usuario", usuario)
                }
                startActivity(intent)
            } else {
                campoError.text = getString(R.string.error_sesion_contrasena)
            }
        }
    }
}