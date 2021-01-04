package com.cav.quizinstructions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.Viewholder> {

    private List<MyScoresServer> myScoresServerList;
    private Context context;

    public CustomAdapter(List<MyScoresServer> myScoresServerList, Context context) {
        this.myScoresServerList = myScoresServerList;
        this.context = context;
    }

    public class Viewholder extends RecyclerView.ViewHolder{

        public TextView score;
        public TextView num_of_items;
        public TextView chapter;
        public TextView num_of_attempt;
        public TextView date_taken;
        public TextView duration;
        public ImageView passed_icon;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            score = (TextView)itemView.findViewById(R.id.txt_score_itemView_summary);
            num_of_items = (TextView)itemView.findViewById(R.id.txt_numItems_itemView_summary);
            chapter = (TextView)itemView.findViewById(R.id.txt_chapter_list_summary);
            num_of_attempt = (TextView)itemView.findViewById(R.id.txt_attempt_summary);
            duration = (TextView)itemView.findViewById(R.id.duration_summary);
            date_taken = (TextView)itemView.findViewById(R.id.date_taken_summary);
            passed_icon = (ImageView)itemView.findViewById(R.id.img_passed);
        }
    }

    @NonNull
    @Override
    public CustomAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.summary_list_view, parent, false);
        return new Viewholder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.Viewholder holder, int position) {

        holder.score.setText("Score: " +myScoresServerList.get(position).getScore());
        holder.num_of_items.setText("/" +myScoresServerList.get(position).getNum_of_items());

        String thisChap = myScoresServerList.get(position).getChapter();
        String module = "";
        switch(thisChap){
            case "1":
                module = Constant._1;
                break;
            case "2":
                module = Constant._2;
                break;
            case "3":
                module = Constant._3;
                break;
        }

        holder.chapter.setText(module);
        holder.num_of_attempt.setText("Total attempts: " +myScoresServerList.get(position).getNum_of_attempt());
        holder.duration.setText("Duration: " + myScoresServerList.get(position).getDuration());
        holder.date_taken.setText("Date taken: " + myScoresServerList.get(position).getDate_taken());
    }

    @Override
    public int getItemCount() {
        return myScoresServerList.size();
    }


}
