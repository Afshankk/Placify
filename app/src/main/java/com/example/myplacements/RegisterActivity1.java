//package com.example.myplacements;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.InputType;
//import android.util.Patterns;
//import android.view.View;
//import android.widget.*;
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.myplacements.HomeActivity;
//import com.example.myplacements.MainActivity;
//import com.example.myplacements.R;
//import com.google.android.gms.auth.api.signin.GoogleSignIn;
//import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
//import com.google.android.gms.auth.api.signin.GoogleSignInClient;
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.GoogleAuthProvider;
//
//import java.util.*;
//
//public class RegisterActivity1 extends AppCompatActivity {
//    private FirebaseAuth auth;
//    private GoogleSignInClient googleSignInClient;
//
//    private boolean isPasswordVisible = false;
//    private TextView captchaPrompt;
//    private GridLayout captchaGrid;
//    private List<ImageView> captchaImages;
//
//    private final List<Integer> imageResources = Arrays.asList(
//            R.drawable.car1, R.drawable.car2, R.drawable.car3,
//            R.drawable.boat1, R.drawable.boat2, R.drawable.boat3,
//            R.drawable.plane1, R.drawable.plane2, R.drawable.plane3
//    );
//
//    private final Map<Integer, String> imageClasses = new HashMap<Integer, String>() {{
//        put(R.drawable.car1, "car");
//        put(R.drawable.car2, "car");
//        put(R.drawable.car3, "car");
//        put(R.drawable.boat1, "boat");
//        put(R.drawable.boat2, "boat");
//        put(R.drawable.boat3, "boat");
//        put(R.drawable.plane1, "plane");
//        put(R.drawable.plane2, "plane");
//        put(R.drawable.plane3, "plane");
//    }};
//
//    private String currentCaptchaClass = "";
//    private final Set<Integer> correctImageIds = new HashSet<>();
//    private final Set<Integer> selectedImageIds = new HashSet<>();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.register);
//
//        auth = FirebaseAuth.getInstance();
//        initializeViews();
//
//        // Set up Google Sign-In options
//        setupGoogleSignIn();
//
//        // Generate initial CAPTCHA
//        generateImageCaptcha();
//
//        // Set up button click listeners
//        setupButtonListeners();
//    }
//
//    private void initializeViews() {
//        captchaPrompt = findViewById(R.id.captcha_prompt);
//        captchaGrid = findViewById(R.id.captcha_grid);
//        captchaImages = Arrays.asList(
//                findViewById(R.id.captcha_image1),
//                findViewById(R.id.captcha_image2),
//                findViewById(R.id.captcha_image3),
//                findViewById(R.id.captcha_image4),
//                findViewById(R.id.captcha_image5),
//                findViewById(R.id.captcha_image6),
//                findViewById(R.id.captcha_image7),
//                findViewById(R.id.captcha_image8),
//                findViewById(R.id.captcha_image9)
//        );
//    }
//
//    private void setupGoogleSignIn() {
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//        googleSignInClient = GoogleSignIn.getClient(this, gso);
//    }
//
//    private void setupButtonListeners() {
//        EditText emailInput = findViewById(R.id.username_input);
//        EditText passwordInput = findViewById(R.id.password_input);
//        Button registerButton = findViewById(R.id.register_btn);
//        Button loginButton = findViewById(R.id.login_btn);
//        Button googleSignInButton = findViewById(R.id.google_sign_in_btn);
//        ImageView eyeIcon = findViewById(R.id.eye_icon);
//
//        // Register button click listener
//        registerButton.setOnClickListener(view -> {
//            String email = emailInput.getText().toString().trim();
//            String password = passwordInput.getText().toString().trim();
//
//            if (validateEmail(email) && validatePassword(password) && validateImageCaptcha()) {
//                registerUser(email, password);
//            } else {
//                Toast.makeText(this, "Please complete the CAPTCHA and enter valid email/password.", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        // Login button click listener
//        loginButton.setOnClickListener(view -> {
//            startActivity(new Intent(this, MainActivity.class));
//            finish();
//        });
//
//        // Google sign-in button click listener
//        googleSignInButton.setOnClickListener(view -> signInWithGoogle());
//
//        // Eye icon click listener for password visibility
//        eyeIcon.setOnClickListener(view -> {
//            isPasswordVisible = !isPasswordVisible;
//            passwordInput.setInputType(isPasswordVisible ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD : (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD));
//            eyeIcon.setImageResource(isPasswordVisible ? R.drawable.ic_eye_open : R.drawable.ic_eye_closed);
//            passwordInput.setSelection(passwordInput.getText().length()); // Move cursor to end
//        });
//    }
//
//    private void generateImageCaptcha() {
//        selectedImageIds.clear();
//        for (ImageView imageView : captchaImages) {
//            imageView.setAlpha(1.0f); // Reset image transparency
//            imageView.setBackgroundResource(R.drawable.rounded_corner); // Reset background
//        }
//
//        List<String> classes = new ArrayList<>(new HashSet<>(imageClasses.values()));
//        currentCaptchaClass = classes.get(new Random().nextInt(classes.size()));
//        captchaPrompt.setText("Select all images with " + currentCaptchaClass);
//
//        List<Integer> classImages = new ArrayList<>();
//        for (Integer imageResource : imageResources) {
//            if (imageClasses.get(imageResource).equals(currentCaptchaClass)) {
//                classImages.add(imageResource);
//            }
//        }
//
//        List<Integer> selectedClassImages = classImages.subList(0, Math.min(3, classImages.size()));
//        List<Integer> otherImages = new ArrayList<>();
//        for (Integer imageResource : imageResources) {
//            if (!imageClasses.get(imageResource).equals(currentCaptchaClass)) {
//                otherImages.add(imageResource);
//            }
//        }
//        Collections.shuffle(otherImages);
//
//        List<Integer> captchaImagesList = new ArrayList<>(selectedClassImages);
//        captchaImagesList.addAll(otherImages.subList(0, Math.min(6, otherImages.size())));
//        Collections.shuffle(captchaImagesList);
//
//        correctImageIds.clear();
//        correctImageIds.addAll(selectedClassImages);
//
//        for (int index = 0; index < captchaImages.size(); index++) {
//            ImageView imageView = captchaImages.get(index);
//            int imageResId = captchaImagesList.get(index);
//            imageView.setImageResource(imageResId);
//            imageView.setTag(imageResId);
//            imageView.setOnClickListener(this::onCaptchaImageClick);
//        }
//    }
//
//    private void onCaptchaImageClick(View view) {
//        ImageView imageView = (ImageView) view;
//        int imageResId = (int) imageView.getTag();
//
//        if (selectedImageIds.contains(imageResId)) {
//            selectedImageIds.remove(imageResId);
//            imageView.setAlpha(1.0f); // Unselected state
//            imageView.setBackgroundResource(R.drawable.rounded_corner);
//        } else {
//            selectedImageIds.add(imageResId);
//            imageView.setAlpha(0.5f); // Selected state
//        }
//    }
//
//    private boolean validateImageCaptcha() {
//        return selectedImageIds.equals(correctImageIds);
//    }
//
//    private boolean validateEmail(String email) {
//        return Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
//                (email.endsWith("@gmail.com") || email.endsWith("@yahoo.com") || email.endsWith("@outlook.com"));
//    }
//
//    private boolean validatePassword(String password) {
//        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{6,}$";
//        return !password.isEmpty() && password.matches(passwordPattern);
//    }
//
//    private void registerUser(String email, String password) {
//        auth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, task -> {
//                    if (task.isSuccessful()) {
//                        auth.getCurrentUser().sendEmailVerification()
//                                .addOnCompleteListener(verifyTask -> {
//                                    if (verifyTask.isSuccessful()) {
//                                        Toast.makeText(this, "Registration successful. Please verify your email.", Toast.LENGTH_LONG).show();
//                                        auth.signOut();
//                                        startActivity(new Intent(this, MainActivity.class));
//                                        finish();
//                                    } else {
//                                        Toast.makeText(this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                    } else {
//                        Toast.makeText(this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//
//    private void signInWithGoogle() {
//        Intent signInIntent = googleSignInClient.getSignInIntent();
//        resultLauncher.launch(signInIntent);
//    }
//
//    private final ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            result -> {
//                if (result.getResultCode() == RESULT_OK) {
//                    Intent data = result.getData();
//                    GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult();
//                    firebaseAuthWithGoogle(account);
//                } else {
//                    Toast.makeText(this, "Google sign-in failed.", Toast.LENGTH_SHORT).show();
//                }
//            });
//
//    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
//        String idToken = account.getIdToken();
//        auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
//                .addOnCompleteListener(this, task -> {
//                    if (task.isSuccessful()) {
//                        Toast.makeText(this, "Google Sign-In successful!", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(this, HomeActivity.class));
//                        finish();
//                    } else {
//                        Toast.makeText(this, "Google Sign-In failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//}
