package com.toptop.projek.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.*
import com.toptop.projek.Laptop
import com.toptop.projek.R
import com.toptop.projek.databinding.FragmentFavoriteBinding
import com.toptop.projek.ui.SharedViewModel
import com.toptop.projek.ui.pencarian.SearchAdapter

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: FirebaseDatabase
    private val sharedViewModel: SharedViewModel by activityViewModels()

    // Kita bisa gunakan adapter yang sama dengan halaman pencarian
    private lateinit var wishlistAdapter: SearchAdapter
    private val laptopDetailsList = mutableListOf<Laptop>()
    private var wishlistListener: ValueEventListener? = null
    private var wishlistRef: DatabaseReference? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = FirebaseDatabase.getInstance()

        setupRecyclerView()

        sharedViewModel.userId.observe(viewLifecycleOwner) { userId ->
            if (userId != null) {
                fetchWishlistLaptopIds(userId)
            } else {
                binding.tvEmptyWishlist.isVisible = true
                binding.rvWishlist.isVisible = false
            }
        }
    }

    private fun setupRecyclerView() {
        wishlistAdapter = SearchAdapter { laptop ->
            val userId = sharedViewModel.userId.value
            val bundle = bundleOf("LAPTOP_ID" to laptop.id, "USER_ID" to userId)
            findNavController().navigate(R.id.action_navigation_favorite_to_detailFragment, bundle)
        }
        binding.rvWishlist.adapter = wishlistAdapter
    }

    private fun fetchWishlistLaptopIds(userId: String) {
        wishlistRef = database.getReference("users").child(userId).child("wishlist")

        wishlistListener = wishlistRef?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                laptopDetailsList.clear() // Kosongkan list setiap ada perubahan
                binding.tvEmptyWishlist.isVisible = !snapshot.exists()
                binding.rvWishlist.isVisible = snapshot.exists()

                if (snapshot.exists()) {
                    for (laptopIdSnapshot in snapshot.children) {
                        val laptopId = laptopIdSnapshot.key
                        if (laptopId != null) {
                            fetchLaptopDetails(laptopId)
                        }
                    }
                } else {
                    wishlistAdapter.submitList(emptyList())
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Gagal memuat wishlist.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchLaptopDetails(laptopId: String) {
        val laptopRef = database.getReference("laptops").child(laptopId)
        laptopRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val laptop = snapshot.getValue(Laptop::class.java)
                if (laptop != null) {
                    laptopDetailsList.add(laptop)
                    wishlistAdapter.submitList(laptopDetailsList.toList()) // Update adapter dengan list baru
                }
            }
            override fun onCancelled(error: DatabaseError) { /* Do nothing */ }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Hentikan listener saat fragment dihancurkan untuk menghindari memory leak
        wishlistListener?.let { wishlistRef?.removeEventListener(it) }
        _binding = null
    }
}