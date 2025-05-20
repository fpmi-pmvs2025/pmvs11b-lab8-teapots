package com.example.lab8_v2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class GameOverActivity extends AppCompatActivity {
    private static final int TOP_SCORES_COUNT = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        String playerName = getIntent().getStringExtra("PLAYER_NAME");
        int score = getIntent().getIntExtra("SCORE", 0);

        TextView resultTextView = findViewById(R.id.resultTextView);
        resultTextView.setText(String.format("Игра окончена!\n%s, ваш счет: %d из 10", 
            playerName, score));

        LinearLayout scoresContainer = findViewById(R.id.scoresContainer);
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        List<DatabaseHelper.ScoreEntry> topScores = dbHelper.getTopScores(TOP_SCORES_COUNT);

        for (int i = 0; i < topScores.size(); i++) {
            DatabaseHelper.ScoreEntry entry = topScores.get(i);
            TextView scoreView = new TextView(this);
            scoreView.setText(String.format("%d. %s: %d", 
                i + 1, entry.name, entry.score));
            scoreView.setTextSize(16);
            scoreView.setPadding(0, 8, 0, 8);
            scoresContainer.addView(scoreView);
        }

        Button newGameButton = findViewById(R.id.newGameButton);
        newGameButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, NameInputActivity.class);
            startActivity(intent);
            finish();
        });
    }
} 