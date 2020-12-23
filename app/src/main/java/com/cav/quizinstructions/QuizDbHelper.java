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

    ProgressBar progressBar;
    private static final int DATABASE_VERSION = 1;
    private List<Question> questionDb;

    private SQLiteDatabase db;

    public QuizDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;

        final String SQL_CREATE_QUESTIONS_TABLE = "CREATE TABLE " +
                QuestionsTable.TABLE_NAME + " ( " +
                QuestionsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                QuestionsTable.COLUMN_QUESTION + " TEXT, " +
                QuestionsTable.COLUMN_OPTION1 + " TEXT, " +
                QuestionsTable.COLUMN_OPTION2 + " TEXT, " +
                QuestionsTable.COLUMN_OPTION3 + " TEXT, " +
                QuestionsTable.COLUMN_OPTION4 + " TEXT, " +
                QuestionsTable.COLUMN_ANSWER_NR + " INTEGER, " +
                QuestionsTable.COLUMN_CHAPTER + " TEXT" +
                ")";

        final String SQL_CREATE_SCORES_TABLE = "CREATE TABLE " +
                ScoresTable.TABLE_NAME_SCORES + " ( " +
                ScoresTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ScoresTable.COLUMN_NAME_EMAIL + " TEXT, " +
                ScoresTable.COLUMN_NAME_SCORE + " INTEGER," +
                ScoresTable.COLUMN_NAME_NUM_ITEMS + " INTEGER," +
                ScoresTable.COLUMN_NAME_CHAPTER + " TEXT," +
                ScoresTable.COLUMN_NAME_NUM_ATTEMPT + " INTEGER," +
                ScoresTable.COLUMN_NAME_DATE_TAKEN + " TEXT," +
                ScoresTable.SYNC_STATUS + " INTEGER" +
                ")";

        db.execSQL(SQL_CREATE_QUESTIONS_TABLE);
        db.execSQL(SQL_CREATE_SCORES_TABLE);
        fillQuestionsTable();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + QuestionsTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ScoresTable.TABLE_NAME_SCORES);
        onCreate(db);
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


    private void fillQuestionsTable() {
        Question q1 = new Question("First Question", "First", "second", "third", "fourth", 1, "Basic Competencies");
        addQuestion(q1);
        Question q2 = new Question("Second Question", "First", "third", "second", "fourth", 3, "Common Competencies");
        addQuestion(q2);
        Question q3 = new Question("Third Question", "First", "third", "second", "fourth", 2, "Basic Competencies");
        addQuestion(q3);
        Question q4 = new Question("Fourth Question", "First", "second", "third", "fourth", 4, "Common Competencies");
        addQuestion(q4);
        Question q5 = new Question("Fifth Question", "Fifth", "second", "third", "fourth", 1, "Core Competencies");
        addQuestion(q5);
    }

    public void addQuestion(Question question){
        ContentValues cv = new ContentValues();
        cv.put(QuestionsTable.COLUMN_QUESTION, question.getQuestion());
        cv.put(QuestionsTable.COLUMN_OPTION1, question.getOption1());
        cv.put(QuestionsTable.COLUMN_OPTION2, question.getOption2());
        cv.put(QuestionsTable.COLUMN_OPTION3, question.getOption3());
        cv.put(QuestionsTable.COLUMN_OPTION4, question.getOption4());
        cv.put(QuestionsTable.COLUMN_ANSWER_NR, question.getAnswerNr());
        cv.put(QuestionsTable.COLUMN_CHAPTER, question.getChapter());
        db.insert(QuestionsTable.TABLE_NAME, null, cv);
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

    public void getJSON(final String urlWebService) {
        class GetJSON extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.d("s", s +"");
                try {
//                    loadRetrievedQuestions(s);
                    parserQuestionsFromString(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }

            }
        }
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }

    void parserQuestionsFromString(String refjson) {
        String stringjson = refjson;
        try {
            JSONObject jObj = new JSONObject(stringjson);

            JSONArray menuitemArray = jObj.getJSONArray("data");

            for (int i = 0; i < menuitemArray.length(); i++) {

                Log.d("question_text " + i,
                        menuitemArray.getJSONObject(i).getString("question_text")
                                .toString());
                Log.d("option1: " + i, menuitemArray.getJSONObject(i)
                        .getString("option1"));
                Log.d("option2: " + i, menuitemArray.getJSONObject(i)
                        .getString("option2"));
                Log.d("option3: " + i, menuitemArray.getJSONObject(i)
                        .getString("option3"));
                Log.d("option4: " + i, menuitemArray.getJSONObject(i)
                        .getString("option4"));
                Log.d("answer_nr: " + i, menuitemArray.getJSONObject(i)
                        .getString("answer_nr"));
                Log.d("chapter: " + i, menuitemArray.getJSONObject(i)
                        .getString("chapter"));

                String question = menuitemArray.getJSONObject(i).getString("question_text").toString();
                String option1 = menuitemArray.getJSONObject(i).getString("option1").toString();
                String option2 = menuitemArray.getJSONObject(i).getString("option2").toString();
                String option3 = menuitemArray.getJSONObject(i).getString("option3").toString();
                String option4 = menuitemArray.getJSONObject(i).getString("option4").toString();
                String answer_nr = menuitemArray.getJSONObject(i).getString("answer_nr").toString();
                String chapter = menuitemArray.getJSONObject(i).getString("chapter").toString();
                Question q1 = new Question(question, option1, option2, option3, option4, Integer.parseInt(answer_nr), chapter);
                addQuestion(q1);

            }

        } catch (Exception je) {

            Log.d("json error...", je + "");
        }
        Log.d("Inside aysnc task", "inside asynctask...");
    }
}
