package com.example.plantify_admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private MaterialButton materialButton;
    private EditText user_email, user_password;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the user is already logged in
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            // User is already logged in, navigate to Home activity
            Intent intent = new Intent(MainActivity.this, Home.class);
            intent.putExtra("userid", currentUser.getUid());
            startActivity(intent);
            finish(); // Prevent user from going back to login screen
            return;
        }

        // Continue with the normal login screen setup
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase App Check
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance()
        );

        // Initialize Firebase instances
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        // Initialize UI elements
        user_email = findViewById(R.id.user_email);
        user_password = findViewById(R.id.user_password);
        materialButton = findViewById(R.id.button_login);

        // Pre-fill email if available
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        String savedEmail = sharedPreferences.getString("user_email", "");
        user_email.setText(savedEmail);

        // Set up login button click listener
        materialButton.setOnClickListener(v -> {
            String email = user_email.getText().toString().trim();
            String password = user_password.getText().toString().trim();

            if (!isValidEmail(email)) {
                Toast.makeText(MainActivity.this, "Invalid email format", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            Login(email, password);
        });
    }

    // Method to handle login
    private void Login(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Login successful
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user != null) {
                                saveUserEmail(email); // Save the email for future use
                                Intent intent = new Intent(MainActivity.this, Home.class);
                                intent.putExtra("userid", user.getUid());
                                startActivity(intent);
                                finish(); // Prevent going back to login screen
                            } else {
                                Toast.makeText(MainActivity.this, "Login successful, but user not found.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Handle login failures
                            if (task.getException() != null) {
                                String errorMessage;
                                if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                    errorMessage = "No account found for this email.";
                                } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                    errorMessage = "Incorrect password.";
                                } else {
                                    errorMessage = "Authentication failed: " + task.getException().getMessage();
                                }
                                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "Unknown error occurred during login.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    // Save the email to SharedPreferences
    private void saveUserEmail(String email) {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_email", email);
        editor.apply(); // Save changes
    }

    // Utility method to validate email format
    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
