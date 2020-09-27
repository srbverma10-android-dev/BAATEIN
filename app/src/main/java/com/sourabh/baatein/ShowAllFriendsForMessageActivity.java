package com.sourabh.baatein;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShowAllFriendsForMessageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_friends_for_message);

        recyclerView = findViewById(R.id.recyclerViewInMessage);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplication());
        recyclerView.setLayoutManager(layoutManager);


        adapter = new ShowAllFriendsForMessageAdapter(getDataset(),getApplication());

        recyclerView.setAdapter(adapter);

        recyclerView.getRecycledViewPool().clear();
        adapter.notifyDataSetChanged();

    }


    private void listenForData() {

        String myId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        DatabaseReference friendsDB = FirebaseDatabase.getInstance().getReference("Friends").child(myId);
        final DatabaseReference userDB = FirebaseDatabase.getInstance().getReference("User");

        friendsDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                        if (dataSnapshot.exists()){

                            String userId = dataSnapshot.getRef().getKey();

                            assert userId != null;
                            userDB.child(userId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    if (snapshot.exists()) {
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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public ArrayList<User> results = new ArrayList<>();

    public ArrayList<User> getDataset() {
        listenForData();
        return results;
    }

}