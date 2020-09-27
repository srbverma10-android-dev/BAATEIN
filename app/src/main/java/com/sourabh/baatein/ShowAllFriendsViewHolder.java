package com.sourabh.baatein;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ShowAllFriendsViewHolder extends RecyclerView.ViewHolder {

    public ImageView profileImage;
    public TextView name, email;
    public Button followButton;
    public RelativeLayout relativeLayout;

    public ShowAllFriendsViewHolder(@NonNull View itemView) {
        super(itemView);

        profileImage = itemView.findViewById(R.id.profileImage);
        name = itemView.findViewById(R.id.name);
        email = itemView.findViewById(R.id.email);
        followButton = itemView.findViewById(R.id.followButton);
        relativeLayout = itemView.findViewById(R.id.relativeLayoutForMoreOptions);

    }
}
