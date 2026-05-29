// File: com/expense/activities/Login.java
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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class Login extends AppCompatActivity {

    // View references
    private TextInputLayout emailInputLayout, passwordInputLayout;
    private EditText emailEditText, passwordEditText;
    private MaterialButton getStartedButton, googleSignInButton, appleSignInButton;
    private TextView forgotPasswordTextView, signUpTextView;

    private GoogleSignInClient googleSignInClient;

    private final ActivityResultLauncher<Intent> googleSignInLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            handleGoogleSignIn(result.getData());
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Handle system bars padding
        View loginView = findViewById(R.id.login);
        if (loginView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(loginView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // Initialize views
        initViews();

        // Initialize Google Sign-In
        googleSignInClient = FirebaseAuthManager.getGoogleSignInClient(this);

        // Check if already signed in
        if (FirebaseAuthManager.isSignedIn()) {
            navigateToMain();
            return;
        }

        setupClickListeners();
    }

    private void initViews() {
        emailInputLayout = findViewById(R.id.emailInputLayout);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        getStartedButton = findViewById(R.id.getStartedButton);
        googleSignInButton = findViewById(R.id.googleSignInButton);
        appleSignInButton = findViewById(R.id.appleSignInButton);
        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView);
        signUpTextView = findViewById(R.id.signUpTextView);
    }

    private void setupClickListeners() {
        // Email Login
        getStartedButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString();

            if (email.isEmpty()) {
                emailInputLayout.setError("Email required");
                return;
            }
            if (password.isEmpty()) {
                passwordInputLayout.setError("Password required");
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInputLayout.setError("Invalid email format");
                return;
            }
            // Clear errors
            emailInputLayout.setError(null);
            passwordInputLayout.setError(null);

            performEmailLogin(email, password);
        });

        // Google Sign-In
        googleSignInButton.setOnClickListener(v -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        });

        // Apple Sign-In
        appleSignInButton.setOnClickListener(v -> {
            FirebaseAuthManager.signInWithApple(Login.this)
                    .addOnSuccessListener(authResult -> {
                        Toast.makeText(Login.this, "Signed in with Apple!", Toast.LENGTH_SHORT).show();
                        navigateToMain();
                    })
                    .addOnFailureListener(e -> {
                        if (e.getMessage() != null && !e.getMessage().contains("cancelled")) {
                            Toast.makeText(Login.this, "Apple sign-in failed: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Forgot Password
        forgotPasswordTextView.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                sendPasswordReset(email);
            } else {
                Toast.makeText(this, "Enter your email first", Toast.LENGTH_SHORT).show();
            }
        });

        // Navigate to Sign Up
        signUpTextView.setOnClickListener(v ->
                startActivity(new Intent(Login.this, Register.class)));
    }

    private void performEmailLogin(String email, String password) {
        setLoadingState(true);

        FirebaseAuthManager.signInWithEmail(email, password)
                .addOnSuccessListener(authResult -> {
                    setLoadingState(false);
                    Toast.makeText(Login.this, "Welcome back!", Toast.LENGTH_SHORT).show();
                    FirebaseAuthManager.updateLastLogin(authResult.getUser().getUid());
                    navigateToMain();
                })
                .addOnFailureListener(e -> {
                    setLoadingState(false);
                    String message = "Login failed";
                    if (e instanceof FirebaseAuthInvalidUserException) {
                        message = "No account found with this email";
                    } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        message = "Invalid password";
                    } else if (e.getMessage() != null) {
                        message = e.getMessage();
                    }
                    Toast.makeText(Login.this, message, Toast.LENGTH_LONG).show();
                });
    }

    private void handleGoogleSignIn(Intent data) {
        setLoadingState(true);

        FirebaseAuthManager.handleGoogleSignInResult(data)
                .addOnSuccessListener(authResult -> {
                    setLoadingState(false);
                    Toast.makeText(Login.this, "Signed in with Google!", Toast.LENGTH_SHORT).show();
                    navigateToMain();
                })
                .addOnFailureListener(e -> {
                    setLoadingState(false);
                    Toast.makeText(Login.this, "Google sign-in failed: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    private void sendPasswordReset(String email) {
        FirebaseAuthManager.sendPasswordResetEmail(email)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(Login.this, "Reset email sent!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(Login.this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void setLoadingState(boolean isLoading) {
        getStartedButton.setEnabled(!isLoading);
        getStartedButton.setText(isLoading ? "Logging in..." : "Login");
        googleSignInButton.setEnabled(!isLoading);
        appleSignInButton.setEnabled(!isLoading);
    }

    private void navigateToMain() {
        Intent intent = new Intent(Login.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}