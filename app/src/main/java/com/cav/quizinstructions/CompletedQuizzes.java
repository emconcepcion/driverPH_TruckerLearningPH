package com.cav.quizinstructions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.muddzdev.styleabletoast.StyleableToast;

import java.util.ArrayList;

public class CompletedQuizzes extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView Score, Email, Num_of_items, Chapter, Num_Of_Attempt, Date_Taken;
    RecyclerView.LayoutManager layoutManager;
    RecyclerAdapter adapter;
    ArrayList<Score> arrayList = new ArrayList<>();
    BroadcastReceiver broadcastReceiver;
    Button btn_view_result;
    Button refresh_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_quizzes);

        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new RecyclerAdapter(arrayList);
        recyclerView.setAdapter(adapter);
        readFromLocalStorage();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                readFromLocalStorage();
            }
        };
    }

    public void onClick (View v){
        Intent intent = getIntent();
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
        overridePendingTransition(0, 0);
        StyleableToast.makeText(getApplicationContext(), CompletedQuizzes.this.getString(R.string.list_updated),
                Toast.LENGTH_LONG, R.style.toastStyle).show();
    }

    public void readFromLocalStorage(){

        //clear data from arraylist
        arrayList.clear();
        QuizDbHelper dbHelper = new QuizDbHelper(this);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        //read using cursor
        Cursor cursor = dbHelper.readFromLocalDatabase(database);

        while(cursor.moveToNext()){
            String email = cursor.getString(cursor.getColumnIndex(DbContract.ScoresTable.COLUMN_NAME_EMAIL));
            int score = cursor.getInt(cursor.getColumnIndex(DbContract.ScoresTable.COLUMN_NAME_SCORE));
            int num_items = cursor.getInt(cursor.getColumnIndex(DbContract.ScoresTable.COLUMN_NAME_NUM_ITEMS));
            String chap = cursor.getString(cursor.getColumnIndex(DbContract.ScoresTable.COLUMN_NAME_CHAPTER));
            int num_attempt = cursor.getInt(cursor.getColumnIndex(DbContract.ScoresTable.COLUMN_NAME_NUM_ATTEMPT));
            String date_Taken = cursor.getString(cursor.getColumnIndex(DbContract.ScoresTable.COLUMN_NAME_DATE_TAKEN));
            int sync_status = cursor.getInt(cursor.getColumnIndex(DbContract.ScoresTable.SYNC_STATUS));

            arrayList.add(new Score(email, score, num_items, chap, num_attempt, date_Taken, sync_status));
        }

        //adapter.notifyDataSetChanged();
        cursor.close();
        dbHelper.close();
    }
}