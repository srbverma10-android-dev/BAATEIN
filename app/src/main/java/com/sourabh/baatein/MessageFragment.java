package com.sourabh.baatein;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MessageFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private LinearLayoutManager linearLayoutManager;

    private List<String> conversationList;
    private List<User> mUser;

    private String myid;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    public MessageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_message,container,false);

        conversationList = new ArrayList<>();

        //Initialization
        FloatingActionButton floatingActionButton = view.findViewById(R.id.floatingInMessage);
        recyclerView = view.findViewById(R.id.recyclerViewAtFragmentMessage);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        myid = firebaseUser.getUid();
        databaseReference = firebaseDatabase.getReference();

        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(false);
        recyclerView.setLayoutManager(linearLayoutManager);

        //-------------------------------------------

        databaseReference.child("Messages").child(myid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                conversationList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    conversationList.add(dataSnapshot.getRef().getKey());
                }


                chatList();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(),ShowAllFriendsForMessageActivity.class);
                startActivity(intent);

            }
        });

        return view;
    }

    private void chatList() {
        mUser = new ArrayList<>();

        databaseReference.child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUser.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    if (dataSnapshot.exists()) {
                        String name = dataSnapshot.child("name").getValue().toString();
                        String email = dataSnapshot.child("email").getValue().toString();
                        String photourl = dataSnapshot.child("imageUrl").getValue().toString();
                        String id = dataSnapshot.getRef().getKey();
                        String token = null;
                        if (dataSnapshot.child("token").exists()) {
                            token = dataSnapshot.child("token").getValue().toString();
                        }

                        User newMessages = new User(name,id,email,photourl,token);

                        //NewMessages newMessages = dataSnapshot.getValue(NewMessages.class);
                        if (conversationList.contains(newMessages.getId())){
                            mUser.add(0,newMessages);
                        }
                    }
                }

                //moveUnseenMessagestoTop(mUser);

                adapter = new MessageFragmentAdapter(getContext(),mUser);
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}