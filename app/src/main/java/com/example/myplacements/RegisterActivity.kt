package com.example.myplacements

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import java.util.*

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private var isPasswordVisible = false
    private lateinit var captchaPrompt: TextView
    private lateinit var captchaGrid: GridLayout
    private lateinit var captchaImages: List<ImageView>

    private val imageResources = listOf(
        R.drawable.car1, R.drawable.car2, R.drawable.car3,
        R.drawable.boat1, R.drawable.boat2, R.drawable.boat3,
        R.drawable.plane1, R.drawable.plane2, R.drawable.plane3
    )

    private val imageClasses = mapOf(
        R.drawable.car1 to "car", R.drawable.car2 to "car", R.drawable.car3 to "car",
        R.drawable.boat1 to "boat", R.drawable.boat2 to "boat", R.drawable.boat3 to "boat",
        R.drawable.plane1 to "plane", R.drawable.plane2 to "plane", R.drawable.plane3 to "plane"
    )

    private var currentCaptchaClass: String = ""
    private val correctImageIds = mutableSetOf<Int>()
    private val selectedImageIds = mutableSetOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)

        auth = FirebaseAuth.getInstance()
        initializeViews()

        // Set up Google Sign-In options
        setupGoogleSignIn()

        // Generate initial CAPTCHA
        generateImageCaptcha()

        // Set up button click listeners
        setupButtonListeners()
    }

    private fun initializeViews() {
        captchaPrompt = findViewById(R.id.captcha_prompt)
        captchaGrid = findViewById(R.id.captcha_grid)
        captchaImages = listOf(
            findViewById(R.id.captcha_image1),
            findViewById(R.id.captcha_image2),
            findViewById(R.id.captcha_image3),
            findViewById(R.id.captcha_image4),
            findViewById(R.id.captcha_image5),
            findViewById(R.id.captcha_image6),
            findViewById(R.id.captcha_image7),
            findViewById(R.id.captcha_image8),
            findViewById(R.id.captcha_image9)
        )
    }

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun setupButtonListeners() {
        val emailInput: EditText = findViewById(R.id.username_input)
        val passwordInput: EditText = findViewById(R.id.password_input)
        val registerButton: Button = findViewById(R.id.register_btn)
        val loginButton: Button = findViewById(R.id.login_btn)
        val googleSignInButton: Button = findViewById(R.id.google_sign_in_btn)
        val eyeIcon: ImageView = findViewById(R.id.eye_icon)

        // Register button click listener
        registerButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (validateEmail(email) && validatePassword(password) && validateImageCaptcha()) {
                registerUser(email, password)
            } else {
                Toast.makeText(this, "Please complete the CAPTCHA and enter valid email/password.", Toast.LENGTH_SHORT).show()
            }
        }

        // Login button click listener
        loginButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Google sign-in button click listener
        googleSignInButton.setOnClickListener { signInWithGoogle() }

        // Eye icon click listener for password visibility
        eyeIcon.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            passwordInput.inputType = if (isPasswordVisible) {
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            eyeIcon.setImageResource(if (isPasswordVisible) R.drawable.ic_eye_open else R.drawable.ic_eye_closed)
            passwordInput.setSelection(passwordInput.text.length) // Move cursor to end
        }
    }

    private fun generateImageCaptcha() {
        selectedImageIds.clear()
        captchaImages.forEach { imageView ->
            imageView.alpha = 1.0f // Reset image transparency
            imageView.setBackgroundResource(R.drawable.rounded_corner) // Reset background
        }

        val classes = imageClasses.values.toSet().toList()
        currentCaptchaClass = classes[Random().nextInt(classes.size)]
        captchaPrompt.text = "Select all images with $currentCaptchaClass"

        val classImages = imageResources.filter { imageClasses[it] == currentCaptchaClass }
        val selectedClassImages = classImages.shuffled().take(3)
        val otherImages = imageResources.filter { imageClasses[it] != currentCaptchaClass }.shuffled().take(6)

        val captchaImagesList = (selectedClassImages + otherImages).shuffled()
        correctImageIds.clear()
        correctImageIds.addAll(selectedClassImages)

        captchaImages.forEachIndexed { index, imageView ->
            val imageResId = captchaImagesList[index]
            imageView.setImageResource(imageResId)
            imageView.tag = imageResId
            imageView.setOnClickListener { onCaptchaImageClick(it) }
        }
    }

    private fun onCaptchaImageClick(view: View) {
        val imageView = view as ImageView
        val imageResId = imageView.tag as Int

        if (selectedImageIds.contains(imageResId)) {
            selectedImageIds.remove(imageResId)
            imageView.alpha = 1.0f // Unselected state
            imageView.setBackgroundResource(R.drawable.rounded_corner)
        } else {
            selectedImageIds.add(imageResId)
            imageView.alpha = 0.5f // Selected state
        }
    }

    private fun validateImageCaptcha(): Boolean {
        return selectedImageIds == correctImageIds
    }

    private fun validateEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
                (email.endsWith("@gmail.com") || email.endsWith("@yahoo.com") || email.endsWith("@outlook.com"))
    }

    private fun validatePassword(password: String): Boolean {
        val passwordPattern = Regex("^" +
                "(?=.*[0-9])" +
                "(?=.*[a-z])" +
                "(?=.*[A-Z])" +
                "(?=.*[@#$%^&+=])" +
                "(?=\\S+$)" +
                ".{6,}" +
                "$")
        return password.isNotEmpty() && passwordPattern.matches(password)
    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    auth.currentUser?.sendEmailVerification()?.addOnCompleteListener { verifyTask ->
                        if (verifyTask.isSuccessful) {
                            Toast.makeText(this, "Registration successful. Please verify your email.", Toast.LENGTH_LONG).show()
                            auth.signOut()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Failed to send verification email.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val accountTask = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = accountTask.getResult(Exception::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: Exception) {
                Toast.makeText(this, "Google sign-in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Google Sign-In successful!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Google Sign-In failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}