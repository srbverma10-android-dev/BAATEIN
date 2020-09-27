package com.sourabh.baatein;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class ShowBeforeUploadActivity extends AppCompatActivity {

    private Button uploadStartButton;
    private ImageView showImage;
    public static EditText caption;

    private String newImageString;
    public static Uri newImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_before_upload);

        initialization();

        Intent intent = getIntent();
        newImageString = intent.getStringExtra("image");

        newImageUri = Uri.parse(newImageString);

        showImage.setImageURI(newImageUri);

        uploadStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startService(new Intent(ShowBeforeUploadActivity.this, UploadPostInBackground.class));

                Intent intent1 = new Intent(ShowBeforeUploadActivity.this, NewsFeedActivity.class);
                startActivity(intent1);

            }
        });

    }

    private void initialization() {

        uploadStartButton = findViewById(R.id.uploadButton);
        showImage = findViewById(R.id.showImageBeforeUpload);
        caption = findViewById(R.id.caption);

    }
}