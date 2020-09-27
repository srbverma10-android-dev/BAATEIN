package com.sourabh.baatein;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NotificationViewHolder extends RecyclerView.ViewHolder {

    public ImageView imageView;
    public TextView textView;

    public NotificationViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView = itemView.findViewById(R.id.postInNotification);
        textView = itemView.findViewById(R.id.textViewInNotification);

    }
}
