package com.example.itogovaya.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast // <-- ВОТ ЭТА СТРОКА ДОБАВЛЕНА
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.itogovaya.adapter.PlantAdapter
import com.example.itogovaya.databinding.ActivityMainBinding
import com.example.itogovaya.model.Plant
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: DatabaseReference
    private lateinit var plantList: ArrayList<Plant>
    private lateinit var adapter: PlantAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Указываем полный URL базы данных из логов
        val dbUrl = "https://itogovayaapp-default-rtdb.europe-west1.firebasedatabase.app"
        database = FirebaseDatabase.getInstance(dbUrl).getReference("plants")

        plantList = ArrayList()
        adapter = PlantAdapter(plantList) { plant ->
            val intent = Intent(this, PlantDetailActivity::class.java)
            intent.putExtra("PLANT_ID", plant.id)
            startActivity(intent)
        }

        binding.plantsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.plantsRecyclerView.adapter = adapter

        fetchPlants()

        binding.addPlantFab.setOnClickListener {
            startActivity(Intent(this, AddPlantActivity::class.java))
        }
    }

    private fun fetchPlants() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                plantList.clear()
                for (plantSnapshot in snapshot.children) {
                    val plant = plantSnapshot.getValue(Plant::class.java)
                    plant?.let { plantList.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Обработка ошибки
                Toast.makeText(this@MainActivity, "Ошибка загрузки данных: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}