package com.toptop.projek.ui.comparison

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.toptop.projek.Laptop
import com.toptop.projek.R
import com.toptop.projek.databinding.FragmentComparisonBinding
import com.toptop.projek.databinding.ItemComparisonCardBinding
import com.toptop.projek.databinding.ItemComparisonSpecBinding
import java.text.NumberFormat
import java.util.*
import kotlin.getValue

class ComparisonFragment : Fragment() {

    private var _binding: FragmentComparisonBinding? = null
    private val binding get() = _binding!!

    private val database = FirebaseDatabase.getInstance()
    private val args: ComparisonFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentComparisonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchAndPopulateLaptop(args.laptopId1, binding.includeCard1)
        fetchAndPopulateLaptop(args.laptopId2, binding.includeCard2)
    }

    private fun fetchAndPopulateLaptop(laptopId: String, cardBinding: ItemComparisonCardBinding) {
        val laptopRef = database.getReference("laptops").child(laptopId)
        laptopRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val laptop = snapshot.getValue(Laptop::class.java)
                if (laptop != null && context != null) {
                    // Isi data ke UI kartu
                    cardBinding.tvCompName.text = laptop.modelName
                    cardBinding.tvCompTagline.text = laptop.description?.split(".")?.firstOrNull() // Ambil kalimat pertama

                    val localeID = Locale("in", "ID")
                    val formatter = NumberFormat.getCurrencyInstance(localeID)
                    formatter.maximumFractionDigits = 0
                    cardBinding.tvCompPrice.text = formatter.format(laptop.price ?: 0L)

                    Glide.with(this@ComparisonFragment)
                        .load(laptop.imageUrl)
                        .placeholder(R.drawable.ic_image_placeholder)
                        .into(cardBinding.ivCompLaptop)

                    // Isi daftar spesifikasi
                    populateSpecs(cardBinding.llCompSpecs, laptop)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Gagal memuat data laptop ${laptopId}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun populateSpecs(specContainer: LinearLayout, laptop: Laptop) {
        specContainer.removeAllViews()
        val inflater = LayoutInflater.from(context)

        val specs = mapOf(
            R.drawable.ic_cpu to laptop.cpu,
            R.drawable.ic_ram to laptop.ram,
            R.drawable.ic_storage to laptop.storage,
            R.drawable.ic_gpu to laptop.gpu,
            R.drawable.ic_screen to "${laptop.screenSize}\" Screen"
        )

        for ((iconRes, value) in specs) {
            if (!value.isNullOrBlank()) {
                val specBinding = ItemComparisonSpecBinding.inflate(inflater, specContainer, false)
                specBinding.ivSpecIcon.setImageResource(iconRes)
                specBinding.tvSpecText.text = value
                specContainer.addView(specBinding.root)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}