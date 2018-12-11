package com.kieranwaugh.coinz.coinz;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class PlayerActivity extends AppCompatActivity {
    FragmentPagerAdapter adapterViewPager;
    private String email = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
    private User user;
    private TextView name;
    private ImageView profilePic;
    private String tag = "PlayerActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getData(); // runs the get data function

        //activity layout and element layouts
        setContentView(R.layout.activity_player);
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        ViewPager vpPager = findViewById(R.id.vpPager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);
        profilePic = findViewById(R.id.profilePicView);
        name = findViewById(R.id.nameView);

        ActivityOptions options1 = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> { // creates the bottom navigation bar
            switch (item.getItemId()){
                case R.id.navigation_player:

                    break;

                case R.id.navigation_map:
                    Intent intent2 = new Intent(PlayerActivity.this, MapActivity.class);
                    startActivity(intent2, options1.toBundle());
                    break;

                case R.id.navigation_bank:
                    Intent intent3 = new Intent(PlayerActivity.this, BankActivity.class);
                    startActivity(intent3, options1.toBundle());
                    break;
            }
            return false;
        });

        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        CollectionReference cr = rootRef.collection("user").document(email).collection("INFO");
        cr.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) { // pulls the players profile picture ID from the databse
                    int id = Integer.parseInt(Objects.requireNonNull(document.get("iconID")).toString());
                    switch (id) { // sets the imageview depending on the ID
                        case 2:
                            profilePic.setImageResource(R.mipmap.icon_2_round);
                            break;
                        case 3:
                            profilePic.setImageResource(R.mipmap.icon_3_round);
                            break;
                        case 4:
                            profilePic.setImageResource(R.mipmap.icon_4_round);
                            break;
                        case 5:
                            profilePic.setImageResource(R.mipmap.icon_5_round);
                            break;
                    }

                }
            }
        });

        profilePic.setOnClickListener(v -> { // onclick listener for profile picture
            PopupMenu popup = new PopupMenu(PlayerActivity.this, profilePic);
            popup.getMenuInflater().inflate(R.menu.profile_pic_click, popup.getMenu()); // inflates the drop down menu
            popup.setOnMenuItemClickListener(item -> { // onclick listener for menu item selected
                String sp = item.getTitle().toString();
                switch(sp){
                    case "Sign Out":
                        FirebaseAuth.getInstance().signOut(); // signs the user out
                        startActivity(new Intent(PlayerActivity.this, MainActivity.class));
                        finish();
                        break;
                    case  "Change Profile Picture": // opens the activity to change the users profile  pic
                        startActivity(new Intent(PlayerActivity.this, PicChoice.class));
                }
                return true;
            });
            popup.show(); // show the menu
        });

    }

    public void getData(){ // function to pull the players data from the database

        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        CollectionReference cr = rootRef.collection("user").document(email).collection("INFO");
        cr.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    user = document.toObject(User.class);
                }
                assert user != null;
                name.setText(user.getName()); // sets the users name to display
                Log.d(tag, "[getData] " + user.getName());

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

    @Override
    public void onBackPressed(){ // takes the player back to the tap to play screen
        ActivityOptions options = ActivityOptions.makeCustomAnimation(this, R.anim.bottom_down, R.anim.nothing);
        startActivity(new Intent(PlayerActivity.this, MainActivity.class), options.toBundle());
    }


    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 2;

        MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        } // initialises the fragment display

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
                    return FriendsFragment.newInstance(); // first fragment is friends
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    return StatsFragment.newInstance(); // second fragment is stats
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
            }
            return "Page " + position;
        }



    }

}


