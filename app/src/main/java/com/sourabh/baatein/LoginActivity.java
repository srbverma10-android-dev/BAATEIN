package com.sourabh.baatein;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private Button signInWithGoogle;
    private CheckBox checkBox;
    private ProgressBar progressBar;

    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReferenceToUser;

    //Google
    private GoogleSignInClient googleSignInClient;

    //Constants
    private int CONST = 1;
    private String id;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initialization();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        signInWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkBox.isChecked()){
                    progressBar.setVisibility(View.VISIBLE);
                    signIn();
                } else {
                    Toast.makeText(LoginActivity.this,"Accept All Terms And Conditions", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void signIn() {

        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, CONST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CONST) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                handleSignInResult(task);
            } catch (Exception e) {
                e.printStackTrace();
                //Toast.makeText(LoginActivity.this,"something is wrong2", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {

        try {
            GoogleSignInAccount acc = completedTask.getResult(ApiException.class);
            Toast.makeText(LoginActivity.this, "PLEASE WAIT UNTIL YOUR PROFILE PHOTO COME", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(acc);
        } catch (ApiException e) {
            progressBar.setVisibility(View.INVISIBLE);
            //Toast.makeText(LoginActivity.this, "SIGNED IN FAILED", Toast.LENGTH_SHORT).show();
        }

    }

    private void FirebaseGoogleAuth(GoogleSignInAccount acc) {

        AuthCredential authCredential = null;
        try {
            authCredential = GoogleAuthProvider.getCredential(acc.getIdToken(), null);
        } catch (Exception e) {
            e.printStackTrace();
            //Toast.makeText(LoginActivity.this,"something is wrong1",Toast.LENGTH_SHORT).show();
        }
        assert authCredential != null;
        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "CONGRATULATION!!! SIGN IN SUCCESSFUL", Toast.LENGTH_SHORT).show();

                    firebaseUser = firebaseAuth.getCurrentUser();
                    try {
                        assert firebaseUser != null;
                        id = firebaseUser.getUid();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    updateUI();

                    user = new User();

                    databaseReferenceToUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            getValues();
                            databaseReferenceToUser.child(id).setValue(user);
                            Toast.makeText(LoginActivity.this, "DATA INSERTED", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                } else {
                    Toast.makeText(LoginActivity.this, "FAILED TO INSERT DATA", Toast.LENGTH_SHORT).show();
                    updateUI();
                }

            }

        });

    }

    private void updateUI() {

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (account != null) {

            databaseReferenceToUser.child(id).child("Follower").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        Intent intent = new Intent(LoginActivity.this, NewsFeedActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Intent intent = new Intent(LoginActivity.this, FindFriendsAfterLogin.class);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

    }

    private void getValues() {

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (account != null) {

            String personname = account.getDisplayName();
            String personemail = account.getEmail();
            String personId = account.getId();
            String personphotostring = null;
            try {
                Uri personphoto = account.getPhotoUrl();
                assert personphoto != null;
                personphotostring = personphoto.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                @Override
                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                    if (task.isComplete()){
                        try {
                            String token = Objects.requireNonNull(task.getResult()).getToken();
                            user.setToken(token);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            user.setEmail(personemail);
            user.setId(personId);
            user.setName(personname);
            user.setImageUrl(personphotostring);

        }


    }


    private void initialization() {

        signInWithGoogle = findViewById(R.id.signInWithGoogle);
        checkBox = findViewById(R.id.checkBox);
        progressBar = findViewById(R.id.progressBar);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceToUser = firebaseDatabase.getReference().child("User");


    }
}