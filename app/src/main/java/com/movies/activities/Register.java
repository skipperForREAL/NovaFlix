// File: com/expense/activities/Register.java
package com.movies.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.movies.R;
import com.movies.utils.FirebaseAuthManager;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class Register extends AppCompatActivity {

    // View references
    private TextInputLayout usernameInputLayout, emailInputLayout, passwordInputLayout;
    private EditText usernameEditText, emailEditText, passwordEditText;
    private MaterialButton signUpButton, googleSignUpButton, appleSignUpButton;
    private TextView termsTextView, signInLinkTextView;

    private GoogleSignInClient googleSignInClient;

    private final ActivityResultLauncher<Intent> googleSignInLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            handleGoogleSignUp(result.getData());
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        View signUpView = findViewById(R.id.signUpRoot);
        if (signUpView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(signUpView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        initViews();
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        googleSignInClient = FirebaseAuthManager.getGoogleSignInClient(this);

        if (FirebaseAuthManager.isSignedIn()) {
            navigateToMain();
            return;
        }

        setupClickListeners();
    }

    private void initViews() {
        usernameInputLayout = findViewById(R.id.usernameInputLayout);
        emailInputLayout = findViewById(R.id.emailInputLayout);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);
        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signUpButton = findViewById(R.id.signUpButton);
        googleSignUpButton = findViewById(R.id.googleSignUpButton);
        appleSignUpButton = findViewById(R.id.appleSignUpButton);
        termsTextView = findViewById(R.id.termsTextView);
        signInLinkTextView = findViewById(R.id.signInLinkTextView);
    }

    private void setupClickListeners() {
        // Email Sign-Up
        signUpButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString();

            if (username.isEmpty()) {
                usernameInputLayout.setError("Username required");
                return;
            }
            if (email.isEmpty()) {
                emailInputLayout.setError("Email required");
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInputLayout.setError("Invalid email format");
                return;
            }
            if (password.length() < 6) {
                passwordInputLayout.setError("Password must be 6+ characters");
                return;
            }
            // Clear errors
            usernameInputLayout.setError(null);
            emailInputLayout.setError(null);
            passwordInputLayout.setError(null);

            performEmailSignUp(email, password, username);
        });

        // Google Sign-Up
        googleSignUpButton.setOnClickListener(v -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        });

        // Apple Sign-Up
        appleSignUpButton.setOnClickListener(v -> {
            FirebaseAuthManager.signInWithApple(Register.this)
                    .addOnSuccessListener(authResult -> {
                        Toast.makeText(Register.this, "Account created with Apple!", Toast.LENGTH_SHORT).show();
                        navigateToMain();
                    })
                    .addOnFailureListener(e -> {
                        if (e.getMessage() != null && !e.getMessage().contains("cancelled")) {
                            Toast.makeText(Register.this, "Apple sign-up failed: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Navigate to Login
        signInLinkTextView.setOnClickListener(v -> {
            Intent intent = new Intent(Register.this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void performEmailSignUp(String email, String password, String username) {
        setLoadingState(true);

        FirebaseAuthManager.signUpWithEmail(email, password, username)
                .addOnSuccessListener(authResult -> {
                    setLoadingState(false);
                    Toast.makeText(Register.this, "Account created!", Toast.LENGTH_SHORT).show();
                    navigateToMain();
                })
                .addOnFailureListener(e -> {
                    setLoadingState(false);
                    String message = "Sign-up failed";
                    if (e instanceof FirebaseAuthUserCollisionException) {
                        message = "An account already exists with this email";
                    } else if (e instanceof FirebaseAuthWeakPasswordException) {
                        message = "Password is too weak";
                    } else if (e.getMessage() != null) {
                        message = e.getMessage();
                    }
                    Toast.makeText(Register.this, message, Toast.LENGTH_LONG).show();
                });
    }

    private void handleGoogleSignUp(Intent data) {
        setLoadingState(true);

        FirebaseAuthManager.handleGoogleSignInResult(data)
                .addOnSuccessListener(authResult -> {
                    setLoadingState(false);
                    Toast.makeText(Register.this, "Account created with Google!", Toast.LENGTH_SHORT).show();
                    navigateToMain();
                })
                .addOnFailureListener(e -> {
                    setLoadingState(false);
                    Toast.makeText(Register.this, "Google sign-up failed: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    private void setLoadingState(boolean isLoading) {
        signUpButton.setEnabled(!isLoading);
        signUpButton.setText(isLoading ? "Creating..." : "Create Account");
        googleSignUpButton.setEnabled(!isLoading);
        appleSignUpButton.setEnabled(!isLoading);
    }

    private void navigateToMain() {
        Intent intent = new Intent(Register.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}