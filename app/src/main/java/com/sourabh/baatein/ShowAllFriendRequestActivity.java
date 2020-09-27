package com.sourabh.baatein;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShowAllFriendRequestActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    public static ImageView profileImageProfileBig;
    public static ImageView forShowMore;
    public static ImageView imageView;
    public static TextView nameShow, emailShow, declineRequest, showProfile, reportUser;

    //Firebase
    private String myId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_friend_request);

        initialization();

        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplication());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ShowAllFriendRequestAdapter(getDataset(), getApplication());

        recyclerView.setAdapter(adapter);

        recyclerView.getRecycledViewPool().clear();
        adapter.notifyDataSetChanged();

    }

    private void listenForData() {

        final DatabaseReference userDB = FirebaseDatabase.getInstance().getReference().child("User");
        DatabaseReference requestDB = FirebaseDatabase.getInstance().getReference("FriendRequest").child(myId);

        requestDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        if (dataSnapshot.exists()){
                            if (Objects.requireNonNull(dataSnapshot.child("RequestType").getValue()).toString().equals("Received")){
                                String userId = dataSnapshot.getRef().getKey();

                                assert userId != null;
                                userDB.child(userId).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        results.clear();

                                        if (snapshot.exists()){
                                            String email = "";
                                            String name = "";
                                            String uid = snapshot.getRef().getKey();
                                            String profile_photo = "";
                                            String token = "";

                                            if (snapshot.child("email").getValue() != null){
                                                email = Objects.requireNonNull(snapshot.child("email").getValue()).toString();
                                            }

                                            if (snapshot.child("imageUrl").getValue() != null){
                                                profile_photo = Objects.requireNonNull(snapshot.child("imageUrl").getValue()).toString();
                                            }

                                            if (snapshot.child("name").getValue() != null){
                                                name = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                                            }

                                            if (snapshot.child("token").getValue() != null){
                                                token = Objects.requireNonNull(snapshot.child("token").getValue()).toString();
                                            }

                                            if (!email.equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail())){
                                                User obj = new User(name, uid, email, profile_photo, token);
                                                results.add(obj);
                                                adapter.notifyDataSetChanged();
                                            }
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public ArrayList<User> results = new ArrayList<>();

    public ArrayList<User> getDataset() {
        results.clear();
        listenForData();
        return results;
    }

    private void clear() {
        int size = this.results.size();
        this.results.clear();
        adapter.notifyItemRangeChanged(0,size);
    }

    private void initialization() {

        recyclerView = findViewById(R.id.recyclerView);
        profileImageProfileBig = findViewById(R.id.profileImageProfileBig);
        nameShow = findViewById(R.id.nameProfileBig);
        emailShow = findViewById(R.id.emailProfileBig);
        declineRequest = findViewById(R.id.declinerequest);
        showProfile = findViewById(R.id.showProfile);
        reportUser = findViewById(R.id.reportUser);
        imageView = findViewById(R.id.imageView);
        forShowMore = findViewById(R.id.forShowMore);

    }
}