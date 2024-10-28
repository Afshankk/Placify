package com.example.myplacements

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class ContributeMaterialActivity : AppCompatActivity() {

    private lateinit var nameInput: EditText
    private lateinit var companyNameInput: EditText
    private lateinit var positionInput: EditText
    private lateinit var interviewProcessInput: EditText
    private lateinit var linkInput: EditText
    private lateinit var linkedinInput: EditText
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contribute_material)

        // Initialize the views
        nameInput = findViewById(R.id.name_input)
        companyNameInput = findViewById(R.id.company_name_input)
        positionInput = findViewById(R.id.position_input)
        interviewProcessInput = findViewById(R.id.interview_process_input)
        linkInput = findViewById(R.id.link_input)
        linkedinInput = findViewById(R.id.linkedin_input)
        submitButton = findViewById(R.id.btn_submit_contribution)

        // Set up the submit button listener
        submitButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val companyName = companyNameInput.text.toString().trim()
            val position = positionInput.text.toString().trim()
            val interviewProcess = interviewProcessInput.text.toString().trim()
            val link = linkInput.text.toString().trim()
            val linkedin = linkedinInput.text.toString().trim()


            // Validate the required fields
            if (name.isEmpty() || companyName.isEmpty() || position.isEmpty() || interviewProcess.isEmpty()) {
                Toast.makeText(this, "Name, Company Name, Position, and Interview Process are mandatory", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate LinkedIn URL
            if (linkedin.isNotEmpty() && !linkedin.startsWith("https://www.linkedin.com/")) {
                Toast.makeText(this, "Please provide a valid LinkedIn profile URL", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            // Save data to Firebase
            val database = FirebaseDatabase.getInstance()
            val reference = database.getReference("Contributions")

            val contributionId = reference.push().key ?: return@setOnClickListener

            val contributionData = mapOf(
                "name" to name,
                "companyName" to companyName,
                "position" to position,
                "interviewProcess" to interviewProcess,
                "link" to link,
                "linkedin" to linkedin,

            )

            // Push the data to Firebase under a unique ID
            reference.child(contributionId).setValue(contributionData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Contribution Submitted Successfully", Toast.LENGTH_SHORT).show()
                    // Clear the form after submission
                    nameInput.text.clear()
                    companyNameInput.text.clear()
                    positionInput.text.clear()
                    interviewProcessInput.text.clear()
                    linkInput.text.clear()
                    linkedinInput.text.clear()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to submit contribution", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
