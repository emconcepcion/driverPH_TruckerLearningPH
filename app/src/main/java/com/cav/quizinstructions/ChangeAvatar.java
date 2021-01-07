package com.cav.quizinstructions;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ChangeAvatar extends AppCompatActivity {
    public String email, img_num, fname, lname,username, password, id, oldimage;
    ImageView frame1, frame2, frame3, frame4, frame5, frame6, avatar1, avatar2, avatar3, avatar4, avatar5, avatar6, imageView7;
    TextView textView12;
    Button selectbtn, cancelbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_avatar);

        frame1 = findViewById(R.id.frame1);
        frame2 = findViewById(R.id.frame2);
        frame3 = findViewById(R.id.frame3);
        frame4 = findViewById(R.id.frame4);
        frame5 = findViewById(R.id.frame5);
        frame6 = findViewById(R.id.frame6);
        avatar1 = findViewById(R.id.avatar_1);
        avatar2 = findViewById(R.id.avatar_2);
        avatar3 = findViewById(R.id.avatar_3);
        avatar4 = findViewById(R.id.avatar_4);
        avatar5 = findViewById(R.id.avatar_5);
        avatar6 = findViewById(R.id.avatar_6);
        imageView7 = findViewById(R.id.imageView7);
        textView12 = findViewById(R.id.textView12);
        selectbtn = findViewById(R.id.selectbtn);
        cancelbtn = findViewById(R.id.cancelbtn);
        fname = getIntent().getStringExtra("first_name");
        lname = getIntent().getStringExtra("last_name");
        email = getIntent().getStringExtra("email");
        username = getIntent().getStringExtra("username");
        img_num = getIntent().getStringExtra("image");
        oldimage = getIntent().getStringExtra("image");
        id = getIntent().getStringExtra("id");

        textView12.setText(fname + " " + lname);
        framesetv();

        avatar1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frame1.setVisibility(View.VISIBLE);
                frame2.setVisibility(View.INVISIBLE);
                frame3.setVisibility(View.INVISIBLE);
                frame4.setVisibility(View.INVISIBLE);
                frame5.setVisibility(View.INVISIBLE);
                frame6.setVisibility(View.INVISIBLE);

                img_num = "1";

                Toast.makeText(ChangeAvatar.this, img_num, Toast.LENGTH_SHORT).show();
            }
        });

        avatar1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frame1.setVisibility(View.VISIBLE);
                frame2.setVisibility(View.INVISIBLE);
                frame3.setVisibility(View.INVISIBLE);
                frame4.setVisibility(View.INVISIBLE);
                frame5.setVisibility(View.INVISIBLE);
                frame6.setVisibility(View.INVISIBLE);

                img_num = "1";

                Toast.makeText(ChangeAvatar.this, img_num, Toast.LENGTH_SHORT).show();
            }
        });

        avatar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frame1.setVisibility(View.INVISIBLE);
                frame2.setVisibility(View.VISIBLE);
                frame3.setVisibility(View.INVISIBLE);
                frame4.setVisibility(View.INVISIBLE);
                frame5.setVisibility(View.INVISIBLE);
                frame6.setVisibility(View.INVISIBLE);

                img_num = "2";

                Toast.makeText(ChangeAvatar.this, img_num, Toast.LENGTH_SHORT).show();
            }
        });

        avatar3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frame1.setVisibility(View.INVISIBLE);
                frame2.setVisibility(View.INVISIBLE);
                frame3.setVisibility(View.VISIBLE);
                frame4.setVisibility(View.INVISIBLE);
                frame5.setVisibility(View.INVISIBLE);
                frame6.setVisibility(View.INVISIBLE);

                img_num = "3";

                Toast.makeText(ChangeAvatar.this, img_num, Toast.LENGTH_SHORT).show();
            }
        });

        avatar4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frame1.setVisibility(View.INVISIBLE);
                frame2.setVisibility(View.INVISIBLE);
                frame3.setVisibility(View.INVISIBLE);
                frame4.setVisibility(View.VISIBLE);
                frame5.setVisibility(View.INVISIBLE);
                frame6.setVisibility(View.INVISIBLE);

                img_num = "4";

                Toast.makeText(ChangeAvatar.this, img_num, Toast.LENGTH_SHORT).show();
            }
        });

        avatar5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frame1.setVisibility(View.INVISIBLE);
                frame2.setVisibility(View.INVISIBLE);
                frame3.setVisibility(View.INVISIBLE);
                frame4.setVisibility(View.INVISIBLE);
                frame5.setVisibility(View.VISIBLE);
                frame6.setVisibility(View.INVISIBLE);

                img_num = "5";

                Toast.makeText(ChangeAvatar.this, img_num, Toast.LENGTH_SHORT).show();
            }
        });

        avatar6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frame1.setVisibility(View.INVISIBLE);
                frame2.setVisibility(View.INVISIBLE);
                frame3.setVisibility(View.INVISIBLE);
                frame4.setVisibility(View.INVISIBLE);
                frame5.setVisibility(View.INVISIBLE);
                frame6.setVisibility(View.VISIBLE);

                img_num = "6";

                Toast.makeText(ChangeAvatar.this, img_num, Toast.LENGTH_SHORT).show();
            }
        });

        selectbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChangeAvatar.this, AccountEdit.class);
                Bundle extras = new Bundle();
                extras.putString("first_name", fname);
                extras.putString("last_name", lname);
                extras.putString("email", email);
                extras.putString("username", username);
                extras.putString("image", img_num);
                extras.putString("id", id);
                intent.putExtras(extras);
                startActivity(intent);
            }
        });

        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChangeAvatar.this, AccountEdit.class);
                Bundle extras = new Bundle();
                extras.putString("first_name", fname);
                extras.putString("last_name", lname);
                extras.putString("email", email);
                extras.putString("username", username);
                extras.putString("image", oldimage);
                extras.putString("id", id);
                intent.putExtras(extras);
                startActivity(intent);
            }
        });
    }

    public void framesetv(){
        if(img_num.equals("1")){
            frame1.setVisibility(View.VISIBLE);
            imageView7.setImageResource(R.drawable.avatar1);
        }else if(img_num.equals("2")){
            frame1.setVisibility(View.INVISIBLE);
            frame2.setVisibility(View.VISIBLE);
            imageView7.setImageResource(R.drawable.avatar2);
        }else if(img_num.equals("3")){
            frame1.setVisibility(View.INVISIBLE);
            frame3.setVisibility(View.VISIBLE);
            imageView7.setImageResource(R.drawable.avatar3);
        }else if(img_num.equals("4")){
            frame1.setVisibility(View.INVISIBLE);
            frame4.setVisibility(View.VISIBLE);
            imageView7.setImageResource(R.drawable.avatar4);
        }else if(img_num.equals("5")){
            frame1.setVisibility(View.INVISIBLE);
            frame5.setVisibility(View.VISIBLE);
            imageView7.setImageResource(R.drawable.avatar5);
        }else if(img_num.equals("6")){
            frame1.setVisibility(View.INVISIBLE);
            frame6.setVisibility(View.VISIBLE);
            imageView7.setImageResource(R.drawable.avatar6);
        }
    }
}