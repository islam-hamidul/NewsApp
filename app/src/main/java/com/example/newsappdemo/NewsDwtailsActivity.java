package com.example.newsappdemo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class NewsDwtailsActivity extends AppCompatActivity {
    TextView title, decs;
    ImageView img;
    String mTitle, mDecs, mImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_dwtails);
        title = findViewById(R.id.title);
        decs = findViewById(R.id.desc);
        img = findViewById(R.id.img);
        Intent intent = getIntent();
        mTitle = intent.getStringExtra("title");
        mDecs = intent.getStringExtra("url");
        mImg = intent.getStringExtra("img");
        title.setText(mTitle);
        decs.setText(mDecs);
        Picasso.with(this).load(mImg).into(img);
    }
}