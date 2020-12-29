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
			QuizContract.QuestionsTable.COLUMN_CHAPTER + " TEXT" +
			");";

	final String SQL_CREATE_SCORES_TABLE = "CREATE TABLE " +
			DbContract.ScoresTable.TABLE_NAME_SCORES + "(" +
			DbContract.ScoresTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			DbContract.ScoresTable.COLUMN_NAME_USER_ID + " INTEGER," +
			DbContract.ScoresTable.COLUMN_NAME_EMAIL + " TEXT, " +
			DbContract.ScoresTable.COLUMN_NAME_SCORE + " INTEGER," +
			DbContract.ScoresTable.COLUMN_NAME_NUM_ITEMS + " INTEGER," +
			DbContract.ScoresTable.COLUMN_NAME_CHAPTER + " TEXT UNIQUE," +
			DbContract.ScoresTable.COLUMN_NAME_NUM_ATTEMPT + " INTEGER," +
			DbContract.ScoresTable.COLUMN_NAME_DATE_TAKEN + " TEXT," +
			DbContract.ScoresTable.SYNC_STATUS + " INTEGER" +
			");";

	final String SQL_CREATE_SCORES_TABLE_FROM_SERVER = "CREATE TABLE " +
			DbContract.ScoresMySQLTable.TABLE_NAME_SCORES_MYSQL + "(" +
			DbContract.ScoresMySQLTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, " +
			DbContract.ScoresMySQLTable.COLUMN_NAME_USER_ID_MYSQL + " INTEGER," +
			DbContract.ScoresMySQLTable.COLUMN_NAME_EMAIL_MYSQL + " TEXT, " +
			DbContract.ScoresMySQLTable.COLUMN_NAME_SCORE_MYSQL + " INTEGER," +
			DbContract.ScoresMySQLTable.COLUMN_NAME_NUM_ITEMS_MYSQL + " INTEGER," +
			DbContract.ScoresMySQLTable.COLUMN_NAME_CHAPTER_MYSQL + " TEXT UNIQUE," +
			DbContract.ScoresMySQLTable.COLUMN_NAME_NUM_ATTEMPT_MYSQL + " INTEGER," +
			DbContract.ScoresMySQLTable.COLUMN_NAME_DATE_TAKEN_MYSQL + " TEXT," +
			DbContract.ScoresMySQLTable.SYNC_STATUS_MYSQL + " INTEGER" +
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
			Log.d("table is created..","Table is created...");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS tbl_questions");
			db.execSQL("DROP TABLE IF EXISTS tbl_scores");
			db.execSQL("DROP TABLE IF EXISTS tbl_scores_server");
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
			
			Log.d("inserted... ", question.getQuestion()+"");
			Log.d("inserted... ", question.getAnswerNr()+"");
			return db.insertWithOnConflict(TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
	}

	public long addScores(Score score)
	{
		ContentValues contentValues = new ContentValues();
		contentValues.put(DbContract.ScoresMySQLTable.COLUMN_NAME_USER_ID_MYSQL, score.getUser_id());
		contentValues.put(DbContract.ScoresMySQLTable.COLUMN_NAME_EMAIL_MYSQL, score.getEmail());
		contentValues.put(DbContract.ScoresMySQLTable.COLUMN_NAME_SCORE_MYSQL, score.getScore());
		contentValues.put(DbContract.ScoresMySQLTable.COLUMN_NAME_NUM_ITEMS_MYSQL, score.getNum_of_items());
		contentValues.put(DbContract.ScoresMySQLTable.COLUMN_NAME_CHAPTER_MYSQL, score.getChapter());
		contentValues.put(DbContract.ScoresMySQLTable.COLUMN_NAME_NUM_ATTEMPT_MYSQL, score.getNum_of_attempt());
		contentValues.put(DbContract.ScoresMySQLTable.COLUMN_NAME_DATE_TAKEN_MYSQL, score.getDate_taken());
		contentValues.put(DbContract.ScoresMySQLTable.SYNC_STATUS_MYSQL, score.getSync_status());

		Log.d("inserted... ", score.getUser_id()+"");
		Log.d("inserted... ", score.getNum_of_attempt()+"");
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

//	public List<Question> getAllQuestions(){
//		List<Question> questionList = new ArrayList<>();
//
//		Cursor c = db.rawQuery("SELECT * FROM " + QuizContract.QuestionsTable.TABLE_NAME, null);
//
//		if(c.moveToFirst()){
//			do{
//				Question question = new Question();
//				question.setQuestion(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_QUESTION)));
//				question.setOption1(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_OPTION1)));
//				question.setOption2(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_OPTION2)));
//				question.setOption3(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_OPTION3)));
//				question.setOption4(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_OPTION4)));
//				question.setAnswerNr(c.getInt(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_ANSWER_NR)));
//				question.setChapter(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_CHAPTER)));
//				questionList.add(question);
//			}while(c.moveToNext());
//		}
//
//		c.close();
//		return questionList;
//	}
}
