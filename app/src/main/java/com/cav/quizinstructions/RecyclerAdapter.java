package com.cav.quizinstructions;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static com.cav.quizinstructions.Constant._1;
import static com.cav.quizinstructions.Constant._2;
import static com.cav.quizinstructions.Constant._3;

public class RecyclerAdapter extends RecyclerView.Adapter <RecyclerAdapter.MyViewHolder> {

    public ArrayList<Score> arrayList = new ArrayList<>();

    public RecyclerAdapter(ArrayList<Score> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.Score.setText("" +arrayList.get(position).getScore());
        holder.Items.setText("/" +arrayList.get(position).getNum_of_items());
        String passedChap = arrayList.get(position).getChapter();
        String chap = "";
        switch (passedChap){
            case "1":
                chap = _1;
                break;
            case "2":
                chap = _2;
                break;
            case "3":
                chap = _3;
                break;
        }
        holder.Chapter.setText(chap);
        holder.Attempt.setText("Attempt #:" +arrayList.get(position).getNum_of_attempt());
        int sync_status = arrayList.get(position).getSync_status();
        if (sync_status == DbContract.SYNC_STATUS_SAVED) {
            holder.Sync_status.setImageResource(R.drawable.saved);
        }else {
            holder.Sync_status.setImageResource(R.drawable.ic_sync);
        }

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView Sync_status;
        TextView Score, Items, Chapter, Attempt;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            Sync_status = (ImageView)itemView.findViewById(R.id.img_sync);
            Score = (TextView)itemView.findViewById(R.id.txt_score_itemView);
            Items = (TextView)itemView.findViewById(R.id.txt_numItems_itemView);
            Chapter = (TextView)itemView.findViewById(R.id.txt_chapter_list);
            Attempt = (TextView)itemView.findViewById(R.id.txt_attempt);
        }
    }
}
