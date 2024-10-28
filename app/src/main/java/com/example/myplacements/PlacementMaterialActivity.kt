package com.example.myplacements

import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class PlacementMaterialActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PlacementMaterialAdapter
    private lateinit var searchView: SearchView
    private val contributionsList = mutableListOf<Contribution>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_placement_material)

        // Initialize RecyclerView and SearchView
        recyclerView = findViewById(R.id.recyclerView)
        searchView = findViewById(R.id.searchView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PlacementMaterialAdapter(contributionsList)
        recyclerView.adapter = adapter

        // Fetch data from Firebase
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("Contributions")

        reference.get().addOnSuccessListener { snapshot ->
            contributionsList.clear()
            for (contributionSnapshot in snapshot.children) {
                val contribution = contributionSnapshot.getValue(Contribution::class.java)
                contribution?.let { contributionsList.add(it) }
            }

            // Sort by company name alphabetically
            contributionsList.sortBy { it.companyName.toLowerCase(Locale.ROOT) }
            adapter.notifyDataSetChanged()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to load data", Toast.LENGTH_SHORT).show()
        }

        // Implement search functionality
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredList = contributionsList.filter {
                    it.companyName.contains(newText ?: "", true) ||
                            it.position.contains(newText ?: "", true)
                }
                adapter.updateList(filteredList)
                return true
            }
        })
    }
}
