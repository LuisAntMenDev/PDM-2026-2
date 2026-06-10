package com.example.proyectofinal

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
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

class PedidosActivity : AppCompatActivity() {

    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var topAppBar: MaterialToolbar
    private lateinit var emptyOrderTextView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var preferences: SharedPreferences
    private lateinit var dbHelper: DbHelper
    private lateinit var orderList: ArrayList<Order>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pedidos)
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
        emptyOrderTextView = findViewById<TextView>(R.id.empty_order_msg)
        recyclerView = findViewById<RecyclerView>(R.id.pedidos_list)
        dbHelper = DbHelper(this)
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT o.${DbHelper.ORDER_TABLE_FIELD_ID}, SUM(p.${DbHelper.PRODUCT_TABLE_FIELD_PRICE}) AS total " +
                "FROM ${DbHelper.ORDER_TABLE_NAME} o INNER JOIN ${DbHelper.ORDER_PRODUCT_TABLE_NAME} op ON o.${DbHelper.ORDER_TABLE_FIELD_ID} = op.${DbHelper.ORDER_PRODUCT_TABLE_FIELD_ORDER_ID} " +
                "INNER JOIN ${DbHelper.PRODUCT_TABLE_NAME} p ON p.${DbHelper.PRODUCT_TABLE_FIELD_ID} = op.${DbHelper.ORDER_PRODUCT_TABLE_FIELD_PRODUCT_ID} " +
                "WHERE o.${DbHelper.ORDER_TABLE_FIELD_USER_ID} = ? " +
                "GROUP BY o.${DbHelper.ORDER_TABLE_FIELD_ID}", arrayOf(userId.toString()))
        if(cursor.count == 0) {
            emptyOrderTextView.visibility = View.VISIBLE
        }
        orderList = ArrayList<Order>()
        val idColumnIndex = cursor.getColumnIndexOrThrow(DbHelper.ORDER_TABLE_FIELD_ID)
        val totalColumnIndex = cursor.getColumnIndexOrThrow("total")
        with(cursor) {
            while (moveToNext()) {
                val id = getInt(idColumnIndex)
                val total = getFloat(totalColumnIndex)
                val order = Order(id, total)
                orderList.add(order)
            }
        }
        cursor.close()
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = PedidoAdapter(orderList)
        recyclerView.adapter = adapter
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
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}