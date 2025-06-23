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
import com.toptop.projek.databinding.ActivityLoginBinding

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi referensi database ke node 'users'
        database = FirebaseDatabase.getInstance().getReference("users")

        binding.button.setOnClickListener {
            val displayName = binding.usernameLogin.text.toString().trim()
            val password = binding.passwordLogin.text.toString()

            if (displayName.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Username dan Password harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginUser(displayName, password)
        }

        binding.directDaftar.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(displayName: String, passwordDariInput: String) {
        // Cari data berdasarkan displayName menggunakan query
        database.orderByChild("displayName").equalTo(displayName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        var userFound = false
                        for (userSnapshot in snapshot.children) {
                            val user = userSnapshot.getValue(User::class.java)

                            // Bandingkan password
                            if (user?.password == passwordDariInput) {
                                userFound = true
                                Toast.makeText(this@Login, "Login berhasil! Halo, ${user.displayName}", Toast.LENGTH_SHORT).show()

                                val intent = Intent(this@Login, MainActivity::class.java)

                                // --- INI BAGIAN YANG HILANG DAN SANGAT PENTING ---
                                // Kirim ID unik milik user ke MainActivity agar MainActivity tahu siapa yang login.
                                // Pastikan class User Anda memiliki properti 'id'.
                                intent.putExtra("USER_ID", user.id)

                                startActivity(intent)
                                finish()
                                break // Keluar dari loop jika user sudah ditemukan dan password cocok
                            }
                        }

                        if (!userFound) {
                            Toast.makeText(this@Login, "Password salah", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@Login, "Username tidak ditemukan", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@Login, "Gagal terhubung ke database", Toast.LENGTH_SHORT).show()
                }
            })
    }
}