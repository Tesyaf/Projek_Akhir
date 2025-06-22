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
import com.toptop.projek.databinding.ActivityRegisterBinding // Pastikan nama file XML Anda adalah activity_register.xml

class Register : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Atur listener untuk tombol register
        binding.button.setOnClickListener {
            // Ambil input dari semua field
            val username = binding.usernameRegister.text.toString() // ID yang sudah diperbaiki
            val email = binding.emailRegister.text.toString()
            val password = binding.passwordRegister.text.toString()

            // Validasi input tidak boleh kosong
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Buat objek User dari data class yang kita buat
            val user = User(username, email, password)

            // Panggil fungsi untuk mendaftarkan user
            registerUser(user)
        }

        // Atur listener untuk teks "Login?"
        binding.textView5.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }

    private fun registerUser(user: User) {
        // Arahkan referensi database ke node 'users'
        database = FirebaseDatabase.getInstance().getReference("users")

        // 1. Cek dulu apakah username sudah ada
        database.child(user.username!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Jika username sudah ada, tampilkan pesan error
                    Toast.makeText(this@Register, "Username sudah digunakan!", Toast.LENGTH_SHORT).show()
                } else {
                    // 2. Jika username belum ada, simpan data user baru
                    database.child(user.username).setValue(user)
                        .addOnSuccessListener {
                            // Jika berhasil disimpan
                            Toast.makeText(this@Register, "Registrasi berhasil!", Toast.LENGTH_SHORT).show()

                            // Kosongkan field input
                            binding.usernameRegister.text.clear()
                            binding.emailRegister.text.clear()
                            binding.passwordRegister.text.clear()

                            // Arahkan ke halaman Login
                            val intent = Intent(this@Register, Login::class.java)
                            startActivity(intent)
                            finish() // Tutup activity register
                        }
                        .addOnFailureListener {
                            // Jika gagal disimpan
                            Toast.makeText(this@Register, "Registrasi gagal, coba lagi", Toast.LENGTH_SHORT).show()
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Register, "Gagal terhubung ke database", Toast.LENGTH_SHORT).show()
            }
        })
    }
}