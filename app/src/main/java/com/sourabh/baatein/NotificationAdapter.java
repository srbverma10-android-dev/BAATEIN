package com.sourabh.baatein;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationViewHolder> {


    public NotificationAdapter(List<Notification> listOfNotification, Context context) {
        this.listOfNotification = listOfNotification;
        this.context = context;
    }

    private List<Notification> listOfNotification;
    private Context context;

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notificationitem, parent,false);
        return new NotificationViewHolder(layoutView);    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {

        try {
            Glide.with(context).load(listOfNotification.get(position).getImage())
                    .circleCrop().into(holder.imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.textView.setText("Total Number of Likes in this picture is : " +listOfNotification.get(position).getNum());

    }

    @Override
    public int getItemCount() {
        return listOfNotification.size();
    }
}
