package com.example.tarea4

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import androidx.core.content.edit

class CatalogoActivity : AppCompatActivity() {

    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var topAppBar: MaterialToolbar

    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.catalogo)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        preferences = getSharedPreferences("sesion_usuario", MODE_PRIVATE)
        val usuario = preferences.getString("usuario", "Sin usuario")
        val orientation = resources.configuration.orientation
        val grid = findViewById<GridLayout>(R.id.gridProductos)
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            grid.columnCount = 2
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            grid.columnCount = 3
        }
        navigationView = findViewById(R.id.navigationView)
        drawerLayout = findViewById(R.id.drawerLayout)
        topAppBar = findViewById(R.id.topAppBar)
        topAppBar.setNavigationOnClickListener {
            drawerLayout.open()
        }
        navigationView.getHeaderView(0).findViewById<TextView>(R.id.header_usuario).text = usuario
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
        findViewById<Button>(R.id.catalogo_btn1).setOnClickListener { btn ->
            goToCart(R.drawable.playera, getString(R.string.card_title_1), getString(R.string.card_price_1))
        }
        findViewById<Button>(R.id.catalogo_btn2).setOnClickListener { btn ->
            goToCart(R.drawable.tenis, getString(R.string.card_title_2), getString(R.string.card_price_2))
        }
        findViewById<Button>(R.id.catalogo_btn3).setOnClickListener { btn ->
            goToCart(R.drawable.pantalon, getString(R.string.card_title_3), getString(R.string.card_price_3))
        }
        findViewById<Button>(R.id.catalogo_btn4).setOnClickListener { btn ->
            goToCart(R.drawable.gorra, getString(R.string.card_title_4), getString(R.string.card_price_4))
        }
        findViewById<Button>(R.id.catalogo_btn5).setOnClickListener { btn ->
            goToCart(R.drawable.lentes, getString(R.string.card_title_5), getString(R.string.card_price_5))
        }
        findViewById<Button>(R.id.catalogo_btn6).setOnClickListener { btn ->
            goToCart(R.drawable.calcetines, getString(R.string.card_title_6), getString(R.string.card_price_6))
        }
    }

    private fun goToCart(img: Int, title: String, price: String) {
        val intent = Intent(this, CarritoActivity::class.java)
        intent.putExtra("img", img)
        intent.putExtra("title", title)
        intent.putExtra("price", price)
        startActivity(intent)
    }
}