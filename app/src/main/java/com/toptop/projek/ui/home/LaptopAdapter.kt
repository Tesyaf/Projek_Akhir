package com.toptop.projek.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.toptop.projek.Laptop
import com.toptop.projek.R
import com.toptop.projek.databinding.ItemRecommendationBinding

// Ubah constructor untuk menerima listener
class LaptopAdapter(private val onLaptopClicked: (Laptop) -> Unit) : ListAdapter<Laptop, LaptopAdapter.LaptopViewHolder>(LaptopDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LaptopViewHolder {
        val binding = ItemRecommendationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LaptopViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LaptopViewHolder, position: Int) {
        val laptop = getItem(position)
        holder.bind(laptop)
    }

    inner class LaptopViewHolder(private val binding: ItemRecommendationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(laptop: Laptop) {
            binding.tvLaptopName.text = laptop.modelName
            Glide.with(itemView.context)
                .load(laptop.imageUrl)
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_error)
                .into(binding.ivLaptopImage)

            // Tambahkan OnClickListener
            itemView.setOnClickListener {
                onLaptopClicked(laptop)
            }
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