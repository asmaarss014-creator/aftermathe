package com.darra.app

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.security.MessageDigest

class DarraDatabase(context: Context) :
    SQLiteOpenHelper(context, "darra.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY, username TEXT UNIQUE NOT NULL, password_hash TEXT NOT NULL)")
        db.execSQL("CREATE TABLE projects (name TEXT PRIMARY KEY, source TEXT NOT NULL)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    fun createUser(username: String, password: String) {
        val values = ContentValues().apply {
            put("username", username)
            put("password_hash", sha256(password))
        }
        writableDatabase.insertWithOnConflict(
            "users", null, values, SQLiteDatabase.CONFLICT_REPLACE
        )
    }

    fun username(): String? =
        readableDatabase.rawQuery("SELECT username FROM users LIMIT 1", null).use {
            if (it.moveToFirst()) it.getString(0) else null
        }

    fun saveProject(name: String, source: String) {
        val v = ContentValues().apply {
            put("name", name)
            put("source", source)
        }
        writableDatabase.insertWithOnConflict(
            "projects", null, v, SQLiteDatabase.CONFLICT_REPLACE
        )
    }

    fun loadProject(name: String): String? =
        readableDatabase.rawQuery(
            "SELECT source FROM projects WHERE name=?", arrayOf(name)
        ).use {
            if (it.moveToFirst()) it.getString(0) else null
        }

    private fun sha256(value: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(value.toByteArray())
            .joinToString("") { "%02x".format(it) }
}

