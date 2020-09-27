package com.sourabh.baatein;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profileImage;
    private TextView name, numOfPosts, numOfFriends;
    private TextView bio, dob, work, studiesAt, livesAt, from, hobbies;
    private Button followButton;

    private ScrollView scrollView;

    private Animation slide;

    private String nameS, imageUrlS, emailS, idS, bioS, dobS, workS, studiesAtS, livesAtS, fromS, hobbiesS;;

    private String myId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

    private DatabaseReference databaseReferenceToFriends = FirebaseDatabase.getInstance().getReference("Friends");

    private String currentState = "RequestReceived";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initialization();

        Intent intent = getIntent();
        nameS = intent.getStringExtra("name");
        emailS = intent.getStringExtra("email");
        idS = intent.getStringExtra("id");
        imageUrlS = intent.getStringExtra("imageUrl");

        try {
            Glide.with(getApplicationContext())
                    .load(imageUrlS).circleCrop().into(profileImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            name.setText(nameS);
        } catch (Exception e) {
            e.printStackTrace();
        }


        numOfPosts.setText("0");

        databaseReferenceToFriends.child(idS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    numOfFriends.setVisibility(View.VISIBLE);
                    numOfFriends.setText(String.valueOf(snapshot.getChildrenCount()));
                } else {
                    numOfFriends.setVisibility(View.VISIBLE);
                    numOfFriends.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        checkUser(idS);

        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                followButton.setText(R.string.processing);

                if (currentState.equals("friends")){
                    final DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Friends");
                    databaseReference1.child(myId).child(idS).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            databaseReference1.child(idS).child(myId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    currentState = "not_friends";
                                    followButton.setText(R.string.follow);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }

                if (currentState.equals("RequestReceived")){

                    final String currentDateAndTime = DateFormat.getDateTimeInstance().format(new Date());

                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Friends");
                    reference.child(myId)
                            .child(idS)
                            .setValue(currentDateAndTime)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    reference.child(idS).child(myId).setValue(currentDateAndTime)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("FriendRequest")
                                                            .child(myId).child(idS);
                                                    databaseReference1.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("FriendRequest")
                                                                    .child(idS).child(myId);
                                                            databaseReference2.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {

                                                                    currentState = "friends";
                                                                    followButton.setText(R.string.following);

                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {

                                                                }
                                                            });
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {

                                                        }
                                                    });

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ProfileActivity.this,"Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileActivity.this,"Failed", Toast.LENGTH_SHORT).show();
                        }
                    });

                }

                //--------------------------NOT FRIENDS STATE ------------------------------------

                if (currentState.equals("not_friends")) {
                    DatabaseReference databaseReferenceAsSender = FirebaseDatabase.getInstance().getReference("FriendRequest")
                            .child(idS).child(myId).child("RequestType");
                    databaseReferenceAsSender.setValue("Received").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                DatabaseReference databaseReferenceAsReceiver = FirebaseDatabase.getInstance().getReference("FriendRequest")
                                        .child(myId).child(idS).child("RequestType");
                                databaseReferenceAsReceiver.setValue("Sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        followButton.setText(R.string.requestsend);
                                        currentState = "request_sent";

                                    }
                                });
                            } else {
                                Toast.makeText(ProfileActivity.this,"FAILED", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }


                //--------------------------REQUEST SENT STATE ------------------------------------

                if (currentState.equals("request_sent")){
                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("FriendRequest")
                            .child(myId).child(idS);
                    databaseReference1.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("FriendRequest")
                                    .child(idS).child(myId);
                            databaseReference2.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    currentState = "not_friends";
                                    followButton.setText(R.string.follow);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }

            }
        });

        DatabaseReference databaseReferenceToAbout = FirebaseDatabase.getInstance().getReference("About").child(idS);
        databaseReferenceToAbout.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    scrollView.setAnimation(slide);
                    scrollView.setVisibility(View.VISIBLE);

                    bioS = Objects.requireNonNull(snapshot.child("BIO").getValue()).toString();
                    dobS = Objects.requireNonNull(snapshot.child("DOB").getValue()).toString();
                    workS = Objects.requireNonNull(snapshot.child("WORKS").getValue()).toString();
                    studiesAtS =Objects.requireNonNull(snapshot.child("STUDIESAT").getValue()).toString();
                    livesAtS = Objects.requireNonNull(snapshot.child("LIVESAT").getValue()).toString();
                    fromS =Objects.requireNonNull(snapshot.child("FROM").getValue()).toString();
                    hobbiesS = Objects.requireNonNull(snapshot.child("HOBBIES").getValue()).toString();

                    try {
                        bio.setText(bioS);
                        dob.setText(dobS);
                        work.setText(workS);
                        studiesAt.setText(studiesAtS);
                        livesAt.setText(livesAtS);
                        from.setText(fromS);
                        hobbies.setText(hobbiesS);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }else {
                    scrollView.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void checkUser(final String userid) {

        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("FriendRequest")
                .child(myId).child(userid);
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (Objects.requireNonNull(snapshot.child("RequestType").getValue()).toString().equals("Sent")){
                        followButton.setText(R.string.requestsend);
                        currentState = "request_sent";
                    } else if (Objects.requireNonNull(snapshot.child("RequestType").getValue()).toString().equals("Received")){
                        followButton.setText(R.string.confirm);
                        currentState = "RequestReceived";
                    } else {
                        followButton.setText(R.string.follow);
                        currentState = "not_friends";
                    }
                } else {

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Friends").child(myId);
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                if (snapshot.child(userid).exists()){
                                    followButton.setText(R.string.following);
                                    currentState = "friends";
                                } else {
                                    followButton.setText(R.string.follow);
                                    currentState = "not_friends";

                                }
                            } else {
                                followButton.setText(R.string.follow);
                                currentState = "not_friends";
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void initialization() {

        profileImage = findViewById(R.id.profileImageForProfileActivity);
        name = findViewById(R.id.nameForProfileActivity);
        numOfPosts = findViewById(R.id.numOfPostForProfileActivity);
        numOfFriends = findViewById(R.id.numOfFriendsProfileActivity);
        followButton = findViewById(R.id.followButtonForProfileActivity);
        scrollView = findViewById(R.id.scrollview);
        slide = AnimationUtils.loadAnimation(ProfileActivity.this, R.anim.bottomanimformoreoption);

        bio = findViewById(R.id.bio);
        dob = findViewById(R.id.dob);
        work = findViewById(R.id.work);
        studiesAt = findViewById(R.id.studies);
        livesAt = findViewById(R.id.lives);
        from = findViewById(R.id.from);
        hobbies = findViewById(R.id.hobbies);

    }
}