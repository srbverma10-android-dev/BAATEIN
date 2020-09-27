package com.sourabh.baatein;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MessageFragmentAdapter extends RecyclerView.Adapter<MessageFragmentViewHolder> {


    public MessageFragmentAdapter(Context context, List<User> mUser) {
        this.context = context;
        this.mUser = mUser;
    }

    private Context context;
    private List<User> mUser;

    private List<User> itemtomove = new ArrayList<>();

    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private String uid = firebaseUser.getUid();

    @NonNull
    @Override
    public MessageFragmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.findfriendsitem, null);
        MessageFragmentViewHolder rcv = new MessageFragmentViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageFragmentViewHolder holder, final int position) {


        holder.followButton.setVisibility(View.INVISIBLE);
        holder.setIsRecyclable(false);

        newMessages(uid,mUser.get(position).getId(),holder.relativeLayout,holder.name,holder.email,position);

        try {
            Glide.with(context)
                    .load(mUser.get(position).getImageUrl())
                    .centerCrop()
                    .into(holder.profileImage);
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MessageActivity.class);
                    intent.putExtra("id", mUser.get(position).getId());
                    intent.putExtra("name", mUser.get(position).getName());
                    intent.putExtra("email", mUser.get(position).getEmail());
                    intent.putExtra("imageUrl", mUser.get(position).getImageUrl());
                    intent.putExtra("token", mUser.get(position).getToken());

                    context.startActivity(intent);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }




    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }




    public void newMessages(final String myid, String id_ou, final RelativeLayout linearLayout, final TextView name, final TextView email, final int position){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Messages")
                .child(myid).child(id_ou);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if (dataSnapshot.child("seen").getValue().toString().equals("false") && !dataSnapshot.child("sender").getValue().toString().equals(myid)){

                        linearLayout.setBackgroundColor(Color.parseColor("#D1D1D1"));
                        name.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                        name.setText(mUser.get(position).getName());
                        email.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));


                    }else {
                        name.setText(mUser.get(position).getName());
                    }
                    email.setText(mUser.get(position).getEmail());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }




}
