package com.toptop.projek.ui.pencarian

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.toptop.projek.databinding.FragmentPencarianBinding
import com.toptop.projek.ui.SharedViewModel
import java.util.*

class PencarianFragment : Fragment() {

    private var _binding: FragmentPencarianBinding? = null
    private val binding get() = _binding!!

    // Menggunakan 'val' karena tidak akan diinisialisasi ulang
    private val database = FirebaseDatabase.getInstance()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var searchAdapter: SearchAdapter
    private val fullLaptopList = mutableListOf<Laptop>()
    private val selectedLaptopIds = mutableSetOf<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPencarianBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        fetchLaptops()
        setupSearchListener()
        setupClickListeners() // <-- Panggil fungsi baru untuk listener tombol
    }

    private fun setupRecyclerView() {
        // Inisialisasi adapter dengan DUA listener: satu untuk klik item, satu untuk perubahan checkbox
        searchAdapter = SearchAdapter(
            onClick = { laptop ->
                // Logika saat seluruh item diklik (pindah ke detail)
                val userId = sharedViewModel.userId.value
                val bundle = bundleOf("LAPTOP_ID" to laptop.id, "USER_ID" to userId)
                findNavController().navigate(R.id.action_navigation_pencarian_to_detailFragment, bundle)
            },
            onSelectionChanged = { selectedIds ->
                // Logika saat checkbox dicentang/dihilangkan centangnya
                selectedLaptopIds.clear()
                selectedLaptopIds.addAll(selectedIds)
                updateCompareButtonState() // Perbarui tampilan tombol "Bandingkan"
            }
        )
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
                searchAdapter.submitList(fullLaptopList.toList())
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

    // FUNGSI BARU UNTUK MENGATUR TOMBOL BANDINGKAN
    private fun setupClickListeners() {
        binding.btnCompare.setOnClickListener {
            if (selectedLaptopIds.size == 2) {
                val ids = selectedLaptopIds.toList()
                val action = PencarianFragmentDirections.actionNavigationPencarianToComparisonFragment(
                    laptopId1 = ids[0],
                    laptopId2 = ids[1]
                )
                findNavController().navigate(action)
            }
        }
    }

    // FUNGSI BARU UNTUK MEMPERBARUI TAMPILAN TOMBOL
    private fun updateCompareButtonState() {
        val selectedCount = selectedLaptopIds.size
        binding.btnCompare.text = "Bandingkan ($selectedCount/2)"

        // Tampilkan tombol jika ada minimal 1 item dipilih
        binding.btnCompare.isVisible = selectedCount > 0

        // Aktifkan tombol hanya jika TEPAT 2 item dipilih
        binding.btnCompare.isEnabled = selectedCount == 2
    }

    private fun filter(query: String) {
        val filteredList = mutableListOf<Laptop>()
        if (query.isEmpty()) {
            filteredList.addAll(fullLaptopList)
        } else {
            val lowerCaseQuery = query.lowercase(Locale.ROOT)
            for (laptop in fullLaptopList) {
                val modelName = laptop.modelName?.lowercase(Locale.ROOT) ?: ""
                val brand = laptop.brand?.lowercase(Locale.ROOT) ?: ""
                if (modelName.contains(lowerCaseQuery) || brand.contains(lowerCaseQuery)) {
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