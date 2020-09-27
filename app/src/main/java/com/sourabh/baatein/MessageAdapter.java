package com.sourabh.baatein;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageViewHolder> {


    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String myid;

    public MessageAdapter(Context context, List<Chat> mchat) {
        this.context = context;
        this.mchat = mchat;
    }

    private Context context;
    private List<Chat> mchat;

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(context).inflate(R.layout.chat_item, null);
        MessageViewHolder rcv = new MessageViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        myid = firebaseUser.getUid();

        holder.messages_by_other.setVisibility(View.INVISIBLE);
        holder.messages_by_me.setVisibility(View.INVISIBLE);

        if (mchat.get(position).getSender().equals(myid)){
            holder.messages_by_me.setVisibility(View.VISIBLE);
            holder.messages_by_me.setText(mchat.get(position).getMessage_body());
        }else if (mchat.get(position).getReceiver().equals(myid)){
            holder.messages_by_other.setVisibility(View.VISIBLE);
            holder.messages_by_other.setText(mchat.get(position).getMessage_body());
        }else {
            holder.messages_by_other.setVisibility(View.INVISIBLE);
            holder.messages_by_me.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return mchat.size();
    }
}
