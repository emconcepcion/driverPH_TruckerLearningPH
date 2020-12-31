package com.cav.quizinstructions;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import com.cav.quizinstructions.QuizContract.*;

import com.cav.quizinstructions.DbContract.*;

import java.util.ArrayList;
import java.util.List;

import static com.cav.quizinstructions.DbContract.ScoresTable.DATABASE_NAME;
import static com.cav.quizinstructions.DbContract.ScoresTable.TABLE_NAME_SCORES;

public class QuizDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    public static SQLiteDatabase db;

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

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            db.disableWriteAheadLogging();
        }
    }


    public void saveToLocalDatabase(int user_id, String email, int score, int num_items, String chap,
                                    int num_of_attempt, String date_taken, int sync_status,
                                    SQLiteDatabase database) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ScoresTable.COLUMN_NAME_USER_ID, user_id);
        contentValues.put(ScoresTable.COLUMN_NAME_EMAIL, email);
        contentValues.put(ScoresTable.COLUMN_NAME_SCORE, score);
        contentValues.put(ScoresTable.COLUMN_NAME_NUM_ITEMS, num_items);
        contentValues.put(ScoresTable.COLUMN_NAME_CHAPTER, chap);
        contentValues.put(ScoresTable.COLUMN_NAME_NUM_ATTEMPT, num_of_attempt);
        contentValues.put(ScoresTable.COLUMN_NAME_DATE_TAKEN, date_taken);
        contentValues.put(ScoresTable.SYNC_STATUS, sync_status);
        database.insertWithOnConflict(ScoresTable.TABLE_NAME_SCORES, null, contentValues,
                SQLiteDatabase.CONFLICT_REPLACE);
    }


    public Cursor readFromLocalDatabase(SQLiteDatabase database) {

        //projection are the column names
        String[] projection = {ScoresTable.COLUMN_NAME_USER_ID, ScoresTable.COLUMN_NAME_EMAIL, ScoresTable.COLUMN_NAME_SCORE,
                ScoresTable.COLUMN_NAME_NUM_ITEMS, ScoresTable.COLUMN_NAME_CHAPTER,
                ScoresTable.COLUMN_NAME_NUM_ATTEMPT, ScoresTable.COLUMN_NAME_DATE_TAKEN,
                ScoresTable.SYNC_STATUS};
        String selection = ScoresTable.COLUMN_NAME_USER_ID + " LIKE ?";
        String[] selection_args = {String.valueOf(Dashboard.thisUserId)};
        return (database.query(ScoresTable.TABLE_NAME_SCORES, projection, selection, selection_args,
                null, null, null));
    }

    public Cursor readFromServer(SQLiteDatabase database) {

        //projection are the column names
        String[] projection = {ScoresMySQLTable.COLUMN_NAME_USER_ID_MYSQL, ScoresMySQLTable.COLUMN_NAME_EMAIL_MYSQL,
                ScoresMySQLTable.COLUMN_NAME_SCORE_MYSQL, ScoresMySQLTable.COLUMN_NAME_NUM_ITEMS_MYSQL,
                ScoresMySQLTable.COLUMN_NAME_CHAPTER_MYSQL, ScoresMySQLTable.COLUMN_NAME_NUM_ATTEMPT_MYSQL,
                ScoresMySQLTable.COLUMN_NAME_DATE_TAKEN_MYSQL, ScoresMySQLTable.SYNC_STATUS_MYSQL};
        String selection = ScoresMySQLTable.COLUMN_NAME_USER_ID_MYSQL + " LIKE ?";
        String[] selection_args = {String.valueOf(Dashboard.thisUserId)};
        return (database.query(ScoresMySQLTable.TABLE_NAME_SCORES_MYSQL, projection, selection, selection_args,
                null, null, null));
    }


    public void updateLocalDatabase(int user_id, String email, int score, int num_items, String chap,
                                    int num_of_attempt, String date_taken, int sync_status,
                                    SQLiteDatabase database) {
        //update syncstatus based on the score
        ContentValues contentValues = new ContentValues();
        contentValues.put(ScoresTable.SYNC_STATUS, sync_status);
        //update table based on the score
        String selection = ScoresTable.COLUMN_NAME_SCORE + " LIKE ?";
        String[] selection_args = {String.valueOf(score)};
        database.update(ScoresTable.TABLE_NAME_SCORES, contentValues, selection, selection_args);
    }


    public List<Question> getAllQuestions() {
        List<Question> questionList = new ArrayList<>();
        db = getReadableDatabase();

        String getChapter = "\"" + QuizInstructions.chapter + "\"";

        Cursor c = db.rawQuery("SELECT * FROM " + QuestionsTable.TABLE_NAME +
                " WHERE chapter " + "=" + getChapter, null);

        if (c.moveToFirst()) {
            do {
                Question question = new Question();
                question.setQuestion(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_QUESTION)));
                question.setOption1(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_OPTION1)));
                question.setOption2(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_OPTION2)));
                question.setOption3(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_OPTION3)));
                question.setOption4(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_OPTION4)));
                question.setAnswerNr(c.getInt(c.getColumnIndex(QuestionsTable.COLUMN_ANSWER_NR)));
                question.setChapter(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_CHAPTER)));
                questionList.add(question);
            } while (c.moveToNext());
        }

        c.close();
        return questionList;
    }

    public List<Score> getAllScores() {
        List<Score> scoresList = new ArrayList<>();
        db = getReadableDatabase();

        String user_id = "\"" + Dashboard.user_id + "\"";

        Cursor c = db.rawQuery("SELECT * FROM " + ScoresMySQLTable.TABLE_NAME_SCORES_MYSQL +
                " WHERE user_id " + "=" + user_id, null);

        if (c.moveToFirst()) {
            do {
                Score score = new Score();
                score.setUser_id(c.getInt(c.getColumnIndex(ScoresMySQLTable.COLUMN_NAME_USER_ID_MYSQL)));
                score.setEmail(c.getString(c.getColumnIndex(ScoresMySQLTable.COLUMN_NAME_EMAIL_MYSQL)));
                score.setScore(c.getInt(c.getColumnIndex(ScoresMySQLTable.COLUMN_NAME_SCORE_MYSQL)));
                score.setNum_of_items(c.getInt(c.getColumnIndex(ScoresMySQLTable.COLUMN_NAME_NUM_ITEMS_MYSQL)));
                score.setChapter(c.getString(c.getColumnIndex(ScoresMySQLTable.COLUMN_NAME_CHAPTER_MYSQL)));
                score.setNum_of_attempt(c.getInt(c.getColumnIndex(ScoresMySQLTable.COLUMN_NAME_NUM_ATTEMPT_MYSQL)));
                score.setDate_taken(c.getString(c.getColumnIndex(ScoresMySQLTable.COLUMN_NAME_DATE_TAKEN_MYSQL)));
                score.setSync_status(c.getInt(c.getColumnIndex(ScoresMySQLTable.SYNC_STATUS_MYSQL)));
                scoresList.add(score);
            } while (c.moveToNext());
        }

        c.close();
        return scoresList;
    }

    public Cursor getAttemptFromLocalDatabase(int user_id, String chapter, SQLiteDatabase sqLiteDatabase) {

        //projection are the column names
        String[] attemptCount = {ScoresTable.COLUMN_NAME_USER_ID, ScoresTable.COLUMN_NAME_NUM_ATTEMPT, ScoresTable.COLUMN_NAME_CHAPTER};
        String selection = ScoresTable.COLUMN_NAME_USER_ID + " LIKE ? AND " + ScoresTable.COLUMN_NAME_CHAPTER + " LIKE ?";
        String[] selection_args = {String.valueOf(user_id),chapter};
        Cursor cursor = sqLiteDatabase.query(ScoresTable.TABLE_NAME_SCORES, attemptCount, selection, selection_args,
                null, null, null);
        return cursor;
    }

}

