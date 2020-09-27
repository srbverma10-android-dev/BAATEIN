package com.sourabh.baatein;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import java.util.List;
import java.util.Objects;

public class ShowAllFriendsAdapter extends RecyclerView.Adapter<ShowAllFriendsViewHolder> {

    public ShowAllFriendsAdapter(List<User> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }

    private List<User> userList;
    private Context context;

    private String myId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

    @NonNull
    @Override
    public ShowAllFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.findfriendsitem, parent,false);
        return new ShowAllFriendsViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ShowAllFriendsViewHolder holder, final int position) {

        holder.email.setText(userList.get(position).getEmail());
        holder.name.setText(userList.get(position).getName());

        try {
            Picasso.get()
                    .load(userList.get(holder.getLayoutPosition()).getImageUrl())
                    .centerCrop()
                    .resize(40,40)
                    .into(holder.profileImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.followButton.setText(R.string.following);

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("name", userList.get(position).getName());
                intent.putExtra("email", userList.get(position).getEmail());
                intent.putExtra("id", userList.get(position).getId());
                intent.putExtra("imageUrl", userList.get(position).getImageUrl());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        });

        holder.followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.followButton.setText(R.string.processing);

                final DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Friends");
                databaseReference1.child(myId).child(userList.get(position).getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        databaseReference1.child(userList.get(position).getId()).child(myId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                userList.remove(position);
                                notifyItemRemoved(position);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //Toast.makeText(context,"Processing",Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context,"Failed",Toast.LENGTH_SHORT).show();
                        holder.followButton.setText(R.string.following);
                    }
                });

            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
