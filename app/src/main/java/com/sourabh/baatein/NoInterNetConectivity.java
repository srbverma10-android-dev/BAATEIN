package com.sourabh.baatein;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class NoInterNetConectivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_inter_net_conectivity);

        ImageView noInternetGIF = findViewById(R.id.nointernet);

        try {
            Glide.with(getApplicationContext())
                    .load(R.drawable.nointernet).into(noInternetGIF);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}