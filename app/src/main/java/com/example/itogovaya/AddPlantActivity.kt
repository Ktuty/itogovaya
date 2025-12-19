package com.example.itogovaya.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.itogovaya.databinding.ActivityAddPlantBinding
import com.example.itogovaya.model.Plant
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class AddPlantActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPlantBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPlantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ИСПРАВЛЕНО: Указываем полный URL базы данных из логов
        val dbUrl = "https://itogovayaapp-default-rtdb.europe-west1.firebasedatabase.app"
        database = FirebaseDatabase.getInstance(dbUrl).getReference("plants")

        binding.saveButton.setOnClickListener {
            savePlantDataToDatabase()
        }
    }

    private fun savePlantDataToDatabase() {
        val name = binding.nameEditText.text.toString().trim()
        val type = binding.typeEditText.text.toString().trim()
        val wateringFrequency = binding.wateringFrequencyEditText.text.toString().toIntOrNull()

        if (name.isEmpty() || type.isEmpty() || wateringFrequency == null) {
            Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        binding.saveButton.isEnabled = false

        val plantId = database.push().key
        if (plantId == null) {
            Toast.makeText(this, "Не удалось создать ID для растения", Toast.LENGTH_SHORT).show()
            binding.progressBar.visibility = View.GONE
            binding.saveButton.isEnabled = true
            return
        }

        val calendar = Calendar.getInstance()
        val lastWateredDate = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_YEAR, wateringFrequency)
        val nextWateringDate = calendar.timeInMillis

        val plant = Plant(plantId, name, type, wateringFrequency, lastWateredDate, nextWateringDate)

        database.child(plantId).setValue(plant)
            .addOnCompleteListener { task ->
                binding.progressBar.visibility = View.GONE
                binding.saveButton.isEnabled = true
                if (task.isSuccessful) {
                    Toast.makeText(this, "Растение успешно добавлено!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Ошибка: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}