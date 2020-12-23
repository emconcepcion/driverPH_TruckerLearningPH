package com.cav.quizinstructions;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.navigation.NavigationView;
import com.muddzdev.styleabletoast.StyleableToast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class QuizActivity extends AppCompatActivity {

    private TextView textViewChapter;
    private TextView textViewQuestion;
    private TextView textViewScore;
    private TextView textViewEmail;
    private TextView textViewQuestionCount;
    private TextView textViewCountdown;
    private TextView attempt;
    private RadioGroup rbGroup;
    private RadioButton rb1, rb2, rb3, rb4;
    Button btn_next;
    ImageButton btn_sound;

    SharedPreferences sp;
    public int num_of_attempt;

    ProgressBar progressBar;

    ArrayList<String> askedQuestions = new ArrayList<>();

    private ColorStateList textColorDefaultRb;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;

    private List<Question> questionList;
    private List<Question> questionDb;
    private int questionCounter;
    private int questionCountTotal;
    private Question currentQuestion;

    TextView answer_nr;
    private int score;
    private boolean answered;
    CardView cardViewScore;
    private boolean backPressed;
    String email;
    private boolean endedAttempt;
    int correct_answer = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        cardViewScore = findViewById(R.id.cardView_viewScore);
        textViewQuestion = findViewById(R.id.txt_question);
        textViewQuestionCount = findViewById(R.id.txt_question_counter);
        textViewCountdown = findViewById(R.id.textview_timer);
        rbGroup = findViewById(R.id.radio_grp_options);
        rb1 = findViewById(R.id.radio_btn_option1);
        rb2 = findViewById(R.id.radio_btn_option2);
        rb3 = findViewById(R.id.radio_btn_option3);
        rb4 = findViewById(R.id.radio_btn_option4);
        textViewChapter = findViewById(R.id.textview_chapter);
        btn_next = findViewById(R.id.btn_next);
        btn_sound = findViewById(R.id.btn_sound);
        textViewScore = findViewById(R.id.textview_score);
        textViewEmail = findViewById(R.id.textview_email);
        attempt = findViewById(R.id.textview_attempt);

        textColorDefaultRb = rb1.getTextColors();

//        Intent intent = getIntent();
//        Bundle bundle = intent.getExtras();
//        String chapter = bundle.getString("chapter");
//        textViewChapter.setText(chapter);

        textViewScore.setVisibility(View.GONE);
        textViewEmail.setVisibility(View.GONE);
        QuizDbHelper dbHelper = new QuizDbHelper(this);
        questionList = dbHelper.getAllQuestions();
        questionCountTotal = (questionList.size() - 2);
        FYAlgoShuffle(questionList);

        SharedPreferences sp = getApplicationContext().getSharedPreferences("mySavedAttempt", Context.MODE_PRIVATE);
        String myEmail = sp.getString("email", "");
        textViewEmail.setText(myEmail);

//        Intent intent = getIntent();
//        email = intent.getExtras().getString("email");
//        textViewEmail.setText(email);

        showNextQuestion();
        getAttempt();

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!answered) {
                    if (rb1.isChecked() || rb2.isChecked() || rb3.isChecked() || rb4.isChecked()) {
                        checkAnswer();
                    } else {
                        StyleableToast.makeText(getApplicationContext(), QuizActivity.this.getString(R.string.please_select_an_answer),
                                Toast.LENGTH_LONG, R.style.toastStyle).show();
                    }
                } else {
                    showNextQuestion();
                }
            }
        });
    }

    public static<T> void FYAlgoShuffle(List<T> list){
        Random random = new Random();
        for (int i = list.size() - 1; i >= 1; i--)
        {
            int j = random.nextInt(i + 1);

            T obj = list.get(i);
            list.set(i, list.get(j));
            list.set(j, obj);
        }
    }

    private void showNextQuestion() {

        rb1.setTextColor(textColorDefaultRb);
        rb2.setTextColor(textColorDefaultRb);
        rb3.setTextColor(textColorDefaultRb);
        rb4.setTextColor(textColorDefaultRb);

        rbSetEnabledTrue();

        if (questionCounter < questionCountTotal) {
            currentQuestion = questionList.get(questionCounter);

            textViewQuestion.setText(currentQuestion.getQuestion());
            textViewChapter.setText("Quiz: " + currentQuestion.getChapter());
            rb1.setText(currentQuestion.getOption1());
            rb2.setText(currentQuestion.getOption2());
            rb3.setText(currentQuestion.getOption3());
            rb4.setText(currentQuestion.getOption4());

            Log.d("answer", currentQuestion.getAnswerNr()+"");

            questionCounter++;
            textViewQuestionCount.setText("Question: " + questionCounter + "/" + questionCountTotal);
            answered = false;
            btn_next.setText("Confirm");
        } else {
            finishQuiz();
        }
    }

    private void timer(){
        countDownTimer = new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                int minutes = (int) (millisUntilFinished / 1000) / 60;
                int seconds = (int) (millisUntilFinished / 1000) % 60;

                String timeRemaining = String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds);
                textViewCountdown.setText(getString(R.string.time_remaining) +": " + timeRemaining);
            }

            public void onFinish() {
                textViewCountdown.setText(getString(R.string.time_is_up));
                Toast toast = Toast.makeText(QuizActivity.this, QuizActivity.this.getString(R.string.time_is_up), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.FILL, 0, 400);
                toast.show();
                finishQuiz();
            }
        }.start();
    }


    public void checkAnswer(){
        answered = true;

        RadioButton rbSelected = findViewById(rbGroup.getCheckedRadioButtonId());
        int answerNr = rbGroup.indexOfChild(rbSelected) + 1;

        Log.d("answer_nr", answerNr+"");
        Log.d("correct_answer:", correct_answer +"");
        if (answerNr == currentQuestion.getAnswerNr()) {
            switch (currentQuestion.getAnswerNr()){
                case 1:
                    currentQuestion.getOption1();
                    askedQuestions.add(currentQuestion.getQuestion()  +"\n\nAnswer: " + currentQuestion.getOption1() + "\n");
                    break;
                case 2:
                    currentQuestion.getOption2();
                    askedQuestions.add(currentQuestion.getQuestion() + "\n\n" +"Answer: " + currentQuestion.getOption2());
                    break;
                case 3:
                    currentQuestion.getOption3();
                    askedQuestions.add(currentQuestion.getQuestion() + "\n\n" +"Answer: " + currentQuestion.getOption3());
                    break;
                case 4:
                    currentQuestion.getOption4();
                    askedQuestions.add(currentQuestion.getQuestion() + "\n\n" +"Answer: " + currentQuestion.getOption4());
                    break;
            }
            score++;
            textViewScore.setText("Score: " + score);

        }
        else{
            String ansNotAvailable = "Correct answer is hidden.";
            askedQuestions.add(currentQuestion.getQuestion() + "\n" + ansNotAvailable);
        }

        showSolution();
    }

    public void showSolution() {
        RadioButton rbSelected = findViewById(rbGroup.getCheckedRadioButtonId());
        int answerNr = rbGroup.indexOfChild(rbSelected) + 1;
        rbSetEnabledFalse();

        if(answerNr != currentQuestion.getAnswerNr()) {
            textViewQuestion.setText("Wrong Answer.");
            rb1.setTextColor(Color.RED);
            rb2.setTextColor(Color.RED);
            rb3.setTextColor(Color.RED);
            rb4.setTextColor(Color.RED);
            rbGroup.clearCheck();
        }else {
            switch (currentQuestion.getAnswerNr()) {
                case 1:
                    rb1.setTextColor(Color.GREEN);
                    textViewQuestion.setText("Correct!");
                    break;
                case 2:
                    rb2.setTextColor(Color.GREEN);
                    textViewQuestion.setText("Correct!");
                    break;
                case 3:
                    rb3.setTextColor(Color.GREEN);
                    textViewQuestion.setText("Correct!");
                    break;
                case 4:
                    rb4.setTextColor(Color.GREEN);
                    textViewQuestion.setText("Correct!");
                    break;
            }
            rbGroup.clearCheck();
        }

            if (questionCounter < questionCountTotal) {
                btn_next.setText("Next");
            } else {
                btn_next.setText("Submit Quiz");
            }

    }

    public void rbSetEnabledFalse(){
        rb1.setEnabled(false);
        rb2.setEnabled(false);
        rb3.setEnabled(false);
        rb4.setEnabled(false);
    }
    public void rbSetEnabledTrue(){
        rb1.setEnabled(true);
        rb2.setEnabled(true);
        rb3.setEnabled(true);
        rb4.setEnabled(true);
    }

    public void toResults(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDate =  new SimpleDateFormat("EEEE MMMM dd, yyyy");
        String currentDate = simpleDate.format(calendar.getTime());

        questionCountTotal = questionList.size() - 2;

        int newAttempt = Integer.parseInt(attempt.getText().toString());
        String myEmail = textViewEmail.getText().toString();

        Intent i = new Intent(QuizActivity.this, QuizStatusList.class);
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("askedQuestions", askedQuestions);
        bundle.putInt("score", score);
        bundle.putInt("items", questionCountTotal);
        bundle.putString("chapter", currentQuestion.getChapter());
        bundle.putInt("attempt", newAttempt);
        bundle.putString("myEmail", myEmail);
        i.putExtras(bundle);
        i.putStringArrayListExtra("askedQuestions", askedQuestions);
//        i.putExtra("email", email);
        i.putExtra("date_taken", currentDate);
        startActivity(i);

    }

    private void finishQuiz() {
        showScore();
    }

    private void getAttempt(){

        SharedPreferences sp = getApplicationContext().getSharedPreferences("mySavedAttempt", Context.MODE_PRIVATE);
        int incAttempt = sp.getInt("attempt", 1);
        attempt.setText(String.valueOf(incAttempt));


//        textViewEmail.setText(myEmail);
//
//        Intent intent = getIntent();
//        int incAttempt = intent.getIntExtra("toQuizActAttempt", 1);
//        attempt.setText(String.valueOf(incAttempt));
    }




    @Override
    public void onBackPressed() {
        Dialog exit_quiz_popup = new Dialog(this);
        ImageView close_exit_popup;
        Button btn_exit_quiz_yes, btn_exit_quiz_no;
        exit_quiz_popup.setContentView(R.layout.exit_quiz_popup);
        close_exit_popup = exit_quiz_popup.findViewById(R.id.close_exit_quiz);
        btn_exit_quiz_yes = (Button) exit_quiz_popup.findViewById(R.id.btn_exit_quiz_yes);
        btn_exit_quiz_no = (Button) exit_quiz_popup.findViewById(R.id.btn_exit_quiz_no);

        close_exit_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exit_quiz_popup.dismiss();
            }
        });

        exit_quiz_popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        exit_quiz_popup.show();

        btn_exit_quiz_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleDate =  new SimpleDateFormat("EEEE MMMM dd, yyyy");
                String currentDate = simpleDate.format(calendar.getTime());

                questionCountTotal = questionList.size() - 2;

                int newAttempt = Integer.parseInt(attempt.getText().toString());

                Intent i = new Intent(QuizActivity.this, QuizStatusList.class);
                Bundle bundle = new Bundle();
                bundle.putInt("score", 0);
                bundle.putInt("items", questionCountTotal);
                bundle.putString("chapter", ("Unfinished Attempt: " + currentQuestion.getChapter()));
                bundle.putInt("attempt", newAttempt);
                i.putExtras(bundle);
                i.putExtra("email", email);
                i.putExtra("date_taken", currentDate);
                startActivity(i);
                finish();
                endedAttempt = true;
            }
        });

        btn_exit_quiz_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exit_quiz_popup.dismiss();
            }
        });
    }

    private boolean paused = true;

    public void buttonPressed(View view) {

        ImageButton button = (ImageButton) findViewById(R.id.btn_sound);
        int icon;

        if (paused) {
            paused = false;
            icon = R.drawable.ic_sound_on;
        } else{
            paused = true;
            icon = R.drawable.ic_sound_off;
        }

        button.setImageDrawable(
                ContextCompat.getDrawable(getApplicationContext(), icon));

    }

    public void showScore(){
        Dialog show_score = new Dialog(this);
        Button btn_view_result;
        show_score.setContentView(R.layout.show_score);
        ImageView result_icon, fail_icon, close_exit_popup;
        close_exit_popup = show_score.findViewById(R.id.close_imgview);
        btn_view_result = (Button)show_score.findViewById(R.id.view_result);
        TextView pass_fail, textview_show_score, textview_show_items;
        pass_fail = show_score.findViewById(R.id.passed_failed);
        result_icon = show_score.findViewById(R.id.pass_fail_icon);
        textview_show_score = show_score.findViewById(R.id.show_my_score);
        textview_show_items = show_score.findViewById(R.id.show_my_item);
        cardViewScore = findViewById(R.id.cardView_viewScore);

        textview_show_score.setText(String.valueOf(score));
        textview_show_items.setText(String.valueOf(questionCountTotal));

        int myScore = Integer.parseInt(textview_show_score.getText().toString());
        int myItems = Integer.parseInt(textview_show_items.getText().toString());


        if(myScore > (myItems * 0.8)){
            pass_fail.setText("Like a Boss!");
            result_icon.setImageResource(R.drawable.ic_cheers);

        }else if(myScore < (myItems * 0.8)){
            pass_fail.setText("Aww, snap!");
            result_icon.setImageResource(R.drawable.ic_sad);
        }

        show_score.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        show_score.show();

        btn_view_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toResults();
                finish();
            }
        });

        close_exit_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toResults();
                finish();
            }
        });
    }
}