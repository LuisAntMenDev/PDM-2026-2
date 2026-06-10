package com.example.proyectofinal

import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import androidx.core.content.edit
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.DbHelper.Companion.PRODUCT_TABLE_FIELD_IMG
import com.example.proyectofinal.DbHelper.Companion.PRODUCT_TABLE_FIELD_NAME
import com.example.proyectofinal.DbHelper.Companion.PRODUCT_TABLE_FIELD_PRICE
import com.example.proyectofinal.DbHelper.Companion.PRODUCT_TABLE_NAME
import com.google.android.material.snackbar.Snackbar

class CatalogoActivity : AppCompatActivity() {

    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var topAppBar: MaterialToolbar
    private lateinit var preferences: SharedPreferences
    private lateinit var dbHelper: DbHelper

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
        val userId = preferences.getInt("id", 0)
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
                R.id.drawer_carrito -> {
                    val intent = Intent(this, CarritoActivity::class.java)
                    startActivity(intent)
                }
                R.id.drawer_pedidos -> {
                    val intent = Intent(this, PedidosActivity::class.java)
                    startActivity(intent)
                }
            }
            menuItem.isChecked = true
            drawerLayout.close()
            val mensaje = "Se pulsó la opción: " + menuItem.title;
            Log.d("HomeActivity", mensaje)
            true
        }
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        dbHelper = DbHelper(this)
        val db = dbHelper.writableDatabase
        val args = arrayOf(userId.toString())
        val inCart = db.query(
            DbHelper.CART_TABLE_NAME,
            arrayOf(DbHelper.CART_TABLE_FIELD_PRODUCT_ID),
            "${DbHelper.CART_TABLE_FIELD_USER_ID} = ?",
            args,
            null,
            null,
            null
        )
        val cartProductIdColumn = inCart.getColumnIndexOrThrow(DbHelper.CART_TABLE_FIELD_PRODUCT_ID)
        val activeIds = ArrayList<Int>()
        with(inCart) {
            while (moveToNext()) {
                activeIds.add(getInt(cartProductIdColumn))
            }
        }
        inCart.close()
        val cursor = db.query(
            DbHelper.PRODUCT_TABLE_NAME,   // The table to query
            null,             // The array of columns to return (pass null to get all)
            null,              // The columns for the WHERE clause
            null,          // The values for the WHERE clause
            null,                   // don't group the rows
            null,                   // don't filter by row groups
            null              // The sort order
        )
        val data = ArrayList<Product>()
        val idColumnIndex = cursor.getColumnIndexOrThrow(DbHelper.PRODUCT_TABLE_FIELD_ID)
        val nameColumnIndex = cursor.getColumnIndexOrThrow(DbHelper.PRODUCT_TABLE_FIELD_NAME)
        val priceColumnIndex = cursor.getColumnIndexOrThrow(DbHelper.PRODUCT_TABLE_FIELD_PRICE)
        val imgColumnIndex = cursor.getColumnIndexOrThrow(DbHelper.PRODUCT_TABLE_FIELD_IMG)
        with(cursor) {
            while (moveToNext()) {
                val id = getInt(idColumnIndex)
                val product = Product(id,
                    getString(nameColumnIndex),
                    getFloat(priceColumnIndex),
                    getString(imgColumnIndex),
                    activeIds.contains(id))
                data.add(product)
            }
        }
        cursor.close()
        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.layoutManager = GridLayoutManager(this, 2)
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView.layoutManager = GridLayoutManager(this, 3)
        }
        val adapter = CatalogoAdapter(data.toTypedArray())
        recyclerView.adapter = adapter
        adapter.setOnClickListener(object :
            CatalogoAdapter.OnClickListener {
            override fun onClick(position: Int, model: Product) {
                val values = ContentValues().apply {
                    put(DbHelper.CART_TABLE_FIELD_USER_ID, userId)
                    put(DbHelper.CART_TABLE_FIELD_PRODUCT_ID, model.id)
                }
                db.insert(DbHelper.CART_TABLE_NAME, null, values)
                val contextView = findViewById<RecyclerView>(R.id.recyclerView)
                Snackbar.make(contextView, String.format("%s se añadió correctamente.", model.name), Snackbar.LENGTH_SHORT)
                    .show()
                model.onCart = true
                adapter.notifyItemChanged(position)
            }
        })
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}