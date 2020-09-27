package com.sourabh.baatein;

import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class PostViewHolder extends RecyclerView.ViewHolder {

    public ImageView profileImageInPost;
    public ImageView post;
    public TextView nameInPost;
    public ImageView like;
    public TextView captionInPost;


    public PostViewHolder(@NonNull View itemView) {
        super(itemView);

        profileImageInPost = itemView.findViewById(R.id.profileImageInPost);
        post = itemView.findViewById(R.id.post);
        nameInPost = itemView.findViewById(R.id.nameInPost);
        like = itemView.findViewById(R.id.likeButton);
        captionInPost  = itemView.findViewById(R.id.captionInPost);

    }

}
