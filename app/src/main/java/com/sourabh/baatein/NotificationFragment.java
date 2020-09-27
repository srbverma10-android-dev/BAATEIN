package com.sourabh.baatein;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NotificationFragment extends Fragment {
    //RecyclerView
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    public NotificationFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_notification,container,false);

        recyclerView = view.findViewById(R.id.recyclerViewInNOtification);

        //RecyclerView
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new NotificationAdapter(getDataset(),getContext());

        recyclerView.setAdapter(adapter);

        recyclerView.getRecycledViewPool().clear();
        adapter.notifyDataSetChanged();

        return view;
    }

    public ArrayList<Notification> results = new ArrayList<>();

    public ArrayList<Notification> getDataset() {
        listenForData();
        return results;
    }

    private void listenForData() {

        results.clear();

        DatabaseReference databaseReferenceToNotification = FirebaseDatabase.getInstance().getReference("POSTS");
        databaseReferenceToNotification.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                        if (dataSnapshot.child("from").getValue().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){

                            String num = String.valueOf(dataSnapshot.child("Likes").getChildrenCount());
                            String image = dataSnapshot.child("image").getValue().toString();

                            Notification notification = new Notification(num,image);
                            results.add(notification);
                            adapter.notifyDataSetChanged();

                        }

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });






    }

}