package com.toptop.projek.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.toptop.projek.User
import com.toptop.projek.databinding.FragmentEditProfileBinding
import com.toptop.projek.ui.SharedViewModel

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var database: FirebaseDatabase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = FirebaseDatabase.getInstance()

        val userId = sharedViewModel.userId.value
        if (userId != null) {
            // Isi form dengan data saat ini
            loadCurrentUserData(userId)
        }

        setupClickListeners(userId)
    }

    private fun loadCurrentUserData(userId: String) {
        database.getReference("users").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                binding.etDisplayName.setText(user?.displayName)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun setupClickListeners(userId: String?) {
        // Tombol kembali di toolbar
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        // Tombol simpan
        binding.btnSaveChanges.setOnClickListener {
            val newDisplayName = binding.etDisplayName.text.toString().trim()
            if (newDisplayName.isEmpty()) {
                Toast.makeText(context, "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (userId != null) {
                updateUser(userId, newDisplayName)
            }
        }
    }

    private fun updateUser(userId: String, newName: String) {
        database.getReference("users").child(userId).child("displayName").setValue(newName)
            .addOnSuccessListener {
                Toast.makeText(context, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show()
                // Kembali ke halaman profil
                findNavController().popBackStack()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Gagal memperbarui profil", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}