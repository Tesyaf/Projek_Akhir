package com.toptop.projek.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.toptop.projek.Laptop
import com.toptop.projek.R
import com.toptop.projek.User
import com.toptop.projek.databinding.FragmentHomeBinding
import androidx.navigation.fragment.findNavController
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import com.toptop.projek.ui.SharedViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: FirebaseDatabase
    private lateinit var laptopAdapter: LaptopAdapter

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi Firebase Database
        database = FirebaseDatabase.getInstance()

        // Setup RecyclerView
        setupRecyclerView()
        val currentUserId = sharedViewModel.userId.value

        if (currentUserId != null) {
            fetchUserData(currentUserId)
        } else {
            // Handle kasus di mana ID tidak terkirim
            Toast.makeText(context, "Gagal mendapatkan data user.", Toast.LENGTH_SHORT).show()
        }
        binding.ivProfile.setOnClickListener {
            findNavController().navigate(R.id.action_global_to_profileFragment)
        }

        fetchLaptopsData()
    }

    private fun setupRecyclerView() {
        laptopAdapter = LaptopAdapter { laptop ->
            val bundle = bundleOf(
                "LAPTOP_ID" to laptop.id,
                "USER_ID" to arguments?.getString("USER_ID")
            )
            findNavController().navigate(R.id.action_navigation_home_to_detailFragment, bundle)
        }
        binding.rvRecommendations.adapter = laptopAdapter
    }

    private fun fetchUserData(userId: String) {
        val userRef = database.getReference("users").child(userId)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    binding.tvUserName.text = user.displayName
                    Glide.with(this@HomeFragment)
                        .load(user.photoURL)
                        .placeholder(R.drawable.ic_profile) // Placeholder default
                        .error(R.drawable.ic_profile)       // Gambar jika error
                        .circleCrop() // Membuat gambar menjadi lingkaran
                        .into(binding.ivProfile)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Gagal memuat data user", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchLaptopsData() {
        val laptopsRef = database.getReference("laptops")
        laptopsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val laptopList = mutableListOf<Laptop>()
                for (laptopSnapshot in snapshot.children) {
                    val laptop = laptopSnapshot.getValue(Laptop::class.java)
                    if (laptop != null) {
                        laptopList.add(laptop)
                    }
                }
                // Kirim daftar laptop ke adapter
                laptopAdapter.submitList(laptopList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Gagal memuat data laptop", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}