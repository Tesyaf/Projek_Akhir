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
import com.toptop.projek.databinding.ActivityRegisterBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

class Register : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi referensi database ke node 'users'
        database = FirebaseDatabase.getInstance().getReference("users")

        binding.register.setOnClickListener {
            val displayName = binding.usernameRegister.text.toString().trim()
            val email = binding.emailRegister.text.toString().trim()
            val password = binding.passwordRegister.text.toString()

            if (displayName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Panggil fungsi untuk mendaftarkan user
            registerUser(displayName, email, password)
        }

        binding.textView5.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }

    private fun registerUser(displayName: String, email: String, password: String) {
        // 1. Cek dulu apakah displayName sudah ada menggunakan query
        database.orderByChild("displayName").equalTo(displayName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Jika snapshot ada isinya, berarti displayName sudah digunakan
                        Toast.makeText(this@Register, "Username (Display Name) sudah digunakan!", Toast.LENGTH_SHORT).show()
                    } else {
                        // 2. Jika displayName belum ada, buat user baru
                        val userId = database.push().key // Buat ID unik baru untuk user

                        if (userId == null) {
                            Toast.makeText(this@Register, "Gagal membuat user ID", Toast.LENGTH_SHORT).show()
                            return
                        }

                        // Buat timestamp dalam format ISO 8601
                        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                        sdf.timeZone = TimeZone.getTimeZone("UTC")
                        val createdAt = sdf.format(Date())

                        // Buat objek User
                        val user = User(userId, displayName, email, password, "", "", createdAt)

                        // Simpan user baru di bawah ID yang unik
                        database.child(userId).setValue(user)
                            .addOnSuccessListener {
                                Toast.makeText(this@Register, "Registrasi berhasil!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@Register, Login::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener {
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