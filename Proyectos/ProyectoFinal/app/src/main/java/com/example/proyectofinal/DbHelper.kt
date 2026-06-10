package com.example.proyectofinal

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE $USER_TABLE_NAME (" +
                "$USER_TABLE_FIELD_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$USER_TABLE_FIELD_USER TEXT UNIQUE," +
                "$USER_TABLE_FIELD_PASSWORD TEXT)")
        var values = ContentValues().apply {
            put(USER_TABLE_FIELD_USER, "UsuarioPrueba")
            put(USER_TABLE_FIELD_PASSWORD, "123456789")
        }
        db.insert(USER_TABLE_NAME, null, values)

        db.execSQL("CREATE TABLE $PRODUCT_TABLE_NAME (" +
                "$PRODUCT_TABLE_FIELD_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$PRODUCT_TABLE_FIELD_NAME TEXT," +
                "$PRODUCT_TABLE_FIELD_PRICE REAL," +
                "$PRODUCT_TABLE_FIELD_IMG TEXT)")
        values = ContentValues().apply {
            put(PRODUCT_TABLE_FIELD_NAME, "Playera")
            put(PRODUCT_TABLE_FIELD_PRICE, 500)
            put(PRODUCT_TABLE_FIELD_IMG, "playera")
        }
        db.insert(PRODUCT_TABLE_NAME, null, values)
        values = ContentValues().apply {
            put(PRODUCT_TABLE_FIELD_NAME, "Zapatos")
            put(PRODUCT_TABLE_FIELD_PRICE, 1200)
            put(PRODUCT_TABLE_FIELD_IMG, "tenis")
        }
        db.insert(PRODUCT_TABLE_NAME, null, values)
        values = ContentValues().apply {
            put(PRODUCT_TABLE_FIELD_NAME, "Pantalon")
            put(PRODUCT_TABLE_FIELD_PRICE, 800)
            put(PRODUCT_TABLE_FIELD_IMG, "pantalon")
        }
        db.insert(PRODUCT_TABLE_NAME, null, values)
        values = ContentValues().apply {
            put(PRODUCT_TABLE_FIELD_NAME, "Gorra")
            put(PRODUCT_TABLE_FIELD_PRICE, 300)
            put(PRODUCT_TABLE_FIELD_IMG, "gorra")
        }
        db.insert(PRODUCT_TABLE_NAME, null, values)
        values = ContentValues().apply {
            put(PRODUCT_TABLE_FIELD_NAME, "Lentes")
            put(PRODUCT_TABLE_FIELD_PRICE, 300)
            put(PRODUCT_TABLE_FIELD_IMG, "lentes")
        }
        db.insert(PRODUCT_TABLE_NAME, null, values)
        values = ContentValues().apply {
            put(PRODUCT_TABLE_FIELD_NAME, "Calcetines")
            put(PRODUCT_TABLE_FIELD_PRICE, 150)
            put(PRODUCT_TABLE_FIELD_IMG, "calcetines")
        }
        db.insert(PRODUCT_TABLE_NAME, null, values)

        db.execSQL("CREATE TABLE $CART_TABLE_NAME (" +
                "$CART_TABLE_FIELD_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$CART_TABLE_FIELD_USER_ID INTEGER," +
                "$CART_TABLE_FIELD_PRODUCT_ID INTEGER)")

        db.execSQL("CREATE TABLE $ORDER_TABLE_NAME (" +
                "$ORDER_TABLE_FIELD_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$ORDER_TABLE_FIELD_USER_ID INTEGER)")

        db.execSQL("CREATE TABLE $ORDER_PRODUCT_TABLE_NAME (" +
                "$ORDER_PRODUCT_TABLE_FIELD_ORDER_ID INTEGER," +
                "$ORDER_PRODUCT_TABLE_FIELD_PRODUCT_ID INTEGER)")
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $ORDER_PRODUCT_TABLE_NAME")
        db.execSQL("DROP TABLE IF EXISTS $ORDER_TABLE_NAME")
        db.execSQL("DROP TABLE IF EXISTS $USER_TABLE_NAME")
        db.execSQL("DROP TABLE IF EXISTS $PRODUCT_TABLE_NAME")
        onCreate(db)
    }
    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "ProyectoFinal.db"
        const val USER_TABLE_NAME = "usuarios"
        const val USER_TABLE_FIELD_ID = "id"
        const val USER_TABLE_FIELD_USER = "usuario"
        const val USER_TABLE_FIELD_PASSWORD = "constrasena"
        const val PRODUCT_TABLE_NAME = "productos"
        const val PRODUCT_TABLE_FIELD_ID = "id"
        const val PRODUCT_TABLE_FIELD_NAME = "nombre"
        const val PRODUCT_TABLE_FIELD_PRICE = "precio"
        const val PRODUCT_TABLE_FIELD_IMG = "imagen"
        const val CART_TABLE_NAME = "carritos"
        const val CART_TABLE_FIELD_ID = "id"
        const val CART_TABLE_FIELD_USER_ID = "user_id"
        const val CART_TABLE_FIELD_PRODUCT_ID = "product_id"
        const val ORDER_TABLE_NAME = "pedidos"
        const val ORDER_TABLE_FIELD_ID = "id"
        const val ORDER_TABLE_FIELD_USER_ID = "user_id"
        const val ORDER_PRODUCT_TABLE_NAME = "pedido_producto"
        const val ORDER_PRODUCT_TABLE_FIELD_ORDER_ID = "order_id"
        const val ORDER_PRODUCT_TABLE_FIELD_PRODUCT_ID = "product_id"
    }
}