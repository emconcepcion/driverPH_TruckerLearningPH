package com.cav.quizinstructions;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cav.quizinstructions.QuizContract.*;

import com.cav.quizinstructions.DbContract.*;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.cav.quizinstructions.DbContract.ScoresTable.DATABASE_NAME;

public class QuizDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private SQLiteDatabase db;

    public QuizDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void saveToLocalDatabase(String email, int score, int num_items, String chap,
                                    int num_of_attempt, String date_taken, int sync_status,
                                    SQLiteDatabase database){
        ContentValues contentValues = new ContentValues();
        contentValues.put(ScoresTable.COLUMN_NAME_EMAIL, email);
        contentValues.put(ScoresTable.COLUMN_NAME_SCORE, score);
        contentValues.put(ScoresTable.COLUMN_NAME_NUM_ITEMS, num_items);
        contentValues.put(ScoresTable.COLUMN_NAME_CHAPTER, chap);
        contentValues.put(ScoresTable.COLUMN_NAME_NUM_ATTEMPT, num_of_attempt);
        contentValues.put(ScoresTable.COLUMN_NAME_DATE_TAKEN, date_taken);
        contentValues.put(ScoresTable.SYNC_STATUS, sync_status);
        database.insert(ScoresTable.TABLE_NAME_SCORES, null, contentValues);
    }

    public Cursor readFromLocalDatabase(SQLiteDatabase database){

        //projection are the column names
        String[] projection = {ScoresTable.COLUMN_NAME_EMAIL, ScoresTable.COLUMN_NAME_SCORE,
                ScoresTable.COLUMN_NAME_NUM_ITEMS, ScoresTable.COLUMN_NAME_CHAPTER,
                ScoresTable.COLUMN_NAME_NUM_ATTEMPT, ScoresTable.COLUMN_NAME_DATE_TAKEN,
                ScoresTable.SYNC_STATUS};
        return (database.query(ScoresTable.TABLE_NAME_SCORES, projection,null, null, null, null, null));
    }

    public void updateLocalDatabase(String email, int score, int num_items, String chap,
                                    int num_of_attempt, String date_taken, int sync_status,
                                    SQLiteDatabase database){
        //update syncstatus based on the score
        ContentValues contentValues = new ContentValues();
        contentValues.put(ScoresTable.SYNC_STATUS, sync_status);
        //update table based on the score
        String selection = ScoresTable.COLUMN_NAME_SCORE+" LIKE ?";
        String[] selection_args = {String.valueOf(score)};
        database.update(ScoresTable.TABLE_NAME_SCORES, contentValues, selection, selection_args);
    }

    public List<Question> getAllQuestions(){
        List<Question> questionList = new ArrayList<>();
        db = getReadableDatabase();
        String getChapter = "\"" + QuizInstructions.chapter + "\"";

        Cursor c = db.rawQuery("SELECT * FROM " + QuestionsTable.TABLE_NAME +
                    " WHERE chapter " + "=" + getChapter, null);

        if(c.moveToFirst()){
            do{
                Question question = new Question();
                question.setQuestion(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_QUESTION)));
                question.setOption1(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_OPTION1)));
                question.setOption2(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_OPTION2)));
                question.setOption3(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_OPTION3)));
                question.setOption4(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_OPTION4)));
                question.setAnswerNr(c.getInt(c.getColumnIndex(QuestionsTable.COLUMN_ANSWER_NR)));
                question.setChapter(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_CHAPTER)));
                questionList.add(question);
            }while(c.moveToNext());
        }

        c.close();
        return questionList;
    }

}
