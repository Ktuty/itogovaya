package com.example.itogovaya.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.itogovaya.databinding.ActivityPlantDetailBinding
import com.example.itogovaya.model.Plant
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class PlantDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlantDetailBinding
    private lateinit var database: DatabaseReference
    private var plantId: String? = null
    private var currentPlant: Plant? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlantDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        plantId = intent.getStringExtra("PLANT_ID")

        if (plantId == null) {
            finish()
            return
        }

        // ИСПРАВЛЕНО: Указываем полный URL базы данных из логов
        val dbUrl = "https://itogovayaapp-default-rtdb.europe-west1.firebasedatabase.app"
        database = FirebaseDatabase.getInstance(dbUrl).getReference("plants/$plantId")

        fetchPlantDetails()

        binding.waterButton.setOnClickListener {
            updateWateringDate()
        }

        binding.deleteButton.setOnClickListener {
            deletePlant()
        }
    }

    private fun fetchPlantDetails() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                currentPlant = snapshot.getValue(Plant::class.java)
                currentPlant?.let { displayPlantData(it) }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@PlantDetailActivity, "Не удалось загрузить данные", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun displayPlantData(plant: Plant) {
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("ru"))

        binding.plantNameTextView.text = plant.name
        binding.plantTypeTextView.text = plant.type
        binding.wateringFrequencyTextView.text = "Поливать каждые ${plant.wateringFrequency} дней"
        plant.lastWateredDate?.let {
            binding.lastWateredTextView.text = "Последний полив: ${dateFormat.format(Date(it))}"
        }
        plant.nextWateringDate?.let {
            binding.nextWateringTextView.text = "Следующий полив: ${dateFormat.format(Date(it))}"
        }
    }

    private fun updateWateringDate() {
        currentPlant?.let { plant ->
            plant.wateringFrequency?.let { frequency ->
                val calendar = Calendar.getInstance()
                val newLastWatered = calendar.timeInMillis
                calendar.add(Calendar.DAY_OF_YEAR, frequency)
                val newNextWatering = calendar.timeInMillis

                val updates = mapOf(
                    "lastWateredDate" to newLastWatered,
                    "nextWateringDate" to newNextWatering
                )

                database.updateChildren(updates).addOnSuccessListener {
                    Toast.makeText(this, "Отлично! Дата полива обновлена.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deletePlant() {
        database.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Растение удалено", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Ошибка при удалении", Toast.LENGTH_SHORT).show()
            }
        }
    }
}