package com.sourabh.baatein;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MessageActivity extends AppCompatActivity {

    private ImageView profileImage;
    private TextView name;

    private RecyclerView recyclerView;
    private MessageAdapter adapter;

    private Button send_button;

    private ValueEventListener seenEventListner1,seenEventListner2;

    private String URL = "https://fcm.googleapis.com/fcm/send";

    private RequestQueue requestQueue;

    private EditText type_a_message;

    private LinearLayoutManager linearLayoutManager;

    private List<Chat> mchat_for_sendMessages, mchat_for_receiveMessages;

    private String nameS, emailS, idS, imageUrlS, token_ou, myName;
    private String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        recyclerView = findViewById(R.id.recyclerViewInMessageActivity);

        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        requestQueue = Volley.newRequestQueue(this);

        mchat_for_receiveMessages = new ArrayList<>();
        mchat_for_sendMessages = new ArrayList<>();

        profileImage = findViewById(R.id.backbuttonfindfriendsafterlogin);
        name = findViewById(R.id.nameForMessage);

        send_button = findViewById(R.id.send_button);
        type_a_message = findViewById(R.id.send_message);

        Intent intent = getIntent();
        nameS = intent.getStringExtra("name");
        emailS = intent.getStringExtra("email");
        idS = intent.getStringExtra("id");
        imageUrlS = intent.getStringExtra("imageUrl");
        token_ou = intent.getStringExtra("token");


        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("User").child(uid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    myName = snapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        try {
            Glide.with(getApplicationContext())
                    .load(imageUrlS)
                    .centerCrop()
                    .circleCrop()
                    .into(profileImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        name.setText(nameS);

        receiveMessages(uid,idS);
        seenMessages(uid,idS);

        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = type_a_message.getText().toString();
                if (!msg.equals("")){
                    sendMessage(uid,idS,msg);
                }else {
                    Toast.makeText(MessageActivity.this, "YOU CAN NOT SEND AN EMPTY MESSAGE", Toast.LENGTH_SHORT).show();
                }
                type_a_message.setText("");
            }
        });

        adapter = new MessageAdapter(MessageActivity.this, mchat_for_receiveMessages);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }




    private void receiveMessages(String uid, String id_ou) {

        DatabaseReference tomid = FirebaseDatabase.getInstance().getReference("Messages").child(uid).child(id_ou);
        tomid.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mchat_for_receiveMessages.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    Chat chat = dataSnapshot.getValue(Chat.class);

                    mchat_for_receiveMessages.add(chat);
                    recyclerView.smoothScrollToPosition(adapter.getItemCount());
                    adapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void sendMessage(String uid, final String id_ou, final String msg) {


        DatabaseReference tomyid = FirebaseDatabase.getInstance().getReference("Messages").child(uid).child(id_ou);
        DatabaseReference tootherid = FirebaseDatabase.getInstance().getReference("Messages").child(id_ou).child(uid);

        Chat chat = new Chat(msg,"false", uid,id_ou);

        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("message_body", msg);
        hashMap.put("seen", "false");
        hashMap.put("sender", uid);
        hashMap.put("receiver", id_ou);

        tootherid.push().setValue(hashMap);
        tomyid.push().setValue(hashMap);


        sendNotification(token_ou,myName);

        mchat_for_receiveMessages.add(chat);
        recyclerView.smoothScrollToPosition(adapter.getItemCount());

        adapter.notifyDataSetChanged();
    }



    private void seenMessages(final String uid, String id_ou) {

        seenEventListner1 = FirebaseDatabase.getInstance().getReference().child("Messages").child(uid).child(id_ou).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if (!dataSnapshot.child("sender").getValue().toString().equals(uid)){
                        if (dataSnapshot.child("seen").getValue().toString().equals("false")){
                            HashMap<String , Object> hashMap1 = new HashMap<>();
                            hashMap1.put("seen","true");
                            dataSnapshot.getRef().updateChildren(hashMap1);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        seenEventListner2 = FirebaseDatabase.getInstance().getReference().child("Messages").child(id_ou).child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if (!dataSnapshot.child("sender").getValue().toString().equals(uid)){
                        if (dataSnapshot.child("seen").getValue().toString().equals("false")){
                            HashMap<String , Object> hashMap2 = new HashMap<>();
                            hashMap2.put("seen","true");
                            dataSnapshot.getRef().updateChildren(hashMap2);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void sendNotification(String token_ou, String myName) {


        try {
            JSONObject mainOBJ = new JSONObject();
            mainOBJ.put("to", token_ou);
            JSONObject notificationOBJ = new JSONObject();
            notificationOBJ.put("title", "New Message By :-");
            notificationOBJ.put("body", myName);
            mainOBJ.put("notification", notificationOBJ);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL,
                    mainOBJ,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String , String> header = new HashMap<>();
                    header.put("Content-Type", "application/json");
                    header.put("Authorization", "key=AAAAKebPUfQ:APA91bGH2j7hN-AH6zBOs3MM1m8rTfWpXnzwDZm8wxhXtkcwLOUWZm_6YlLPQPna8tSXG-JSMI8GOEkpKKX9JFLo2sdH3yyEbtI9WEnZLP3lpnrY0Or1qa5TZ8mGAIUEIVElJzl5XUWq");
                    return header;
                }
            };

            requestQueue.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        FirebaseDatabase.getInstance().getReference().child("Messages").child(uid).child(idS).removeEventListener(seenEventListner1);
        FirebaseDatabase.getInstance().getReference().child("Messages").child(idS).child(uid).removeEventListener(seenEventListner2);

    }
}

