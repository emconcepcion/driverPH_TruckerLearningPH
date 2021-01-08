package com.cav.quizinstructions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.anton46.stepsview.StepsView;
import com.cav.quizinstructions.databinding.ActivityLessonBinding;
import com.muddzdev.styleabletoast.StyleableToast;

import static com.cav.quizinstructions.Constant.SP_LESSONID;


public class Lesson extends AppCompatActivity {

    ActivityLessonBinding binding;
    String[] descriptionData = {"One", "Two", "Three", "Four", "Five", "Six"};
    int arrSize;
    public static TextView arraySize, progress_Module, progress_LessonTitle;
    int current_state = 0;
    Bundle data = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLessonBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        arraySize = findViewById(R.id.arr_size);
        arrSize = descriptionData.length;
        arraySize.setText(String.valueOf(arrSize));
        progress_Module = findViewById(R.id.progress_Module);
        progress_LessonTitle = findViewById(R.id.lessTitle);

        SharedPreferences sharedPreferences = getSharedPreferences(SP_LESSONID, MODE_PRIVATE);
        String progMod = sharedPreferences.getString("moduleName", "");
        String progLess = sharedPreferences.getString("lessonTitle", "");

        String null_lessonId = "No active modules yet.";
        if (progMod.equals("null") || progLess.equals("null")){
            progress_Module.setText(null_lessonId);
        }else{
            progress_Module.setText(progMod);
            progress_LessonTitle.setText(progLess);
        }

//        String currentModule = Lessons_Basic_Content.module;
//        if (!currentModule.equals("null")){
//            progress_Module.setText("Active Module");
//        }else{
//            progress_Module.setText(currentModule);
//        }

        binding.spb.setLabels(descriptionData)
                .setBarColorIndicator(Color.BLACK)
                .setProgressColorIndicator(getResources().getColor(R.color.colorBlueLight))
                .setLabelColorIndicator(getResources().getColor(R.color.colorRed))
                .setCompletedPosition(0)
                .drawView();

        binding.spb.setCompletedPosition(current_state);

        binding.btnNextLesson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(current_state<(descriptionData.length-1)) {
                    current_state = current_state + 1;
                    binding.spb.setCompletedPosition(current_state).drawView();
                    if (data != null) {
                        current_state = data.getInt("progress", current_state);

                    } else {
                        Intent i = new Intent(Lesson.this, Lesson.class);
                        data = getIntent().getExtras();
                        binding.spb.setCompletedPosition(current_state);
                        i.putExtra("progress", current_state);
                        finish();
                        startActivity(getIntent());
                    }
//                data.getInt("progress", current_state);

                    StyleableToast.makeText(Lesson.this, "Lesson number: " + (current_state+1), Toast.LENGTH_SHORT, R.style.toastStyle).show();
                }else if(binding.btnNextLesson.isPressed()){
                    startActivity(new Intent(Lesson.this, VoiceResponse.class));
                }

            }
        });

        binding.btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(current_state>0) {
                    current_state = current_state - 1;
                    binding.spb.setCompletedPosition(current_state).drawView();

                    if (data != null) {
                        current_state = data.getInt("progress", current_state);
                    } else {
                        Intent i = new Intent(Lesson.this, Lesson.class);
                        data = getIntent().getExtras();
                        binding.spb.setCompletedPosition(current_state);
                        i.putExtra("progress", current_state);
                        binding.spb.setCompletedPosition(current_state);
                        finish();
                        startActivity(getIntent());
                    }
                    StyleableToast.makeText(Lesson.this, "Lesson number: " + (current_state-1), Toast.LENGTH_SHORT, R.style.toastStyle).show();
                }
                //  Log.d("current state = ", current_state + "");
            }
        });


    }
}