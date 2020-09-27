package com.sourabh.baatein;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


public class SplashScreenActivity extends AppCompatActivity {

    private ImageView logoAndName;
    private TextView slogan, from;
    private Animation topAnim, bottomAnim;

    //Firebase
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReferenceToUser = FirebaseDatabase.getInstance().getReference("User");
    private String myId;

    private Handler handler;

    //Strings and other Variables

    public static User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        makefullscreen();
        intialization();
        animation();

        if (ConnectionManager.checkConnection(getBaseContext())) {

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (firebaseUser != null){
                        myId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

//                        databaseReferenceToUser.child(myId).addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                                user = snapshot.getValue(User.class);
//
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//
//                            }
//                        });

                        Intent intent = new Intent(SplashScreenActivity.this, NewsFeedActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }else{
                        Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                    finish();
                }
            },1500);

        } else {
//            Toast.makeText(this,"No INternet", Toast.LENGTH_SHORT).show();
//            Intent network = new Intent(SplashScreenActivity.this, NoInterNetConectivity.class);
//            network.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(network);
//            finish();
        }
/*        finish();*/

    }


    private void intialization() {

        logoAndName = findViewById(R.id.nameAndLogo);
        slogan = findViewById(R.id.slogan);
        from = findViewById(R.id.from);
        topAnim = AnimationUtils.loadAnimation(SplashScreenActivity.this, R.anim.topanim);
        bottomAnim = AnimationUtils.loadAnimation(SplashScreenActivity.this, R.anim.bottomanim);

        handler = new Handler();

        //Firebase
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    }

    private void makefullscreen() {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    private void animation() {

        logoAndName.setAnimation(topAnim);
        from.setAnimation(bottomAnim);
        slogan.setAnimation(topAnim);

    }
}