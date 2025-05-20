package com.example.lab8_v2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class NameInputActivity extends AppCompatActivity {
    private EditText nameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_input);

        nameEditText = findViewById(R.id.nameEditText);
        Button startButton = findViewById(R.id.startButton);

        startButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Пожалуйста, введите имя", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("PLAYER_NAME", name);
            startActivity(intent);
            finish();
        });
    }
} 