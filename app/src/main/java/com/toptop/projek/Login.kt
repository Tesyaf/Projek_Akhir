package com.toptop.projek

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.toptop.projek.databinding.ActivityLoginBinding // Ganti nama jika file XML Anda bukan activity_login.xml

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Setup View Binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Atur OnClickListener untuk tombol login
        binding.button.setOnClickListener {
            // Ambil input dari EditText
            val username = binding.usernameLogin.text.toString()
            val password = binding.passwordLogin.text.toString() // Menggunakan ID yang sudah diperbaiki

            // Validasi input tidak boleh kosong
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Username dan Password harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Panggil fungsi untuk proses login
            loginUser(username, password)
        }

        // Atur OnClickListener untuk teks "Daftar?"
        binding.directDaftar.setOnClickListener {
            // Arahkan ke Activity Register (buat jika belum ada)
            // val intent = Intent(this, RegisterActivity::class.java)
            // startActivity(intent)
        }
    }

    private fun loginUser(username: String, passwordDariInput: String) {
        // Arahkan referensi database ke node 'users'
        database = FirebaseDatabase.getInstance().getReference("users")

        // Cari data berdasarkan username yang diinput
        database.child(username).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Cek apakah username ada di database
                if (snapshot.exists()) {
                    // Jika username ada, ambil password dari database
                    val passwordDariDb = snapshot.child("password").getValue(String::class.java)

                    // Bandingkan password dari input dengan password di database
                    if (passwordDariDb == passwordDariInput) {
                        // Jika password cocok
                        Toast.makeText(this@Login, "Login berhasil!", Toast.LENGTH_SHORT).show()

                        // Arahkan ke MainActivity atau halaman utama
                        val intent = Intent(this@Login, MainActivity::class.java)
                        startActivity(intent)
                        finish() // Tutup activity login agar tidak bisa kembali
                    } else {
                        // Jika password tidak cocok
                        Toast.makeText(this@Login, "Password salah", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Jika username tidak ditemukan
                    Toast.makeText(this@Login, "Username tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Jika terjadi error saat mengakses database
                Toast.makeText(this@Login, "Gagal terhubung ke database", Toast.LENGTH_SHORT).show()
            }
        })
    }
}