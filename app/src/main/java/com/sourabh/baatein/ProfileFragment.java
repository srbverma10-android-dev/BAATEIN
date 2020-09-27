package com.sourabh.baatein;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ProfileFragment extends Fragment {



    private TextView post;
    private TextView friends;
    private TextView requests;
    private TextView numOfFriends;
    private TextView numOfRequests;
    private TextView numOfPosts;

    public static TextView name;

    private String myid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private String nameString, imageUrl, bioS, dobS, workS, studiesAtS, livesAtS, fromS, hobbiesS;

    private ScrollView scrollView;
    private TextView bio, dob, work, studiesAt, livesAt, from, hobbies;

    public ProfileFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        final View view = inflater.inflate(R.layout.fragment_profile,container,false);
        //Initialization
        ImageView boxForProfile = view.findViewById(R.id.boxforProfile);
        ImageView profileImage = view.findViewById(R.id.profileImageProfile);
        post = view.findViewById(R.id.postprofil);
        friends = view.findViewById(R.id.friendsprofile);
        requests = view.findViewById(R.id.requestsprofile);
        name = view.findViewById(R.id.nameprofile);
        numOfPosts = view.findViewById(R.id.numofpostprofile);
        numOfFriends = view.findViewById(R.id.numoffriendsprofile);
        numOfRequests = view.findViewById(R.id.numofrequestsprofile);
        final Animation topAnim = AnimationUtils.loadAnimation(getContext(), R.anim.topanimforprofile);
        final Animation slide = AnimationUtils.loadAnimation(getContext(), R.anim.bottomanimformoreoption);

        //-----------------------------------------------------------

        bio = view.findViewById(R.id.bio);
        dob = view.findViewById(R.id.dob);
        work = view.findViewById(R.id.work);
        studiesAt = view.findViewById(R.id.studies);
        livesAt = view.findViewById(R.id.lives);
        from = view.findViewById(R.id.from);
        hobbies = view.findViewById(R.id.hobbies);

        //------------------------------------------------------------

        FloatingActionButton floatingActionButton = view.findViewById(R.id.floating);

        scrollView = view.findViewById(R.id.scrollview);

        DatabaseReference databaseReferenceToFriends = FirebaseDatabase.getInstance().getReference("Friends");
        DatabaseReference databaseReferenceToFriendRequest = FirebaseDatabase.getInstance().getReference("FriendRequest");
        DatabaseReference databaseReferenceToUser = FirebaseDatabase.getInstance().getReference("User");




        //-----------------------------------------------------------------------

        boxForProfile.setAnimation(topAnim);
        profileImage.setAnimation(topAnim);
        post.setAnimation(topAnim);
        name.setAnimation(topAnim);
        friends.setAnimation(topAnim);
        requests.setAnimation(topAnim);
        numOfRequests.setAnimation(topAnim);
        numOfFriends.setAnimation(topAnim);
        numOfPosts.setAnimation(topAnim);

        topAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                post.setVisibility(View.VISIBLE);
                friends.setVisibility(View.VISIBLE);
                requests.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        DatabaseReference databaseReferenceToPosts = FirebaseDatabase.getInstance().getReference("POSTS");
        databaseReferenceToPosts.orderByChild("from").equalTo(myid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    numOfPosts.setText(String.valueOf(snapshot.getChildrenCount()));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        try {
            nameString = SplashScreenActivity.user.getName();
            imageUrl = SplashScreenActivity.user.getImageUrl();
        } catch (Exception e) {
            e.printStackTrace();

            databaseReferenceToUser.child(myid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        imageUrl = Objects.requireNonNull(snapshot.child("imageUrl").getValue()).toString();
                        nameString = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

        databaseReferenceToFriends.child(myid).addValueEventListener(new ValueEventListener() {
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

        databaseReferenceToFriendRequest.child(myid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        if (Objects.requireNonNull(dataSnapshot.child("RequestType").getValue()).toString().equals("Received")){
                            numOfRequests.setVisibility(View.VISIBLE);
                            numOfRequests.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                            numOfRequests.setTypeface(Typeface.DEFAULT_BOLD);
                            requests.setTypeface(Typeface.DEFAULT_BOLD);
                        } else {
                            numOfRequests.setVisibility(View.VISIBLE);
                            numOfRequests.setText("0");
                            numOfRequests.setTypeface(Typeface.DEFAULT);
                            requests.setTypeface(Typeface.DEFAULT);
                        }
                    }
                } else {
                    numOfRequests.setVisibility(View.VISIBLE);
                    numOfRequests.setText("0");
                    numOfRequests.setTypeface(Typeface.DEFAULT);
                    requests.setTypeface(Typeface.DEFAULT);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        try {
            Glide.with(Objects.requireNonNull(getContext()))
                    .load(imageUrl)
                    .circleCrop()
                    .into(profileImage);
            name.setText(nameString);

        } catch (Exception e) {
            e.printStackTrace();
        }



        DatabaseReference databaseReferenceToAbout = FirebaseDatabase.getInstance().getReference("About").child(myid);
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

        requests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), ShowAllFriendRequestActivity.class);
                startActivity(intent);

            }
        });

        friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), ShowAllFriendsActivity.class);
                startActivity(intent);

            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                intent.putExtra("name", nameString);
                intent.putExtra("imageUrl", imageUrl);
                intent.putExtra("bio", bioS);
                intent.putExtra("dob", dobS);
                intent.putExtra("work", workS);
                intent.putExtra("studiesAt", studiesAtS);
                intent.putExtra("livesAt", livesAtS);
                intent.putExtra("from", fromS);
                intent.putExtra("hobbies", hobbiesS);
                startActivity(intent);

            }
        });


        return view;
    }



}