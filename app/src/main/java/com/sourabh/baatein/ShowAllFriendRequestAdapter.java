package com.sourabh.baatein;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ShowAllFriendRequestAdapter extends RecyclerView.Adapter<ShowAllFriendRequestViewHolder> {

    public ShowAllFriendRequestAdapter(List<User> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }

    private List<User> userList;
    private Context context;

    private int decline = 0;

    private Animation bottomAnim, gobackAnim;
    private String myId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

    @NonNull
    @Override
    public ShowAllFriendRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.findfriendsitem, parent,false);
        return new ShowAllFriendRequestViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ShowAllFriendRequestViewHolder holder, final int position) {

        bottomAnim = AnimationUtils.loadAnimation(context, R.anim.bottomanimformoreoption);
        gobackAnim = AnimationUtils.loadAnimation(context, R.anim.gobackformoreoption);

        holder.name.setText(userList.get(position).getName());
        holder.email.setText(userList.get(position).getEmail());
        try {
            Picasso.get()
                    .load(userList.get(holder.getLayoutPosition()).getImageUrl())
                    .centerCrop()
                    .resize(40,40)
                    .into(holder.profileImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.followButton.setText(R.string.confirm);
        holder.followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.followButton.setText(R.string.processing);

                final String currentDateAndTime = DateFormat.getDateTimeInstance().format(new Date());

                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Friends");
                reference.child(myId)
                        .child(userList.get(position).getId())
                        .setValue(currentDateAndTime)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                reference.child(userList.get(position).getId()).child(myId).setValue(currentDateAndTime)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("FriendRequest")
                                                        .child(myId).child(userList.get(position).getId());
                                                databaseReference1.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("FriendRequest")
                                                                .child(userList.get(position).getId()).child(myId);
                                                        databaseReference2.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {

                                                                userList.remove(position);
                                                                notifyItemRemoved(position);

                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {

                                                            }
                                                        });
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {

                                                    }
                                                });

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context,"Failed", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context,"Failed", Toast.LENGTH_SHORT).show();
                        holder.followButton.setText(R.string.confirm);
                    }
                });

            }
        });

        holder.showAllFriendRequestForMoreOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowAllFriendRequestActivity.forShowMore.setVisibility(View.VISIBLE);

                ShowAllFriendRequestActivity.imageView.setAnimation(bottomAnim);
                ShowAllFriendRequestActivity.imageView.setVisibility(View.VISIBLE);

                try {
                    Glide.with(context)
                            .load(userList.get(position).getImageUrl())
                            .circleCrop()
                            .into(ShowAllFriendRequestActivity.profileImageProfileBig);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                ShowAllFriendRequestActivity.profileImageProfileBig.setVisibility(View.VISIBLE);
                ShowAllFriendRequestActivity.profileImageProfileBig.setAnimation(bottomAnim);

                ShowAllFriendRequestActivity.nameShow.setVisibility(View.VISIBLE);
                ShowAllFriendRequestActivity.nameShow.setAnimation(bottomAnim);
                ShowAllFriendRequestActivity.nameShow.setText(userList.get(position).getName());

                ShowAllFriendRequestActivity.emailShow.setVisibility(View.VISIBLE);
                ShowAllFriendRequestActivity.emailShow.setAnimation(bottomAnim);
                ShowAllFriendRequestActivity.nameShow.setText(userList.get(position).getEmail());

                ShowAllFriendRequestActivity.declineRequest.setVisibility(View.VISIBLE);
                ShowAllFriendRequestActivity.declineRequest.setAnimation(bottomAnim);
                ShowAllFriendRequestActivity.declineRequest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ShowAllFriendRequestActivity.declineRequest.setText(R.string.processing);
                        final DatabaseReference declineRequest = FirebaseDatabase.getInstance().getReference("FriendRequest")
                                .child(myId).child(userList.get(position).getId());
                        declineRequest.child("RequestType").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    if (Objects.equals(snapshot.getValue(), "Received")){
                                        declineRequest.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                ShowAllFriendRequestActivity.declineRequest.setText("Declined");
                                                userList.remove(position);
                                                notifyItemRemoved(position);

                                                //---------------------------------

                                                ShowAllFriendRequestActivity.forShowMore.setVisibility(View.INVISIBLE);

                                                ShowAllFriendRequestActivity.imageView.setAnimation(bottomAnim);
                                                ShowAllFriendRequestActivity.imageView.setVisibility(View.INVISIBLE);

                                                ShowAllFriendRequestActivity.profileImageProfileBig.setAnimation(bottomAnim);
                                                ShowAllFriendRequestActivity.profileImageProfileBig.setVisibility(View.INVISIBLE);

                                                ShowAllFriendRequestActivity.nameShow.setAnimation(bottomAnim);
                                                ShowAllFriendRequestActivity.nameShow.setVisibility(View.INVISIBLE);

                                                ShowAllFriendRequestActivity.emailShow.setAnimation(bottomAnim);
                                                ShowAllFriendRequestActivity.emailShow.setVisibility(View.INVISIBLE);

                                                ShowAllFriendRequestActivity.declineRequest.setAnimation(bottomAnim);
                                                ShowAllFriendRequestActivity.declineRequest.setVisibility(View.INVISIBLE);

                                                ShowAllFriendRequestActivity.showProfile.setAnimation(bottomAnim);
                                                ShowAllFriendRequestActivity.showProfile.setVisibility(View.INVISIBLE);

                                                ShowAllFriendRequestActivity.reportUser.setAnimation(bottomAnim);
                                                ShowAllFriendRequestActivity.reportUser.setVisibility(View.INVISIBLE);

                                                //--------------------------------
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(context,"Failed", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }
                });

                ShowAllFriendRequestActivity.showProfile.setVisibility(View.VISIBLE);
                ShowAllFriendRequestActivity.showProfile.setAnimation(bottomAnim);
                ShowAllFriendRequestActivity.showProfile.setOnClickListener(new View.OnClickListener() {
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

                ShowAllFriendRequestActivity.reportUser.setVisibility(View.VISIBLE);
                ShowAllFriendRequestActivity.reportUser.setAnimation(bottomAnim);
                ShowAllFriendRequestActivity.reportUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ShowAllFriendRequestActivity.reportUser.setText("Processing");

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Reports")
                                .child(userList.get(position).getId()).child(myId);
                        databaseReference.setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                final DatabaseReference declineRequest = FirebaseDatabase.getInstance().getReference("FriendRequest")
                                        .child(myId).child(userList.get(position).getId());
                                declineRequest.child("RequestType").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()){
                                            if (Objects.equals(snapshot.getValue(), "Received")){
                                                declineRequest.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        ShowAllFriendRequestActivity.reportUser.setText("REPORT USER");
                                                        userList.remove(position);
                                                        notifyItemRemoved(position);

                                                        //---------------------------------

                                                        ShowAllFriendRequestActivity.forShowMore.setVisibility(View.INVISIBLE);

                                                        ShowAllFriendRequestActivity.imageView.setAnimation(bottomAnim);
                                                        ShowAllFriendRequestActivity.imageView.setVisibility(View.INVISIBLE);

                                                        ShowAllFriendRequestActivity.profileImageProfileBig.setAnimation(bottomAnim);
                                                        ShowAllFriendRequestActivity.profileImageProfileBig.setVisibility(View.INVISIBLE);

                                                        ShowAllFriendRequestActivity.nameShow.setAnimation(bottomAnim);
                                                        ShowAllFriendRequestActivity.nameShow.setVisibility(View.INVISIBLE);

                                                        ShowAllFriendRequestActivity.emailShow.setAnimation(bottomAnim);
                                                        ShowAllFriendRequestActivity.emailShow.setVisibility(View.INVISIBLE);

                                                        ShowAllFriendRequestActivity.declineRequest.setAnimation(bottomAnim);
                                                        ShowAllFriendRequestActivity.declineRequest.setVisibility(View.INVISIBLE);

                                                        ShowAllFriendRequestActivity.showProfile.setAnimation(bottomAnim);
                                                        ShowAllFriendRequestActivity.showProfile.setVisibility(View.INVISIBLE);

                                                        ShowAllFriendRequestActivity.reportUser.setAnimation(bottomAnim);
                                                        ShowAllFriendRequestActivity.reportUser.setVisibility(View.INVISIBLE);

                                                        //--------------------------------
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(context,"Failed", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });

                            }
                        });
                    }
                });

            }
        });

        ShowAllFriendRequestActivity.forShowMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ShowAllFriendRequestActivity.forShowMore.setVisibility(View.INVISIBLE);

                ShowAllFriendRequestActivity.imageView.startAnimation(gobackAnim);
                ShowAllFriendRequestActivity.imageView.setVisibility(View.INVISIBLE);

                ShowAllFriendRequestActivity.nameShow.setAnimation(gobackAnim);
                ShowAllFriendRequestActivity.nameShow.setVisibility(View.INVISIBLE);

                ShowAllFriendRequestActivity.emailShow.setAnimation(gobackAnim);
                ShowAllFriendRequestActivity.emailShow.setVisibility(View.INVISIBLE);

                ShowAllFriendRequestActivity.declineRequest.setAnimation(gobackAnim);
                ShowAllFriendRequestActivity.declineRequest.setVisibility(View.INVISIBLE);

                ShowAllFriendRequestActivity.showProfile.setAnimation(gobackAnim);
                ShowAllFriendRequestActivity.showProfile.setVisibility(View.INVISIBLE);

                ShowAllFriendRequestActivity.reportUser.setAnimation(gobackAnim);
                ShowAllFriendRequestActivity.reportUser.setVisibility(View.INVISIBLE);

                try {
                    ShowAllFriendRequestActivity.profileImageProfileBig.setAnimation(gobackAnim);
                    ShowAllFriendRequestActivity.profileImageProfileBig.setVisibility(View.INVISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }



            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
