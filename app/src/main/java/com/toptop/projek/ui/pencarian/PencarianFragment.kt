package com.toptop.projek.ui.pencarian

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.*
import com.toptop.projek.Laptop
import com.toptop.projek.R
import com.toptop.projek.databinding.FragmentPencarianBinding
import java.util.*
import androidx.fragment.app.activityViewModels
import com.toptop.projek.ui.SharedViewModel


class PencarianFragment : Fragment() {

    private var _binding: FragmentPencarianBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: FirebaseDatabase
    private lateinit var searchAdapter: SearchAdapter
    private val fullLaptopList = mutableListOf<Laptop>()

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPencarianBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = FirebaseDatabase.getInstance()
        setupRecyclerView()
        fetchLaptops()
        setupSearchListener()
    }

    private fun setupRecyclerView() {
        searchAdapter = SearchAdapter { laptop ->
            // Ambil userId dari 'papan tulis'
            val userId = sharedViewModel.userId.value

            val bundle = bundleOf(
                "LAPTOP_ID" to laptop.id,
                "USER_ID" to userId
            )
            findNavController().navigate(R.id.action_navigation_pencarian_to_detailFragment, bundle)
        }
        binding.rvSearchResults.adapter = searchAdapter
    }

    private fun fetchLaptops() {
        val laptopsRef = database.getReference("laptops")
        laptopsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                fullLaptopList.clear()
                for (laptopSnapshot in snapshot.children) {
                    val laptop = laptopSnapshot.getValue(Laptop::class.java)
                    if (laptop != null) {
                        fullLaptopList.add(laptop)
                    }
                }
                // Tampilkan semua laptop pada awalnya
                searchAdapter.submitList(fullLaptopList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupSearchListener() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                filter(s.toString())
            }
        })
    }

    private fun filter(query: String) {
        val filteredList = mutableListOf<Laptop>()
        if (query.isEmpty()) {
            filteredList.addAll(fullLaptopList)
        } else {
            for (laptop in fullLaptopList) {
                if (laptop.modelName?.lowercase(Locale.ROOT)?.contains(query.lowercase(Locale.ROOT)) == true ||
                    laptop.brand?.lowercase(Locale.ROOT)?.contains(query.lowercase(Locale.ROOT)) == true) {
                    filteredList.add(laptop)
                }
            }
        }
        searchAdapter.submitList(filteredList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}