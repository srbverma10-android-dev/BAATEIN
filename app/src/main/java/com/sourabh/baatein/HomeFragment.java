package com.sourabh.baatein;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment {

    public static final String CHANNEL_ID = "sourabh.baatein";
    private static final int PICK_IMAGE_REQUEST = 1;

    //RecyclerView
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private Uri newImageUri;
    private String myid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

    private List<String> listOfMyFriends;
    private List<Post> listOfPost;

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home,container,false);

        //Initialization
        FloatingActionButton floatingActionButton = view.findViewById(R.id.floating);
        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(layoutManager);

        //---------------------------------------------------------

        listOfMyFriends = new ArrayList<>();
        listOfPost = new ArrayList<>();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getContext(),R.style.AlertDialog));
                builder.setMessage("WHERE YOU WANT TO UPLOAD IMAGE???");
                builder.setCancelable(true);

                builder.setPositiveButton("STORY", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Toast.makeText(getContext(),"STORY WILL BE ADDED SOON", Toast.LENGTH_SHORT).show();

                    }
                });

                builder.setNegativeButton("POST", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_PICK);
                        startActivityForResult(intent, PICK_IMAGE_REQUEST);

                    }
                });

                AlertDialog alertDialog = builder.create();

                alertDialog.show();

            }
        });

        DatabaseReference databaseReferenceToFriends = FirebaseDatabase.getInstance().getReference("Friends")
                .child(myid);
        databaseReferenceToFriends.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                        listOfMyFriends.add(dataSnapshot.getRef().getKey());

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        DatabaseReference databaseReferenceToPost = FirebaseDatabase.getInstance().getReference("POSTS");

        databaseReferenceToPost.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                        if (listOfMyFriends.contains(Objects.requireNonNull(snapshot.child("from").getValue()).toString()) ||
                                myid.equals(Objects.requireNonNull(snapshot.child("from").getValue()).toString())) {
                            String image = Objects.requireNonNull(snapshot.child("image").getValue()).toString();
                            String id = Objects.requireNonNull(snapshot.child("from").getValue()).toString();
                            String postId = Objects.requireNonNull(snapshot.child("postId").getValue()).toString();

                            Post post = new Post(id,image,postId);

                            listOfPost.add(0,post);
                            adapter.notifyDataSetChanged();
                        }

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        adapter = new PostAdapter(listOfPost,getContext());

        recyclerView.setAdapter(adapter);

        recyclerView.getRecycledViewPool().clear();
        adapter.notifyDataSetChanged();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            newImageUri = data.getData();

            String newImageString = newImageUri.toString();

            Intent intent1 = new Intent(getContext(),ShowBeforeUploadActivity.class);
            intent1.putExtra("image", newImageString);
            startActivity(intent1);

        }



    }





}