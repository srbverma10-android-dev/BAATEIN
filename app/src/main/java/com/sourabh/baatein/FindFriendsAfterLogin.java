package com.sourabh.baatein;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import java.util.ArrayList;
import java.util.Objects;

public class FindFriendsAfterLogin extends AppCompatActivity {

    private TextView next;
    private EditText input;
    private Button searchButton;

    //RecyclerView
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends_after_login);

        initialization();

        //RecyclerView
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplication());
        recyclerView.setLayoutManager(layoutManager);


        adapter = new FindFriendsAfterLoginAdapter(getDataset(),getApplication());

        recyclerView.setAdapter(adapter);

        recyclerView.getRecycledViewPool().clear();
        adapter.notifyDataSetChanged();

        try {
            searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clear();
                    listenForData();
                    recyclerView.getRecycledViewPool().clear();
                    adapter.notifyDataSetChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FindFriendsAfterLogin.this, NewsFeedActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });


    }




    private void listenForData() {

        DatabaseReference userDB = FirebaseDatabase.getInstance().getReference().child("User");
        Query query = userDB.orderByChild("email").startAt(input.getText().toString()).endAt(input.getText().toString() + "\uf8ff");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
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

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void clear() {
        int size = this.results.size();
        this.results.clear();
        adapter.notifyItemRangeChanged(0,size);
    }

    public ArrayList<User> results = new ArrayList<>();

    public ArrayList<User> getDataset() {
        listenForData();
        return results;
    }

    private void initialization() {

        next = findViewById(R.id.next);
        input = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.seatchButton);
        recyclerView = findViewById(R.id.recyclerView);
        //Firebase
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;

    }
}