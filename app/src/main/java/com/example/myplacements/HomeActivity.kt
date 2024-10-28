package com.example.myplacements

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class HomeActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val btnContributeMaterial: Button = findViewById(R.id.btn_contribute_material)
        val btnAlumniContact: Button = findViewById(R.id.btn_alumni_contact)
        val btnPlacementMaterial: Button = findViewById(R.id.btn_placement_material)
        val btnResumeTemplates: Button = findViewById(R.id.btn_resume_templates)
        val btnSignOut: ImageButton = findViewById(R.id.btn_sign_out)

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Navigate to the Contribute Material Activity
        btnContributeMaterial.setOnClickListener {
            val intent = Intent(this, ContributeMaterialActivity::class.java)
            startActivity(intent)
        }

        // Navigate to the Alumni Contact Activity
        btnAlumniContact.setOnClickListener {
            val intent = Intent(this, AlumniContactActivity::class.java)
            startActivity(intent)
        }

        // Navigate to the Placement Material Activity
        btnPlacementMaterial.setOnClickListener {
            val intent = Intent(this, PlacementMaterialActivity::class.java)
            startActivity(intent)
        }

        // Navigate to the Resume and Cover Letter Templates Activity
        btnResumeTemplates.setOnClickListener {
            val intent = Intent(this, ResumeTemplatesActivity::class.java) // Create this activity
            startActivity(intent)
        }



        // Sign-out button logic
        btnSignOut.setOnClickListener {
            signOutUser()
        }
    }

    // Sign out the user from Firebase and Google
    private fun signOutUser() {
        // Sign out from Google account
        googleSignInClient.signOut().addOnCompleteListener(this) {
            // Sign out from Firebase
            firebaseAuth.signOut()

            // Navigate to MainActivity (Login screen)
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish() // Close this activity
        }
    }
}
