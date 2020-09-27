package com.sourabh.baatein;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

public class ShowAllPostsActivity extends AppCompatActivity {

    //RecyclerView
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_posts);

        recyclerView = findViewById(R.id.recyclerView);


        //RecyclerView
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplication());
        recyclerView.setLayoutManager(layoutManager);



        recyclerView.setAdapter(adapter);

        recyclerView.getRecycledViewPool().clear();
        adapter.notifyDataSetChanged();
    }
}