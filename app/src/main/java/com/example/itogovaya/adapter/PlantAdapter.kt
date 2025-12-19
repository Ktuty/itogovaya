package com.example.itogovaya.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.itogovaya.databinding.PlantItemBinding
import com.example.itogovaya.model.Plant
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class PlantAdapter(
    private val plants: List<Plant>,
    private val onItemClicked: (Plant) -> Unit
) : RecyclerView.Adapter<PlantAdapter.PlantViewHolder>() {

    class PlantViewHolder(val binding: PlantItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val binding = PlantItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        val plant = plants[position]
        holder.binding.plantNameTextView.text = plant.name

        plant.nextWateringDate?.let {
            val today = Calendar.getInstance()
            today.set(Calendar.HOUR_OF_DAY, 0); today.set(Calendar.MINUTE, 0); today.set(Calendar.SECOND, 0); today.set(Calendar.MILLISECOND, 0)
            val nextWateringDay = Calendar.getInstance().apply { timeInMillis = it }
            val diff = nextWateringDay.timeInMillis - today.timeInMillis
            val days = TimeUnit.MILLISECONDS.toDays(diff)

            val dateText = when {
                days < 0 -> "Полив просрочен!"
                days == 0L -> "Полить сегодня"
                days == 1L -> "Полить завтра"
                else -> "Следующий полив: ${SimpleDateFormat("dd MMMM", Locale("ru")).format(Date(it))}"
            }
            holder.binding.nextWateringTextView.text = dateText
        }

        holder.itemView.setOnClickListener {
            onItemClicked(plant)
        }
    }

    override fun getItemCount() = plants.size
}