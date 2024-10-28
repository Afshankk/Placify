package com.example.myplacements

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import java.util.*

class AlumniContactActivity : AppCompatActivity() {

    private lateinit var contributorsContainer: LinearLayout
    private lateinit var searchView: SearchView
    private val contributors = mutableListOf<Contributor>()
    private val filteredContributors = mutableListOf<Contributor>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alumni_contact)

        // Initialize views
        contributorsContainer = findViewById(R.id.contributors_container)
        searchView = findViewById(R.id.searchView)

        // Fetch data from Firebase
        fetchContributors()

        // Set up search listener
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterContributors(newText)
                return true
            }
        })
    }

    private fun fetchContributors() {
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("Contributions")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                contributors.clear()

                for (snapshot in dataSnapshot.children) {
                    val contributor = snapshot.getValue(Contributor::class.java)
                    contributor?.let {
                        // Ensure at least one social handle exists
                        if (it.linkedin.isNotEmpty() || it.insta.isNotEmpty() || it.twitter.isNotEmpty()) {
                            contributors.add(it) // Add valid contributors
                        }
                    }
                }

                // Remove duplicates based on LinkedIn ID
                removeDuplicateLinkedInIds()

                // Sort contributors by name
                contributors.sortBy { it.name }

                // Display contributors
                displayContributors()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@AlumniContactActivity, "Failed to fetch data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun removeDuplicateLinkedInIds() {
        val uniqueLinkedInIds = mutableSetOf<String>()
        contributors.removeIf { !uniqueLinkedInIds.add(it.linkedin) }
    }

    private fun filterContributors(query: String?) {
        filteredContributors.clear()

        if (query.isNullOrEmpty()) {
            filteredContributors.addAll(contributors)
        } else {
            val lowerCaseQuery = query.lowercase(Locale.getDefault())
            filteredContributors.addAll(
                contributors.filter { it.name.lowercase(Locale.getDefault()).contains(lowerCaseQuery) }
            )
        }

        displayContributors()
    }

    private fun displayContributors() {
        contributorsContainer.removeAllViews() // Clear previous views

        for (contributor in filteredContributors) {
            val contributorLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setPadding(8, 8, 8, 8)
                background = resources.getDrawable(R.drawable.box_background) // Set a background drawable for the box
            }

            // Horizontal layout for contributor name and dropdown icon
            val nameAndIconLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setPadding(8, 8, 8, 8)
                gravity = Gravity.CENTER_VERTICAL
            }

            // ImageView for LinkedIn profile picture (Placeholder image for now)
            val profileImageView = ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams(64, 64) // Size for the image
                setImageResource(R.drawable.icon_account_circle) // Placeholder image
            }

            // TextView for contributor name
            val tvName = TextView(this).apply {
                text = contributor.name
                textSize = 18f
                layoutParams = LinearLayout.LayoutParams(
                    0, // Width set to 0 for weight
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f // Weight for the TextView to take remaining space
                )
            }

            // Layout for social handles
            val handlesLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                visibility = View.GONE // Initially hidden
                setPadding(16, 4, 8, 4)
            }

            // Add social links to handlesLayout
            if (contributor.linkedin.isNotEmpty()) {
                val linkedinTextView = TextView(this).apply {
                    text = "LinkedIn: ${contributor.linkedin}"
                    textSize = 16f
                    setPadding(0, 4, 0, 4)
                    setTextColor(resources.getColor(android.R.color.holo_blue_dark)) // Set link color
                    setOnClickListener {
                        openLink(contributor.linkedin)
                    }
                }
                handlesLayout.addView(linkedinTextView)
            }

            if (contributor.insta.isNotEmpty()) {
                val instaTextView = TextView(this).apply {
                    text = "Instagram: ${contributor.insta}"
                    textSize = 16f
                    setPadding(0, 4, 0, 4)
                    setTextColor(resources.getColor(android.R.color.holo_blue_dark)) // Set link color
                    setOnClickListener {
                        openLink(contributor.insta)
                    }
                }
                handlesLayout.addView(instaTextView)
            }

            if (contributor.twitter.isNotEmpty()) {
                val twitterTextView = TextView(this).apply {
                    text = "Twitter: ${contributor.twitter}"
                    textSize = 16f
                    setPadding(0, 4, 0, 4)
                    setTextColor(resources.getColor(android.R.color.holo_blue_dark)) // Set link color
                    setOnClickListener {
                        openLink(contributor.twitter)
                    }
                }
                handlesLayout.addView(twitterTextView)
            }

            // ImageView for dropdown icon
            val dropdownIcon = ImageView(this).apply {
                setImageResource(android.R.drawable.arrow_down_float) // Use an arrow icon
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setPadding(8, 0, 0, 0)
                setOnClickListener {
                    // Toggle visibility of handlesLayout
                    handlesLayout.visibility = if (handlesLayout.visibility == View.VISIBLE) View.GONE else View.VISIBLE
                }
            }

            // Add profile image and name to the horizontal layout
            nameAndIconLayout.addView(profileImageView)
            nameAndIconLayout.addView(tvName)
            nameAndIconLayout.addView(dropdownIcon)

            // Add views to contributor layout
            contributorLayout.addView(nameAndIconLayout) // Add the horizontal layout
            contributorLayout.addView(handlesLayout) // Add the handles layout

            contributorsContainer.addView(contributorLayout)
        }
    }

    private fun openLink(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    // Contributor data class
    data class Contributor(
        var name: String = "",
        var linkedin: String = "",
        var insta: String = "",
        var twitter: String = ""
    )
}
