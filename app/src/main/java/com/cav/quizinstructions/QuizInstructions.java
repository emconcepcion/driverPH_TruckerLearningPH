package com.cav.quizinstructions;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class QuizInstructions extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_instructions);

        Button buttonStartQuiz = findViewById(R.id.btn_start_quiz);
        buttonStartQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startQuiz();
            }
        });

    }

    private void startQuiz() {
        Intent intent = new Intent(QuizInstructions.this, QuizActivity.class);
        startActivity(intent);
    }
}