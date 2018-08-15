package com.cpsc.photos;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class PhotoBigActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_big);
        ImageView bigImg = findViewById(R.id.bitImag);
        String imgPath = getIntent().getStringExtra("imgPath");
        Glide.with(this).load(imgPath).error(R.mipmap.ic_launcher).into(bigImg);
        bigImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
