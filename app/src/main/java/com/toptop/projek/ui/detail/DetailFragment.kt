package com.toptop.projek.ui.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.toptop.projek.Laptop
import com.toptop.projek.R
import com.toptop.projek.User
import com.toptop.projek.databinding.FragmentDetailBinding
import com.toptop.projek.databinding.ItemSpecDetailBinding
import java.text.NumberFormat
import java.util.*
import androidx.navigation.fragment.findNavController
import androidx.fragment.app.activityViewModels // Pastikan import ini ada
import androidx.navigation.fragment.navArgs // Import ini untuk perbaikan kedua
import com.toptop.projek.ui.SharedViewModel

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: FirebaseDatabase
    private val TAG = "DetailFragment"

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = FirebaseDatabase.getInstance()

        val laptopId = arguments?.getString("LAPTOP_ID")
        val userId = sharedViewModel.userId.value

        if (laptopId != null) {
            fetchLaptopDetails(laptopId)
        } else {
            Toast.makeText(context, "Laptop tidak ditemukan", Toast.LENGTH_SHORT).show()
            Log.e(TAG, "Laptop ID is null")
        }

        if (userId != null) {
            fetchUserData(userId)
        } else {
            Log.e(TAG, "User ID is null")
        }

        binding.ivProfile.setOnClickListener {
            findNavController().navigate(R.id.action_global_to_profileFragment)
        }

        binding.btnAddToWishlist.setOnClickListener {
            if (userId != null && laptopId != null) {
                // Path ke wishlist user
                val wishlistItemRef = database.getReference("users")
                    .child(userId)
                    .child("wishlist")
                    .child(laptopId)

                // Set nilainya menjadi true
                wishlistItemRef.setValue(true)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Ditambahkan ke wishlist!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Gagal menambahkan ke wishlist", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(context, "Anda harus login untuk menambahkan wishlist", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchLaptopDetails(laptopId: String) {
        val laptopRef = database.getReference("laptops").child(laptopId)
        laptopRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val laptop = snapshot.getValue(Laptop::class.java)
                if (laptop != null) {
                    populateUi(laptop)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Gagal memuat detail laptop", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchUserData(userId: String) {
        val userRef = database.getReference("users").child(userId)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (user != null && context != null) {
                    Glide.with(this@DetailFragment)
                        .load(user.photoURL)
                        .placeholder(R.drawable.ic_profile)
                        .error(R.drawable.ic_profile)
                        .circleCrop()
                        .into(binding.ivProfile)
                }
            }
            override fun onCancelled(error: DatabaseError) { /* Do nothing */ }
        })
    }

    private fun populateUi(laptop: Laptop) {
        // Mengisi data utama
        binding.tvProductName.text = laptop.modelName
        binding.tvAboutDescription.text = laptop.description

        // Menggunakan Glide untuk memuat gambar laptop
        if (context != null) {
            Glide.with(this)
                .load(laptop.imageUrl)
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_error)
                .into(binding.ivLaptop)
        }

        // Format harga
        val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        formatter.maximumFractionDigits = 0 // Hilangkan desimal
        val formattedPrice = formatter.format(laptop.price)
        binding.btnBuyNow.text = "Buy Now - $formattedPrice"

        // Mengisi spesifikasi secara dinamis
        populateSpecs(laptop)
    }

    private fun populateSpecs(laptop: Laptop) {
        binding.gridSpecs.removeAllViews() // Kosongkan grid sebelum diisi

        val specs = mapOf(
            R.drawable.ic_cpu to laptop.cpu,
            R.drawable.ic_ram to laptop.ram,
            R.drawable.ic_storage to laptop.storage,
            R.drawable.ic_gpu to laptop.gpu,
            R.drawable.ic_screen to "${laptop.screenSize} inch",
            R.drawable.ic_os to laptop.operatingSystem
        )

        val inflater = LayoutInflater.from(context)
        for ((iconRes, value) in specs) {
            if (value != null && value.isNotBlank()) {
                // Inflate item_spec_detail layout
                val specViewBinding = ItemSpecDetailBinding.inflate(inflater, binding.gridSpecs, false)

                // Set data
                specViewBinding.ivSpecIcon.setImageResource(iconRes)
                specViewBinding.tvSpecValue.text = value

                // Tambahkan view ke GridLayout
                binding.gridSpecs.addView(specViewBinding.root)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}