package com.toptop.projek.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.toptop.projek.R
import com.toptop.projek.User
import com.toptop.projek.databinding.FragmentProfileBinding
import com.toptop.projek.ui.SharedViewModel

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var database: FirebaseDatabase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = FirebaseDatabase.getInstance()

        sharedViewModel.userId.observe(viewLifecycleOwner) { userId ->
            if (userId != null) {
                fetchProfileData(userId)
            } else {
                Toast.makeText(context, "Gagal memuat profil, user tidak ditemukan.", Toast.LENGTH_SHORT).show()
            }
        }

        setupClickListeners()
    }

    private fun fetchProfileData(userId: String) {
        val userRef = database.getReference("users").child(userId)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (user != null && context != null) {
                    binding.tvProfileName.text = user.displayName
                    binding.tvProfileEmail.text = user.email
                    Glide.with(this@ProfileFragment)
                        .load(user.photoURL)
                        .placeholder(R.drawable.ic_profile)
                        .error(R.drawable.ic_profile)
                        .into(binding.ivProfilePicture)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Gagal memuat profil.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupClickListeners() {
        binding.btnEditProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

        binding.btnLogout.setOnClickListener {
            // Gunakan global action untuk logout
            findNavController().navigate(R.id.action_global_to_loginActivity)
            // Tutup MainActivity agar tidak bisa kembali
            activity?.finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}