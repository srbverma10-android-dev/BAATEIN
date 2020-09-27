package com.sourabh.baatein;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessageViewHolder extends RecyclerView.ViewHolder {

    public TextView messages_by_me,messages_by_other;

    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);

        messages_by_me = itemView.findViewById(R.id.messages_by_me);
        messages_by_other =itemView.findViewById(R.id.messages_by_other);

    }
}
