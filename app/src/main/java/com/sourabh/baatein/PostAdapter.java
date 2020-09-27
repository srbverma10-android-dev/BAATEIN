package com.sourabh.baatein;

import android.content.Context;
import android.os.Handler;
import android.text.method.SingleLineTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.logging.LogRecord;

public class PostAdapter extends RecyclerView.Adapter<PostViewHolder> {

    public PostAdapter(List<Post> postList, Context context) {
        this.postList = postList;
        this.context = context;
    }

    private List<Post> postList;
    private Context context;
    private Animation goback;


    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.postitem, parent,false);
        return new PostViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull final PostViewHolder holder, final int position) {

        goback = AnimationUtils.loadAnimation(context,R.anim.gobackformoreoption);

        final String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference databaseReferenceToUser = FirebaseDatabase.getInstance().getReference("User")
                .child(postList.get(position).getIdOfOtherUser());
        databaseReferenceToUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    String nameOfOtherUser = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                    String imageOfOtherUser = snapshot.child("imageUrl").getValue().toString();

                    try {
                        Glide.with(context).load(imageOfOtherUser)
                                .circleCrop().into(holder.profileImageInPost);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    holder.nameInPost.setText(nameOfOtherUser);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        try {
            Glide.with(context).load(postList.get(position).getPostOfOtherUser()).into(holder.post);
        } catch (Exception e) {
            e.printStackTrace();
        }

        DatabaseReference databaseReferenceToPost = FirebaseDatabase.getInstance().getReference("POSTS")
                .child(postList.get(position).getPostId());
        databaseReferenceToPost.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    if (!snapshot.child("caption").getValue().toString().equals("")) {
                        holder.captionInPost.setVisibility(View.VISIBLE);
                        holder.captionInPost.setText(snapshot.child("caption").getValue().toString());
                    } else {
                        holder.captionInPost.setVisibility(View.INVISIBLE);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final DatabaseReference databaseReferenceToLike = FirebaseDatabase.getInstance().getReference("POSTS")
                        .child(postList.get(position).getPostId()).child("Likes");
                databaseReferenceToLike.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()){

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                                if (dataSnapshot.getValue().toString().equals(myId)){
                                    holder.like.setVisibility(View.INVISIBLE);
                                } else {

                                    databaseReferenceToLike.child(myId).setValue(myId).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            holder.like.setAnimation(goback);
                                            holder.like.setVisibility(View.INVISIBLE);

                                        }
                                    });

                                }

                            }

                        } else {

                            databaseReferenceToLike.child(myId).setValue(myId).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    holder.like.setAnimation(goback);
                                    holder.like.setVisibility(View.INVISIBLE);

                                }
                            });

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });



    }

    @Override
    public int getItemCount() {
        return postList.size();
    }



}
