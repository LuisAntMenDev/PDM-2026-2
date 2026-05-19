package com.example.tarea4

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import androidx.core.content.edit

class CarritoActivity : AppCompatActivity() {

    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var topAppBar: MaterialToolbar

    private lateinit var preferences: SharedPreferences

    private lateinit var img: ImageView
    private lateinit var titulo: TextView
    private lateinit var precio: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.carrito)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        preferences = getSharedPreferences("sesion_usuario", MODE_PRIVATE)
        val usuario = preferences.getString("usuario", "Sin usuario")
        navigationView = findViewById(R.id.navigationView)
        navigationView.getHeaderView(0).findViewById<TextView>(R.id.header_usuario).text = usuario
        drawerLayout = findViewById(R.id.drawerLayout)
        topAppBar = findViewById(R.id.topAppBar)
        topAppBar.setNavigationOnClickListener {
            drawerLayout.open()
        }
        img = findViewById<ImageView>(R.id.carrito_foto)
        titulo = findViewById<TextView>(R.id.carrito_titulo)
        precio = findViewById<TextView>(R.id.carrito_precio)
        img.setImageResource(intent.getIntExtra("img", R.drawable.apparel_24px))
        titulo.text = intent.getStringExtra("title")
        precio.text = intent.getStringExtra("price")
        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_ajustes -> {
                    Log.d("HomeActivity", "Se pulsó acción de ajustes")
                    true
                }

                R.id.action_info -> {
                    Log.d("HomeActivity", "Se pulsó acción de más información")
                    true
                }

                R.id.action_sesion -> {
                    Log.d("HomeActivity", "Se pulsó acción de sesión")
                    preferences.edit { clear() }
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.drawer_catalogo -> {
                    val intent = Intent(this, CatalogoActivity::class.java)
                    startActivity(intent)
                }
            }
            menuItem.isChecked = true
            drawerLayout.close()
            val mensaje = "Se pulsó la opción: " + menuItem.title;
            Log.d("HomeActivity", mensaje)
            true

        }
    }
}