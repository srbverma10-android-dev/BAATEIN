package com.sourabh.baatein;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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


public class FindFriendsAfterLoginAdapter extends RecyclerView.Adapter<FindFriendsAfterLoginViewHolder> {

    public FindFriendsAfterLoginAdapter(List<User> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }

    private List<User> userList;
    private Context context;
    private String myId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private String currentState = "not_friends";

    //private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");

    @NonNull
    @Override
    public FindFriendsAfterLoginViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.findfriendsitem, parent,false);
        return new FindFriendsAfterLoginViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull final FindFriendsAfterLoginViewHolder holder, final int position) {

        holder.name.setText(userList.get(holder.getLayoutPosition()).getName());
        holder.email.setText(userList.get(holder.getLayoutPosition()).getEmail());
        holder.followButton.setText(R.string.follow);

        try {
            Picasso.get()
                    .load(userList.get(holder.getLayoutPosition()).getImageUrl())
                    .centerCrop()
                    .resize(40,40)
                    .into(holder.profileImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

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

        checkUser(userList,position,holder);

        holder.followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentState.equals("friends")){
                    final DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Friends");
                    databaseReference1.child(myId).child(userList.get(position).getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            databaseReference1.child(userList.get(position).getId()).child(myId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    currentState = "not_friends";
                                    holder.followButton.setText(R.string.follow);

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

                    if (currentState.equals("RequestReceived")){

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

                                                                currentState = "friends";
                                                                holder.followButton.setText(R.string.following);

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
                            }
                        });

                    }

                //--------------------------NOT FRIENDS STATE ------------------------------------

                if (currentState.equals("not_friends")) {
                    DatabaseReference databaseReferenceAsSender = FirebaseDatabase.getInstance().getReference("FriendRequest")
                            .child(userList.get(position).getId()).child(myId).child("RequestType");
                    databaseReferenceAsSender.setValue("Received").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                DatabaseReference databaseReferenceAsReceiver = FirebaseDatabase.getInstance().getReference("FriendRequest")
                                        .child(myId).child(userList.get(position).getId()).child("RequestType");
                                databaseReferenceAsReceiver.setValue("Sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        holder.followButton.setText(R.string.requestsend);
                                        currentState = "request_sent";

                                    }
                                });
                            } else {
                                Toast.makeText(context,"FAILED", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }


                //--------------------------REQUEST SENT STATE ------------------------------------

                if (currentState.equals("request_sent")){
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

                                    currentState = "not_friends";
                                    holder.followButton.setText(R.string.follow);

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


            }
        });




    }



    private void checkUser(final List<User> userList, final int position, final FindFriendsAfterLoginViewHolder holder) {

        final String userid = userList.get(position).getId();

        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("FriendRequest")
                .child(myId).child(userid);
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (Objects.requireNonNull(snapshot.child("RequestType").getValue()).toString().equals("Sent")){
                        holder.followButton.setText(R.string.requestsend);
                        currentState = "request_sent";
                    } else if (Objects.requireNonNull(snapshot.child("RequestType").getValue()).toString().equals("Received")){
                        holder.followButton.setText(R.string.confirm);
                        currentState = "RequestReceived";
                    } else {
                        holder.followButton.setText(R.string.follow);
                        currentState = "not_friends";
                    }
                } else {

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Friends").child(myId);
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                if (snapshot.child(userid).exists()){
                                    holder.followButton.setText(R.string.following);
                                    currentState = "friends";
                                } else {
                                    holder.followButton.setText(R.string.follow);
                                    currentState = "not_friends";

                                }
                            } else {
                                holder.followButton.setText(R.string.follow);
                                currentState = "not_friends";
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }



    @Override
    public int getItemCount() {
        return userList.size();
    }
}
