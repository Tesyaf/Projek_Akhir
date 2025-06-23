package com.toptop.projek.ui.pencarian

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.toptop.projek.Laptop
import com.toptop.projek.R
import com.toptop.projek.databinding.ItemSearchResultBinding
import java.text.NumberFormat
import java.util.*

class SearchAdapter(private val onClick: (Laptop) -> Unit) :
    ListAdapter<Laptop, SearchAdapter.ViewHolder>(LaptopDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemSearchResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val laptop = getItem(position)
        holder.bind(laptop)
        holder.itemView.setOnClickListener { onClick(laptop) }
    }

    inner class ViewHolder(private val binding: ItemSearchResultBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(laptop: Laptop) {
            binding.tvModelName.text = laptop.modelName
            binding.tvDescription.text = laptop.description

            // 1. Buat formatter untuk mata uang Indonesia (IDR)
            val localeID = Locale("in", "ID")
            val formatter = NumberFormat.getCurrencyInstance(localeID)
            formatter.maximumFractionDigits = 0 // Opsional: untuk menghilangkan ,00 di akhir

            // 2. Format harga langsung dari database tanpa dibagi
            binding.tvPrice.text = formatter.format(laptop.price ?: 0)

            Glide.with(itemView.context).load(laptop.imageUrl).into(binding.ivLaptopImage)

            // Note: Logic untuk menampilkan spec chips bisa ditambahkan di sini jika diperlukan
        }
    }

    class LaptopDiffCallback : DiffUtil.ItemCallback<Laptop>() {
        override fun areItemsTheSame(oldItem: Laptop, newItem: Laptop): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Laptop, newItem: Laptop): Boolean {
            return oldItem == newItem
        }
    }
}