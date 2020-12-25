package com.cav.quizinstructions;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.util.Linkify;
import android.widget.TextView;

public class Advisory extends AppCompatActivity {

    TextView dot_link, lto_link, ltfrb_link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advisory);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //getSupportActionBar().setTitle("Advisory");

        dot_link = findViewById(R.id.dot_link);
        dot_link.setText("Visit website:\n https://dotr.gov.ph/");
        Linkify.addLinks(dot_link, Linkify.WEB_URLS);

        lto_link = findViewById(R.id.lto_link);
        lto_link.setText("Visit website:\n https://www.lto.gov.ph/");
        Linkify.addLinks(lto_link, Linkify.WEB_URLS);

        ltfrb_link = findViewById(R.id.ltfrb_link);
        ltfrb_link.setText("Visit website:\n https://ltfrb.gov.ph/");
        Linkify.addLinks(ltfrb_link, Linkify.WEB_URLS);
    }
}