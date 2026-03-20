package com.example.tarea2

import android.content.res.Configuration
import android.os.Bundle
import android.widget.GridLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Vista 1: Login
        //setContentView(R.layout.login)
        // Vista 2: Productos
        setContentView(R.layout.productos)
        val orientation = resources.configuration.orientation
        val grid = findViewById<GridLayout>(R.id.gridProductos)
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            grid.columnCount = 2
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            grid.columnCount = 3
        }
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/
    }
}