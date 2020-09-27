package com.sourabh.baatein;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NewsFeedActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;

    private NotificationFragment notificationFragment;
    private SearchFragment searchFragment;
    private HomeFragment homeFragment;
    private MessageFragment messageFragment;
    private ProfileFragment profileFragment;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed);

        initialization();

        tabLayout.setupWithViewPager(viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);

        viewPagerAdapter.addFragment(notificationFragment);
        viewPagerAdapter.addFragment(searchFragment);
        viewPagerAdapter.addFragment(homeFragment);
        viewPagerAdapter.addFragment(messageFragment);
        viewPagerAdapter.addFragment(profileFragment);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(2);


        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.notnotification);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.notsearch);
        Objects.requireNonNull(tabLayout.getTabAt(2)).setIcon(R.drawable.nothome);
        Objects.requireNonNull(tabLayout.getTabAt(3)).setIcon(R.drawable.notmessage);
        Objects.requireNonNull(tabLayout.getTabAt(4)).setIcon(R.drawable.profile);


        final BadgeDrawable badgeDrawable = tabLayout.getTabAt(4).getOrCreateBadge();
        badgeDrawable.setVisible(false);
        DatabaseReference showOrNot = FirebaseDatabase.getInstance().getReference("FriendRequest")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        showOrNot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        if (dataSnapshot.child("RequestType").getValue().toString().equals("Received")){
                            badgeDrawable.setVisible(true);
                        }else {
                            badgeDrawable.setVisible(false);
                        }
                    }
                } else {
                    badgeDrawable.setVisible(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void initialization() {

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        notificationFragment = new NotificationFragment();
        searchFragment = new SearchFragment();
        homeFragment = new HomeFragment();
        messageFragment = new MessageFragment();
        profileFragment = new ProfileFragment();

    }

    private static class ViewPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragmentList = new ArrayList<>();

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        public void addFragment(Fragment fragment){
            fragmentList.add(fragment);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

    }

}