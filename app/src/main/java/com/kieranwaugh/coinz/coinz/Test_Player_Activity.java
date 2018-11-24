package com.kieranwaugh.coinz.coinz;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class Test_Player_Activity extends AppCompatActivity {
    FragmentPagerAdapter adapterViewPager;
    private String email = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
    private User user;
    private TextView name;
    private ImageView profilePic;
    private static PlayerStats stats;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getData();
        //getStats();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test__player);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        Menu menu = bottomNavigationView.getMenu();
        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);
        profilePic = findViewById(R.id.profilePicView);
        name = findViewById(R.id.nameView);
        Intent intent = getIntent();
        stats = (PlayerStats) intent.getSerializableExtra("stats");

        ActivityOptions options1 = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out);
        ActivityOptions options2 = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navigation_stats:

                        break;

                    case R.id.navigation_map:
                        Intent intent2 = new Intent(Test_Player_Activity.this, mapActivity.class);
                        startActivity(intent2, options1.toBundle());
                        break;

                    case R.id.navigation_bank:
                        Intent intent3 = new Intent(Test_Player_Activity.this, bankActivity.class);
                        startActivity(intent3, options1.toBundle());
                        break;
                }
                return false;
            }
        });
    }

    public void getData(){

        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        CollectionReference cr = rootRef.collection("user").document(email).collection("INFO");
        cr.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    user = document.toObject(User.class);
                }
                assert user != null;
                name.setText(user.getName());

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapterViewPager.notifyDataSetChanged();
    }


    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

//    public void getStats(){
//        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
//        CollectionReference cr = rootRef.collection("user").document(email).collection("STATS");
//        cr.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//
//                for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
//                    stats = document.toObject(PlayerStats.class);
//                }
//            }
//        });
//    }


    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 3;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    return FriendsFragment.newInstance();
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    return StatsFragment.newInstance();
                case 2: // Fragment # 1 - This will show SecondFragment
                    return SettingsFragment.newInstance(2, "Page # 3");
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return "Friends";
                case 1:
                    return "Statistics";
                case 2:
                    return  "Settings";
            }
            return "Page " + position;
        }



    }
}


