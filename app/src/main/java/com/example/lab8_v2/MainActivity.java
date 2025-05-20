package com.example.lab8_v2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private ImageView flagImageView;
    private Button[] answerButtons;
    private TextView scoreTextView;
    private List<Country> countries;
    private Country currentCountry;
    private Random random;
    private int score = 0;
    private int questionsAnswered = 0;
    private static final int MAX_QUESTIONS = 10;
    private String playerName;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playerName = getIntent().getStringExtra("PLAYER_NAME");
        if (playerName == null) {
            finish();
            return;
        }

        dbHelper = new DatabaseHelper(this);
        int bestScore = dbHelper.getBestScore(playerName);

        flagImageView = findViewById(R.id.flagImageView);
        scoreTextView = findViewById(R.id.scoreTextView);
        answerButtons = new Button[]{
            findViewById(R.id.answerButton1),
            findViewById(R.id.answerButton2),
            findViewById(R.id.answerButton3),
            findViewById(R.id.answerButton4)
        };
        
        random = new Random();
        countries = new ArrayList<>();
        score = 0;
        questionsAnswered = 0;
        updateScore(bestScore);

        // Инициализация кнопок
        for (Button button : answerButtons) {
            button.setText("Загрузка...");
            button.setEnabled(false);
        }

        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://restcountries.com/v3.1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

        CountryApi api = retrofit.create(CountryApi.class);
        api.getCountries().enqueue(new Callback<List<Country>>() {
            @Override
            public void onResponse(Call<List<Country>> call, Response<List<Country>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    countries = response.body();
                    if (!countries.isEmpty()) {
                        startNewRound();
                    } else {
                        showError("Нет данных о странах");
                    }
                } else {
                    showError("Ошибка загрузки данных: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Country>> call, Throwable t) {
                showError("Ошибка загрузки данных: " + t.getMessage());
            }
        });

        for (Button button : answerButtons) {
            button.setOnClickListener(v -> checkAnswer(button.getText().toString()));
        }
    }

    private void updateScore(int bestScore) {
        scoreTextView.setText(String.format("Счет: %d (Лучший: %d)", score, bestScore));
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        for (Button button : answerButtons) {
            button.setText("Ошибка");
            button.setEnabled(false);
        }
    }

    private void startNewRound() {
        if (countries.isEmpty()) {
            showError("Нет данных о странах");
            return;
        }

        if (questionsAnswered >= MAX_QUESTIONS) {
            endGame();
            return;
        }

        currentCountry = countries.get(random.nextInt(countries.size()));
        String flagUrl = currentCountry.getFlag();
        
        if (flagUrl.isEmpty()) {
            showError("Ошибка загрузки флага");
            return;
        }

        Glide.with(this)
            .load(flagUrl)
            .error(R.drawable.ic_launcher_background)
            .into(flagImageView);

        List<String> options = new ArrayList<>();
        options.add(currentCountry.getName());
        
        while (options.size() < 4) {
            String randomCountry = countries.get(random.nextInt(countries.size())).getName();
            if (!options.contains(randomCountry) && !randomCountry.isEmpty()) {
                options.add(randomCountry);
            }
        }

        for (int i = 0; i < 4; i++) {
            int randomIndex = random.nextInt(options.size());
            String countryName = options.remove(randomIndex);
            answerButtons[i].setText(countryName);
            answerButtons[i].setEnabled(true);
        }
    }

    private void checkAnswer(String selectedAnswer) {
        questionsAnswered++;
        
        if (selectedAnswer.equals(currentCountry.getName())) {
            score += 1;
            updateScore(dbHelper.getBestScore(playerName));
            Toast.makeText(this, "Правильно! +1 балл", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Неправильно! Правильный ответ: " + currentCountry.getName(), 
                Toast.LENGTH_SHORT).show();
        }

        if (questionsAnswered >= MAX_QUESTIONS) {
            endGame();
        } else {
            startNewRound();
        }
    }

    private void endGame() {
        dbHelper.saveScore(playerName, score);
        Intent intent = new Intent(this, GameOverActivity.class);
        intent.putExtra("PLAYER_NAME", playerName);
        intent.putExtra("SCORE", score);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}