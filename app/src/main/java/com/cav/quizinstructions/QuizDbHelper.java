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
                                    int num_of_attempt, String duration, String date_taken,
                                    int isLocked, int isCompleted, int sync_status,
                                    SQLiteDatabase database) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ScoresTable.COLUMN_NAME_USER_ID, user_id);
        contentValues.put(ScoresTable.COLUMN_NAME_EMAIL, email);
        contentValues.put(ScoresTable.COLUMN_NAME_SCORE, score);
        contentValues.put(ScoresTable.COLUMN_NAME_NUM_ITEMS, num_items);
        contentValues.put(ScoresTable.COLUMN_NAME_CHAPTER, chap);
        contentValues.put(ScoresTable.COLUMN_NAME_NUM_ATTEMPT, num_of_attempt);
        contentValues.put(ScoresTable.COLUMN_NAME_DURATION, duration);
        contentValues.put(ScoresTable.COLUMN_NAME_DATE_TAKEN, date_taken);
        contentValues.put(ScoresTable.COLUMN_NAME_IS_LOCKED, isLocked);
        contentValues.put(ScoresTable.COLUMN_NAME_IS_COMPLETED, isCompleted);
        contentValues.put(ScoresTable.SYNC_STATUS, sync_status);
        database.insertWithOnConflict(ScoresTable.TABLE_NAME_SCORES, null, contentValues,
                SQLiteDatabase.CONFLICT_REPLACE);
    }

//    public void saveFromMysqlToLocalDatabase(int user_id, String email, int score, int num_items, String chap,
//                                    int num_of_attempt, String duration, String date_taken,
//                                    int isLocked, int isCompleted,
//                                    SQLiteDatabase database) {
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(ScoresMySQLTable.COLUMN_NAME_USER_ID_MYSQL, user_id);
//        contentValues.put(ScoresMySQLTable.COLUMN_NAME_EMAIL_MYSQL, email);
//        contentValues.put(ScoresMySQLTable.COLUMN_NAME_SCORE_MYSQL, score);
//        contentValues.put(ScoresMySQLTable.COLUMN_NAME_NUM_ITEMS_MYSQL, num_items);
//        contentValues.put(ScoresMySQLTable.COLUMN_NAME_CHAPTER_MYSQL, chap);
//        contentValues.put(ScoresMySQLTable.COLUMN_NAME_NUM_ATTEMPT_MYSQL, num_of_attempt);
//        contentValues.put(ScoresMySQLTable.COLUMN_NAME_DURATION_MYSQL, duration);
//        contentValues.put(ScoresMySQLTable.COLUMN_NAME_DATE_TAKEN_MYSQL, date_taken);
//        contentValues.put(ScoresMySQLTable.COLUMN_NAME_IS_LOCKED_MYSQL, isLocked);
//        contentValues.put(ScoresMySQLTable.COLUMN_NAME_IS_COMPLETED_MYSQL, isCompleted);
//        database.insertWithOnConflict(ScoresMySQLTable.TABLE_NAME_SCORES_MYSQL, null, contentValues,
//                SQLiteDatabase.CONFLICT_REPLACE);
//    }


    public Cursor readFromLocalDatabase(SQLiteDatabase database) {

        //projection are the column names
        String[] projection = {ScoresTable.COLUMN_NAME_USER_ID, ScoresTable.COLUMN_NAME_EMAIL, ScoresTable.COLUMN_NAME_SCORE,
                ScoresTable.COLUMN_NAME_NUM_ITEMS, ScoresTable.COLUMN_NAME_CHAPTER,
                ScoresTable.COLUMN_NAME_NUM_ATTEMPT, ScoresTable.COLUMN_NAME_DURATION,
                ScoresTable.COLUMN_NAME_DATE_TAKEN, ScoresTable.COLUMN_NAME_IS_LOCKED,
                ScoresTable.COLUMN_NAME_IS_COMPLETED, ScoresTable.SYNC_STATUS};
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
                ScoresMySQLTable.COLUMN_NAME_DURATION_MYSQL,
                ScoresMySQLTable.COLUMN_NAME_DATE_TAKEN_MYSQL, ScoresMySQLTable.COLUMN_NAME_IS_LOCKED_MYSQL,
                ScoresMySQLTable.COLUMN_NAME_IS_COMPLETED_MYSQL};
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

    public void updateMySQLLocalDatabase(int user_id, String chap, int unlockThis, int passed,
                                    SQLiteDatabase database) {
        //update syncstatus based on the score
        ContentValues contentValues = new ContentValues();
        contentValues.put(ScoresMySQLTable.COLUMN_NAME_IS_LOCKED_MYSQL, unlockThis);
        contentValues.put(ScoresMySQLTable.COLUMN_NAME_IS_COMPLETED_MYSQL, passed);
        //update table based on the score
        String selection = ScoresMySQLTable.COLUMN_NAME_CHAPTER_MYSQL + " LIKE ? AND " +
                            ScoresMySQLTable.COLUMN_NAME_USER_ID_MYSQL + " LIKE ?";
        String[] selection_args = {Dashboard.user_id};
        database.update(ScoresMySQLTable.TABLE_NAME_SCORES_MYSQL, contentValues, selection, selection_args);
    }

    public void updateMyUnlockedModule(int user_id, String chap, int unlockThis, int passed,
                                         SQLiteDatabase database) {
        //update syncstatus based on the score
        ContentValues contentValues = new ContentValues();
        contentValues.put(ScoresMySQLTable.COLUMN_NAME_IS_LOCKED_MYSQL, unlockThis);
        contentValues.put(ScoresMySQLTable.COLUMN_NAME_IS_COMPLETED_MYSQL, passed);
        //update table based on the score
        String selection = ScoresMySQLTable.COLUMN_NAME_CHAPTER_MYSQL + " LIKE ? AND " +
                ScoresMySQLTable.COLUMN_NAME_USER_ID_MYSQL + " LIKE ?";
        String[] selection_args = {Dashboard.user_id};
        database.update(ScoresMySQLTable.TABLE_NAME_SCORES_MYSQL, contentValues, selection, selection_args);
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

    public List<MyScoresServer> getAllScores() {
    //    List<Score> scoresList = new ArrayList<>();
        List<MyScoresServer> myScoresServerList = new ArrayList<>();
        db = getReadableDatabase();

        String user_id = "\"" + Dashboard.user_id + "\"";

        Cursor c = db.rawQuery("SELECT * FROM " + ScoresMySQLTable.TABLE_NAME_SCORES_MYSQL +
                " WHERE user_id " + "=" + user_id, null);

        if (c.moveToFirst()) {
            do {
                MyScoresServer myScoresServer = new MyScoresServer();
                myScoresServer.setUser_id(c.getInt(c.getColumnIndex(ScoresMySQLTable.COLUMN_NAME_USER_ID_MYSQL)));
                myScoresServer.setEmail(c.getString(c.getColumnIndex(ScoresMySQLTable.COLUMN_NAME_EMAIL_MYSQL)));
                myScoresServer.setScore(c.getInt(c.getColumnIndex(ScoresMySQLTable.COLUMN_NAME_SCORE_MYSQL)));
                myScoresServer.setNum_of_items(c.getInt(c.getColumnIndex(ScoresMySQLTable.COLUMN_NAME_NUM_ITEMS_MYSQL)));
                myScoresServer.setChapter(c.getString(c.getColumnIndex(ScoresMySQLTable.COLUMN_NAME_CHAPTER_MYSQL)));
                myScoresServer.setNum_of_attempt(c.getInt(c.getColumnIndex(ScoresMySQLTable.COLUMN_NAME_NUM_ATTEMPT_MYSQL)));
                myScoresServer.setDate_taken(c.getString(c.getColumnIndex(ScoresMySQLTable.COLUMN_NAME_DATE_TAKEN_MYSQL)));
                myScoresServer.setIsLocked(c.getInt(c.getColumnIndex(ScoresMySQLTable.COLUMN_NAME_IS_LOCKED_MYSQL)));
                myScoresServer.setIsCompleted(c.getInt(c.getColumnIndex(ScoresMySQLTable.COLUMN_NAME_IS_COMPLETED_MYSQL)));
                myScoresServerList.add(myScoresServer);
            } while (c.moveToNext());
        }

        c.close();
        return myScoresServerList;
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

    public Cursor getLockedUnlockedFromLocalDatabase(int user_id, String chapter, int isLocked, int isCompleted, SQLiteDatabase sqLiteDatabase) {

        //projection are the column names
        String[] checkLockUnlock = {ScoresMySQLTable.COLUMN_NAME_USER_ID_MYSQL,
                ScoresMySQLTable.COLUMN_NAME_CHAPTER_MYSQL,
                ScoresMySQLTable.COLUMN_NAME_IS_LOCKED_MYSQL,
                ScoresMySQLTable.COLUMN_NAME_IS_COMPLETED_MYSQL};
        String selection = ScoresMySQLTable.COLUMN_NAME_USER_ID_MYSQL + " LIKE ? AND " + ScoresMySQLTable.COLUMN_NAME_CHAPTER_MYSQL + " LIKE ?";
        String[] selection_args = {String.valueOf(user_id), chapter};
        Cursor cursor = sqLiteDatabase.query(ScoresMySQLTable.TABLE_NAME_SCORES_MYSQL, checkLockUnlock, selection, selection_args,
                null, null, null);
        return cursor;
    }

}

