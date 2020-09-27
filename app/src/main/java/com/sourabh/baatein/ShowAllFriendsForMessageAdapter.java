package com.sourabh.baatein;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.INotificationSideChannel;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ShowAllFriendsForMessageAdapter extends RecyclerView.Adapter<ShowAllFriendsForMessageViewHolder> {

    public ShowAllFriendsForMessageAdapter(List<User> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }

    private List<User> userList;
    private Context context;

    @NonNull
    @Override
    public ShowAllFriendsForMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.findfriendsitem, parent,false);
        return new ShowAllFriendsForMessageViewHolder(layoutView);    }

    @Override
    public void onBindViewHolder(@NonNull ShowAllFriendsForMessageViewHolder holder, final int position) {

        try {
            Glide.with(context)
                    .load(userList.get(position).getImageUrl())
                    .centerCrop()
                    .circleCrop()
                    .into(holder.profileImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.name.setText(userList.get(position).getName());
        holder.email.setText(userList.get(position).getEmail());

        holder.followButton.setVisibility(View.INVISIBLE);

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context,MessageActivity.class);
                intent.putExtra("name", userList.get(position).getName());
                intent.putExtra("email", userList.get(position).getEmail());
                intent.putExtra("id", userList.get(position).getId());
                intent.putExtra("imageUrl", userList.get(position).getImageUrl());
                intent.putExtra("token", userList.get(position).getToken());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
