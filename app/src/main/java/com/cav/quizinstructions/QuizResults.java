package com.cav.quizinstructions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cav.quizinstructions.QuizActivity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class QuizResults extends AppCompatActivity {

    ListView listView;
    TextView score_result, answerKey;
    Button btn_unlock_next_module;

    private Dialog show_score;
    Button btn_view_result;
    ImageView pass_icon, fail_icon;
    ImageView close_exit_popup;
    TextView pass_fail, textview_show_score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_results);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        getSupportActionBar().setTitle("Quiz Results");

        btn_view_result = findViewById(R.id.btn_retake);
        btn_view_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //   toQuizList();
                startActivity(new Intent(QuizResults.this, QuizStatusList.class));
            }
        });

        score_result = findViewById(R.id.txt_score_result);
        listView = findViewById(R.id.list_view);

        showResult();

    }

    public void showResult(){
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList = (ArrayList<String>) getIntent().getSerializableExtra("askedQuestions");
//        int correctAns = getIntent().getExtras().getInt("correctAns");
//        answerKey.setText(correctAns);
        int txt_score_result = getIntent().getExtras().getInt("score");

        score_result.setText(txt_score_result + "/" + arrayList.size());

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);
    }

    //        score_list = txt_score_result;
//        scoreList = new ArrayList<>();
//        scoreList.addAll(arrayList);
////        scoreList.add("" + txt_score_result);



}

