package com.example.lab8_v2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "GameScores.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_SCORES = "scores";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_SCORE = "score";

    private static final String CREATE_TABLE = 
        "CREATE TABLE " + TABLE_SCORES + " (" +
        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
        COLUMN_NAME + " TEXT, " +
        COLUMN_SCORE + " INTEGER)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORES);
        onCreate(db);
    }

    public void saveScore(String name, int score) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_SCORE, score);
        db.insert(TABLE_SCORES, null, values);
        db.close();
    }

    public int getBestScore(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SCORES,
            new String[]{COLUMN_SCORE},
            COLUMN_NAME + " = ?",
            new String[]{name},
            null, null,
            COLUMN_SCORE + " DESC",
            "1");

        int bestScore = 0;
        if (cursor.moveToFirst()) {
            bestScore = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return bestScore;
    }

    public List<ScoreEntry> getTopScores(int limit) {
        List<ScoreEntry> scores = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(TABLE_SCORES,
            new String[]{COLUMN_NAME, COLUMN_SCORE},
            null, null, null, null,
            COLUMN_SCORE + " DESC",
            String.valueOf(limit));

        while (cursor.moveToNext()) {
            String name = cursor.getString(0);
            int score = cursor.getInt(1);
            scores.add(new ScoreEntry(name, score));
        }
        
        cursor.close();
        db.close();
        return scores;
    }

    public static class ScoreEntry {
        public final String name;
        public final int score;

        public ScoreEntry(String name, int score) {
            this.name = name;
            this.score = score;
        }
    }
} 