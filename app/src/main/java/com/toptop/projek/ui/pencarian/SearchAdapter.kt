package com.toptop.projek.ui.pencarian

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.toptop.projek.Laptop
import com.toptop.projek.R
import com.toptop.projek.databinding.ItemSearchResultBinding
import java.text.NumberFormat
import java.util.*

class SearchAdapter(
    private val onClick: (Laptop) -> Unit,
    private val onSelectionChanged: ((Set<String>) -> Unit)? = null
) : ListAdapter<Laptop, SearchAdapter.ViewHolder>(LaptopDiffCallback()) {

    private val selectedItems = mutableSetOf<String>()

    fun setSelectedItems(items: Set<String>) {
        selectedItems.clear()
        selectedItems.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemSearchResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val laptop = getItem(position)
        holder.bind(laptop)
    }

    inner class ViewHolder(private val binding: ItemSearchResultBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(laptop: Laptop) {
            // Mengisi data-data view
            binding.tvModelName.text = laptop.modelName
            binding.tvDescription.text = laptop.description

            val localeID = Locale("in", "ID")
            val formatter = NumberFormat.getCurrencyInstance(localeID)
            formatter.maximumFractionDigits = 0
            binding.tvPrice.text = formatter.format(laptop.price ?: 0)

            Glide.with(itemView.context)
                .load(laptop.imageUrl)
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_error)
                .into(binding.ivLaptopImage)

            // --- BAGIAN INI TELAH DIPERBAIKI ---
            binding.chipGroupSpecs.removeAllViews()
            val specs = listOfNotNull(laptop.cpu, laptop.ram, laptop.gpu)

            // Dapatkan inflater dari context untuk me-load layout XML
            val inflater = LayoutInflater.from(itemView.context)

            // Loop dan inflate chip dari layout XML
            for (spec in specs) {
                val chip = inflater.inflate(R.layout.item_spec_chip, binding.chipGroupSpecs, false) as Chip

                // Atur teksnya
                chip.text = spec

                // Tambahkan chip yang sudah jadi ke dalam ChipGroup
                binding.chipGroupSpecs.addView(chip)
            }
            // ------------------------------------

            // Logika kondisional untuk checkbox
            binding.cbCompare.isVisible = (onSelectionChanged != null)
            if (onSelectionChanged != null) {
                binding.cbCompare.setOnCheckedChangeListener(null)
                binding.cbCompare.isChecked = selectedItems.contains(laptop.id)
                binding.cbCompare.setOnCheckedChangeListener { _, isChecked ->
                    val laptopId = laptop.id ?: return@setOnCheckedChangeListener

                    if (isChecked) {
                        if (selectedItems.size < 2) {
                            selectedItems.add(laptopId)
                        } else {
                            binding.cbCompare.isChecked = false
                        }
                    } else {
                        selectedItems.remove(laptopId)
                    }
                    onSelectionChanged.invoke(selectedItems)
                }
            }

            // Atur klik pada seluruh item
            itemView.setOnClickListener {
                onClick(laptop)
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