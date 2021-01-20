package com.cav.quizinstructions;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;


public class Database {
	public static final String DATABASE_NAME= DbContract.ScoresTable.DATABASE_NAME;
	public static final String TABLE_NAME= QuizContract.QuestionsTable.TABLE_NAME;
	public static final int DATABSE_VERSION=1;

	//to create a table

	final String SQL_CREATE_QUESTIONS_TABLE = "CREATE TABLE " +
			QuizContract.QuestionsTable.TABLE_NAME + "(" +
			QuizContract.QuestionsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, " +
			QuizContract.QuestionsTable.COLUMN_QUESTION + " TEXT UNIQUE, " +
			QuizContract.QuestionsTable.COLUMN_OPTION1 + " TEXT, " +
			QuizContract.QuestionsTable.COLUMN_OPTION2 + " TEXT, " +
			QuizContract.QuestionsTable.COLUMN_OPTION3 + " TEXT, " +
			QuizContract.QuestionsTable.COLUMN_OPTION4 + " TEXT, " +
			QuizContract.QuestionsTable.COLUMN_ANSWER_NR + " INTEGER, " +
			QuizContract.QuestionsTable.COLUMN_CHAPTER + " TEXT," +
			QuizContract.QuestionsTable.COLUMN_MODULE_NAME + " TEXT," +
			QuizContract.QuestionsTable.COLUMN_IMAGE + " TEXT" +
			");";

	final String SQL_CREATE_SCORES_TABLE = "CREATE TABLE " +
			DbContract.ScoresTable.TABLE_NAME_SCORES + "(" +
			DbContract.ScoresTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			DbContract.ScoresTable.COLUMN_NAME_USER_ID + " INTEGER," +
			DbContract.ScoresTable.COLUMN_NAME_EMAIL + " TEXT, " +
			DbContract.ScoresTable.COLUMN_NAME_SCORE + " INTEGER," +
			DbContract.ScoresTable.COLUMN_NAME_NUM_ITEMS + " INTEGER," +
			DbContract.ScoresTable.COLUMN_NAME_CHAPTER + " TEXT," +
			DbContract.ScoresTable.COLUMN_NAME_NUM_ATTEMPT + " INTEGER," +
			DbContract.ScoresTable.COLUMN_NAME_DURATION + " TEXT," +
			DbContract.ScoresTable.COLUMN_NAME_DATE_TAKEN + " TEXT," +
			DbContract.ScoresTable.COLUMN_NAME_IS_LOCKED + " INTEGER," +
			DbContract.ScoresTable.COLUMN_NAME_IS_COMPLETED + " INTEGER," +
			DbContract.ScoresTable.SYNC_STATUS + " INTEGER" +
			");";

	//for displaying all passed tests
	final String SQL_CREATE_SCORES_TABLE_FROM_SERVER = "CREATE TABLE " +
			DbContract.ScoresMySQLTable.TABLE_NAME_SCORES_MYSQL + "(" +
			DbContract.ScoresMySQLTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			DbContract.ScoresMySQLTable.COLUMN_NAME_USER_ID_MYSQL + " INTEGER," +
			DbContract.ScoresMySQLTable.COLUMN_NAME_EMAIL_MYSQL + " TEXT, " +
			DbContract.ScoresMySQLTable.COLUMN_NAME_SCORE_MYSQL + " INTEGER," +
			DbContract.ScoresMySQLTable.COLUMN_NAME_NUM_ITEMS_MYSQL + " INTEGER," +
			DbContract.ScoresMySQLTable.COLUMN_NAME_CHAPTER_MYSQL + " TEXT UNIQUE," +
			DbContract.ScoresMySQLTable.COLUMN_NAME_NUM_ATTEMPT_MYSQL + " INTEGER," +
			DbContract.ScoresMySQLTable.COLUMN_NAME_DURATION_MYSQL + " TEXT," +
			DbContract.ScoresMySQLTable.COLUMN_NAME_DATE_TAKEN_MYSQL + " TEXT," +
			DbContract.ScoresMySQLTable.COLUMN_NAME_IS_LOCKED_MYSQL + " INTEGER," +
			DbContract.ScoresMySQLTable.COLUMN_NAME_IS_COMPLETED_MYSQL + " INTEGER" +
			");";

	//for retrieval of attempts, unlock and updating
	final String SQL_CREATE_SCORES_ALL_ATTEMPTS_TABLE_FROM_SERVER = "CREATE TABLE " +
			DbContract.AllAttemptsMySQLTable.TABLE_NAME_SCORES_ALL + "(" +
			DbContract.AllAttemptsMySQLTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			DbContract.AllAttemptsMySQLTable.COLUMN_NAME_USER_ID_ALL + " INTEGER," +
			DbContract.AllAttemptsMySQLTable.COLUMN_NAME_EMAIL_ALL + " TEXT, " +
			DbContract.AllAttemptsMySQLTable.COLUMN_NAME_SCORE_ALL + " INTEGER," +
			DbContract.AllAttemptsMySQLTable.COLUMN_NAME_NUM_ITEMS_ALL + " INTEGER," +
			DbContract.AllAttemptsMySQLTable.COLUMN_NAME_CHAPTER_ALL + " TEXT UNIQUE," +
			DbContract.AllAttemptsMySQLTable.COLUMN_NAME_NUM_ATTEMPT_ALL + " INTEGER," +
			DbContract.AllAttemptsMySQLTable.COLUMN_NAME_DURATION_ALL + " TEXT," +
			DbContract.AllAttemptsMySQLTable.COLUMN_NAME_DATE_TAKEN_ALL + " TEXT," +
			DbContract.AllAttemptsMySQLTable.COLUMN_NAME_IS_LOCKED_ALL + " INTEGER," +
			DbContract.AllAttemptsMySQLTable.COLUMN_NAME_IS_COMPLETED_ALL + " INTEGER" +
			");";

	private Context context;
	public static SQLiteDatabase db;	// manipulation with database
	DatabaseHelper dbhelper;
	
	public Database(Context ctx) {
		// TODO Auto-generated constructor stub
		this.context=ctx;
		dbhelper=new DatabaseHelper(ctx);
	}
	
 	
	//SQLITEOpenHelper has methods to creae and open
	class DatabaseHelper extends SQLiteOpenHelper
	{
		//DatabaseHelper's constructor will create the database
		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABSE_VERSION);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL(SQL_CREATE_QUESTIONS_TABLE);
			db.execSQL(SQL_CREATE_SCORES_TABLE);
			db.execSQL(SQL_CREATE_SCORES_TABLE_FROM_SERVER);
			db.execSQL(SQL_CREATE_SCORES_ALL_ATTEMPTS_TABLE_FROM_SERVER);
			Log.d("table is created..","Table is created...");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS tbl_questions");
			db.execSQL("DROP TABLE IF EXISTS tbl_scores");
			db.execSQL("DROP TABLE IF EXISTS tbl_scores_server");
			db.execSQL("DROP TABLE IF EXISTS tbl_scores_all_attempts");
			onCreate(db);
		}

		@Override
		public void onOpen(SQLiteDatabase db) {
			super.onOpen(db);
			db.disableWriteAheadLogging();
		}

	}

	public Database Open() throws SQLException
	{
			db=dbhelper.getWritableDatabase();
			return this;
	}
	
	public void close()
	{
		dbhelper.close();
	}
	
	public long addQuestion(Question question)
	{
		ContentValues cv = new ContentValues();

		cv.put(QuizContract.QuestionsTable.COLUMN_QUESTION, question.getQuestion());
		cv.put(QuizContract.QuestionsTable.COLUMN_OPTION1, question.getOption1());
		cv.put(QuizContract.QuestionsTable.COLUMN_OPTION2, question.getOption2());
		cv.put(QuizContract.QuestionsTable.COLUMN_OPTION3, question.getOption3());
		cv.put(QuizContract.QuestionsTable.COLUMN_OPTION4, question.getOption4());
		cv.put(QuizContract.QuestionsTable.COLUMN_ANSWER_NR, question.getAnswerNr());
		cv.put(QuizContract.QuestionsTable.COLUMN_CHAPTER, question.getChapter());
		cv.put(QuizContract.QuestionsTable.COLUMN_MODULE_NAME, question.getModuleName());
		cv.put(QuizContract.QuestionsTable.COLUMN_IMAGE, question.getImageUrl());
			
			Log.d("inserted... ", question.getQuestion()+"");
			Log.d("inserted... ", question.getAnswerNr()+"");
			return db.insertWithOnConflict(TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
	}

	public long addScoresServer(MyScoresServer scoresServer)
	{
		ContentValues contentValues = new ContentValues();
		contentValues.put(DbContract.ScoresMySQLTable.COLUMN_NAME_USER_ID_MYSQL, scoresServer.getUser_id());
		contentValues.put(DbContract.ScoresMySQLTable.COLUMN_NAME_EMAIL_MYSQL, scoresServer.getEmail());
		contentValues.put(DbContract.ScoresMySQLTable.COLUMN_NAME_SCORE_MYSQL, scoresServer.getScore());
		contentValues.put(DbContract.ScoresMySQLTable.COLUMN_NAME_NUM_ITEMS_MYSQL, scoresServer.getNum_of_items());
		contentValues.put(DbContract.ScoresMySQLTable.COLUMN_NAME_CHAPTER_MYSQL, scoresServer.getChapter());
		contentValues.put(DbContract.ScoresMySQLTable.COLUMN_NAME_NUM_ATTEMPT_MYSQL, scoresServer.getNum_of_attempt());
		contentValues.put(DbContract.ScoresMySQLTable.COLUMN_NAME_DURATION_MYSQL, scoresServer.getDuration());
		contentValues.put(DbContract.ScoresMySQLTable.COLUMN_NAME_DATE_TAKEN_MYSQL, scoresServer.getDate_taken());
		contentValues.put(DbContract.ScoresMySQLTable.COLUMN_NAME_IS_LOCKED_MYSQL, scoresServer.getIsLocked());
		contentValues.put(DbContract.ScoresMySQLTable.COLUMN_NAME_IS_COMPLETED_MYSQL, scoresServer.getIsCompleted());

		Log.d("inserted... ", scoresServer.getUser_id()+"");
		Log.d("inserted... ", scoresServer.getNum_of_attempt()+"");
		return db.insertWithOnConflict(DbContract.ScoresMySQLTable.TABLE_NAME_SCORES_MYSQL, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);

	}

	public void deleteAllQuestion(String studentRoll) {
		// TODO Auto-generated method stub
		db.delete(TABLE_NAME,  null,null);
	      
	        Log.d("Delete Table","Delete Question called.....");
	        
	}

	public void deleteAllScores() {
		// TODO Auto-generated method stub
		db.delete(DbContract.ScoresTable.TABLE_NAME_SCORES,  null,null);

		Log.d("Delete Table","Delete Scores called.....");

	}
}
