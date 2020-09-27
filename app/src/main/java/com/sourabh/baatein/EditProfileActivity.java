package com.sourabh.baatein;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity {

    private EditText nameEditText;
    private ImageView profileImage, chooseFile;

    private FloatingActionButton floatingActionButton;

    public static Uri newImageUri;

    private EditText bio, dob, work, studiesAt, livesAt, from, hobbies;


    private static final int PICK_IMAGE_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        intialization();

        final Intent intent = getIntent();
        String nameString = intent.getStringExtra("name");
        String imageUrl = intent.getStringExtra("imageUrl");
        String bioS = intent.getStringExtra("bio");
        String dobS = intent.getStringExtra("dob");
        String workS = intent.getStringExtra("work");
        String studiesAtS = intent.getStringExtra("studiesAt");
        String livesAtS = intent.getStringExtra("livesAt");
        String fromS = intent.getStringExtra("from");
        String hobbiesS = intent.getStringExtra("hobbies");


        nameEditText.setText(nameString);
        bio.setText(bioS);
        dob.setText(dobS);
        work.setText(workS);
        studiesAt.setText(studiesAtS);
        livesAt.setText(livesAtS);
        from.setText(fromS);
        hobbies.setText(hobbiesS);

        try {
            Glide.with(getApplicationContext())
                    .load(imageUrl)
                    .circleCrop()
                    .into(profileImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        chooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);

            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String changedName = nameEditText.getText().toString();

                final String myID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

                final DatabaseReference toUser = FirebaseDatabase.getInstance().getReference("User").child(myID);
                toUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            toUser.child("name").setValue(changedName).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    ProfileFragment.name.setText(changedName);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(EditProfileActivity.this,"FAILED", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                //--------------------------------------------

                if (!bio.getText().toString().equals("") && !dob.getText().toString().equals("") && !work.getText().toString().equals("") && !studiesAt.getText().toString().equals("")
                                    && !livesAt.getText().toString().equals("") && !from.getText().toString().equals("") && !hobbies.getText().toString().equals("")){

                    final DatabaseReference databaseReferenceToAboutUser = FirebaseDatabase.getInstance().getReference("About").child(myID);
                    databaseReferenceToAboutUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            HashMap<String ,String> hashMap = new HashMap<>();
                            hashMap.put("BIO", bio.getText().toString());
                            hashMap.put("DOB", dob.getText().toString());
                            hashMap.put("WORKS", work.getText().toString());
                            hashMap.put("STUDIESAT", studiesAt.getText().toString());
                            hashMap.put("LIVESAT", livesAt.getText().toString());
                            hashMap.put("FROM", from.getText().toString());
                            hashMap.put("HOBBIES", hobbies.getText().toString());

                            databaseReferenceToAboutUser.setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(EditProfileActivity.this,"About section is UPDATED successfully", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(EditProfileActivity.this,"About section is NOT UPDATED", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }else {
                    Toast.makeText(EditProfileActivity.this,"Something is Empty", Toast.LENGTH_SHORT).show();
                }



                //--------------------------------------------

                Intent intent1 = new Intent(EditProfileActivity.this, NewsFeedActivity.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent1);

                //--------------------------------------------

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            newImageUri = data.getData();

            Glide.with(getApplicationContext()).load(newImageUri).circleCrop().into(profileImage);

            startService(new Intent(EditProfileActivity.this, UploadProfileInBackground.class));

        }



    }





    private void intialization() {

        nameEditText = findViewById(R.id.nameEditText);
        profileImage = findViewById(R.id.profileImageInEditProfile);
        chooseFile = findViewById(R.id.chooseFile);
        floatingActionButton = findViewById(R.id.savechanges);

        bio = findViewById(R.id.bio);
        dob = findViewById(R.id.dob);
        work = findViewById(R.id.work);
        studiesAt = findViewById(R.id.studies);
        livesAt = findViewById(R.id.lives);
        from = findViewById(R.id.from);
        hobbies = findViewById(R.id.hobbies);




    }
}