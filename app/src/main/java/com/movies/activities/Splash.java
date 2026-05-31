package com.movies.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.movies.R;
import com.movies.utils.FirebaseAuthManager;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        animateText("NovaFlix");

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent;
            if (FirebaseAuthManager.isSignedIn()) {
                intent = new Intent(Splash.this, MainActivity.class);
            } else {
                intent = new Intent(Splash.this, Login.class);
            }
            startActivity(intent);
            finish();
        }, 2500); // Increased delay slightly to allow animation to finish
    }

    private void animateText(String fullText) {
        TextView textView = findViewById(R.id.appNameTextView);
        if (textView == null) return;
        
        textView.setText(""); // Start with empty
        long delay = 150; // ms between letters
        
        for (int i = 0; i < fullText.length(); i++) {
            final int index = i;
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                textView.append(String.valueOf(fullText.charAt(index)));
            }, delay * (i + 1));
        }
    }
}
