package com.darra.app

import android.app.AlertDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var db: DarraDatabase
    private lateinit var code: EditText
    private lateinit var status: TextView
    private lateinit var profile: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = DarraDatabase(this)
        code = findViewById(R.id.code)
        status = findViewById(R.id.status)
        profile = findViewById(R.id.profile)

        refreshProfile()

        findViewById<Button>(R.id.loginButton).setOnClickListener { loginDialog() }
        findViewById<Button>(R.id.createButton).setOnClickListener {
            code.setText("# main.py\nprint('Hello from my Darra app')\n")
            status.text = "New app created"
        }
        findViewById<Button>(R.id.saveButton).setOnClickListener {
            db.saveProject("MyApp", code.text.toString())
            status.text = "Saved locally"
        }
        findViewById<Button>(R.id.runButton).setOnClickListener {
            val result = DarraRuntime.validate(code.text.toString())
            status.text = if (result.ok) {
                "✓ Valid — runtime ready"
            } else {
                "✕ ${result.error}"
            }
        }

        val saved = db.loadProject("MyApp")
        if (saved != null) code.setText(saved)
    }

    private fun loginDialog() {
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 10, 40, 0)
        }
        val user = EditText(this).apply { hint = "New username" }
        val pass = EditText(this).apply {
            hint = "New login password"
            inputType = 0x81
        }
        box.addView(user)
        box.addView(pass)

        AlertDialog.Builder(this)
            .setTitle("Create Login")
            .setView(box)
            .setPositiveButton("Create") { _, _ ->
                val u = user.text.toString().trim()
                val p = pass.text.toString()
                if (u.isEmpty() || p.length < 4) {
                    status.text = "Username required; password must be 4+ characters"
                } else {
                    db.createUser(u, p)
                    refreshProfile()
                    status.text = "Login created"
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun refreshProfile() {
        val u = db.username()
        profile.text = if (u == null) "Profile: No login" else "Profile: $u"
    }
}
