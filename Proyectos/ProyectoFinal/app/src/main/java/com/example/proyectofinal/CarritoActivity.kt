package com.example.proyectofinal

import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

class CarritoActivity : AppCompatActivity() {

    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var topAppBar: MaterialToolbar
    private lateinit var emptyCartTextView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnConfirmar: Button
    private lateinit var totalTextView: TextView
    private lateinit var preferences: SharedPreferences
    private lateinit var dbHelper: DbHelper
    private lateinit var cartList: ArrayList<Product>

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
        val userId = preferences.getInt("id", 0)
        navigationView = findViewById(R.id.navigationView)
        navigationView.getHeaderView(0).findViewById<TextView>(R.id.header_usuario).text = usuario
        drawerLayout = findViewById(R.id.drawerLayout)
        topAppBar = findViewById(R.id.topAppBar)
        topAppBar.setNavigationOnClickListener {
            drawerLayout.open()
        }
        emptyCartTextView = findViewById<TextView>(R.id.empty_cart_msg)
        recyclerView = findViewById<RecyclerView>(R.id.carrito_list)
        btnConfirmar = findViewById<Button>(R.id.btn_confirmar)
        totalTextView = findViewById<TextView>(R.id.cart_total)
        dbHelper = DbHelper(this)
        val db = dbHelper.writableDatabase
        val cursor = db.rawQuery("SELECT p.${DbHelper.PRODUCT_TABLE_FIELD_ID}, p.${DbHelper.PRODUCT_TABLE_FIELD_NAME}, p.${DbHelper.PRODUCT_TABLE_FIELD_PRICE}, p.${DbHelper.PRODUCT_TABLE_FIELD_IMG} " +
                "FROM ${DbHelper.CART_TABLE_NAME} c INNER JOIN ${DbHelper.PRODUCT_TABLE_NAME} p ON c.${DbHelper.CART_TABLE_FIELD_PRODUCT_ID} = p.${DbHelper.PRODUCT_TABLE_FIELD_ID} " +
                "WHERE c.${DbHelper.CART_TABLE_FIELD_USER_ID} = ?", arrayOf(userId.toString()))
        if(cursor.count == 0) {
            emptyCartTextView.visibility = View.VISIBLE
        } else {
            btnConfirmar.visibility = View.VISIBLE
        }
        cartList = ArrayList<Product>()
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
                    true)
                cartList.add(product)
            }
        }
        cursor.close()
        updatePrice()
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = CarritoAdapter(cartList)
        recyclerView.adapter = adapter
        adapter.setOnClickListener(object :
            CarritoAdapter.OnClickListener {
            override fun onClick(position: Int, model: Product) {
                val selection = "${DbHelper.CART_TABLE_FIELD_PRODUCT_ID} = ? AND ${DbHelper.CART_TABLE_FIELD_USER_ID} = ?"
                val selectionArgs = arrayOf(model.id.toString(), userId.toString())
                val deletedRows = db.delete(DbHelper.CART_TABLE_NAME, selection, selectionArgs)
                val contextView = findViewById<RecyclerView>(R.id.carrito_list)
                if(deletedRows == 0) {
                    Snackbar.make(contextView, "Error al eliminar el producto", Snackbar.LENGTH_SHORT).show()
                    return
                }
                Snackbar.make(contextView, String.format("%s se eliminó correctamente.", model.name), Snackbar.LENGTH_SHORT)
                    .show()
                cartList.removeAt(position)
                adapter.notifyItemRemoved(position)
                if(cartList.isEmpty()) {
                    emptyCartTextView.visibility = View.VISIBLE
                    btnConfirmar.visibility = View.GONE
                }
                updatePrice()
            }
        })
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

        val btnConfirmar = findViewById<Button>(R.id.btn_confirmar)
        btnConfirmar.setOnClickListener { btnConfirmar ->
            val orderValues = ContentValues()
            orderValues.put(DbHelper.ORDER_TABLE_FIELD_USER_ID, userId)
            val id = db.insert(DbHelper.ORDER_TABLE_NAME, null, orderValues)
            if(id == (-1).toLong()) {
                return@setOnClickListener
            }
            cartList.forEach { product ->
                val values = ContentValues().apply {
                    put(DbHelper.ORDER_PRODUCT_TABLE_FIELD_ORDER_ID, id)
                    put(DbHelper.ORDER_PRODUCT_TABLE_FIELD_PRODUCT_ID, product.id)
                }
                db.insert(DbHelper.ORDER_PRODUCT_TABLE_NAME, null, values)
            }
            db.delete(DbHelper.CART_TABLE_NAME, "${DbHelper.CART_TABLE_FIELD_USER_ID} = ?", arrayOf(userId.toString()))
            val intent = Intent(this, PedidosActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updatePrice() {
        if(cartList.isEmpty()) {
            totalTextView.text = ""
            return
        }
        val total = cartList.fold(0.0, {acc, elem -> acc + elem.price})
        totalTextView.text = String.format("Total: $%.2f", total)
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}