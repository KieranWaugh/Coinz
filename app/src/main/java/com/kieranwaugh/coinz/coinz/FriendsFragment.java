package com.kieranwaugh.coinz.coinz;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;


public class FriendsFragment extends android.support.v4.app.Fragment {
    private String email = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail(); // Current users email
    private String tag = "FriendsFragment"; // tag for log
    private List<String> friendNames = new ArrayList<>(); // name list for players friends
    private List<String> friendsEmails = new ArrayList<>(); // email list for players friends
    private List<Integer> profilePics = new ArrayList<>(); // profile picture for friend
    private String[] names; // array for friends names to be displayed in list view
    private int[] pics; // array for friends profile pictures to be displayed in list view
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance(); // initialises fireBase firestore database

    // newInstance constructor for creating fragment with no arguments
    public static FriendsFragment newInstance() {
        FriendsFragment fragmentFirst = new FriendsFragment();
        Bundle args = new Bundle();


        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        friendNames.add(0, "New Friend"); // initialises the first list view element to add a new friend
        profilePics.add(0, R.drawable.ic_icons8_plus); // adds a image to list view

        //https://www.mkyong.com/android/android-listview-example/




    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false); // attaches the fragment to the activity

        CollectionReference cr = rootRef.collection("user").document(email).collection("Friends"); // path to players friends
        cr.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    friendNames.add(Objects.requireNonNull(document.get("name")).toString()); // gets name of friend
                    friendsEmails.add(Objects.requireNonNull(document.get("email")).toString()); // gets friends email
                    int id = Integer.parseInt(Objects.requireNonNull(document.get("iconID")).toString()); // gets profile pic id
                    switch (id) {
                        case 1:
                            profilePics.add(R.mipmap.default_user_icon_round); // if friends icon id is 1 it has not been changed since initialisation
                            break;
                        case 2:
                            profilePics.add(R.mipmap.icon_2_round); // if id is 2 then display pic 2
                            break;
                        case 3:
                            profilePics.add(R.mipmap.icon_3_round); // if id is 3 then display pic 3
                            break;
                        case 4:
                            profilePics.add(R.mipmap.icon_4_round); // if id is 4 then display pic 4
                            break;
                        case 5:
                            profilePics.add(R.mipmap.icon_5_round); // if id is 5 then display pic 5
                            break;
                    }

                }

            }
            names = friendNames.toArray(new String[0]);
            pics = profilePics.stream().mapToInt(i -> i).toArray();
            ListView li = view.findViewById(R.id.listViewPassword);
            li.setAdapter(new FriendsAdapter(getActivity(), R.layout.fragment_friends, names));

            li.setOnItemClickListener((parent, view1, position, id) -> {


                Log.d("FriendsFragment", "id is " + position);
                if (position == 0) {
                    Log.d(tag, "id in 0");
                    final String[] newEmail = new String[1];
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Enter Email");
// I'm using fragment here so I'm using getView() to provide ViewGroup
// but you can provide here any other instance of ViewGroup from your Fragment / Activity
                    View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.email_input, (ViewGroup) getView(), false);
// Set up the input
                    final EditText input = viewInflated.findViewById(R.id.input);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    builder.setView(viewInflated);

// Set up the buttons
                    builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {

                        newEmail[0] = input.getText().toString();
                        Log.d(tag, "email is " +  newEmail[0]);
                        final User[] newFriend = new User[1];
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        if (!newEmail[0].equals("")) {

                            CollectionReference cr1 = db.collection("user").document(newEmail[0]).collection("INFO");
                            cr1.get().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    for (DocumentSnapshot document : Objects.requireNonNull(task1.getResult())) {
                                        newFriend[0] = document.toObject(User.class);
                                    }
                                    if (newFriend[0] != null) {
                                        if (!friendsEmails.contains(newEmail[0])){
                                            db.collection("user").document(email).collection("Friends").add(newFriend[0]);
                                            Intent i = new Intent(getApplicationContext(), Test_Player_Activity.class);
                                            ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
                                            startActivity(i, options.toBundle());
                                            //FragmentTransaction ft = getFragmentManager().beginTransaction();
                                            //ft.detach(FriendsFragment.this).attach(FriendsFragment.this).commit();
                                        }else{
                                            Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(R.id.viewSnack), "You are already friends!",Snackbar.LENGTH_SHORT).show();
                                        }

                                    }
                                    else {
                                        Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(R.id.viewSnack), "Email does not exist, Try again!",Snackbar.LENGTH_SHORT).show();

                                        Log.d(tag, "cant add friend");

                                    }
                                }
//                                    Intent i = new Intent(getApplicationContext(), Test_Player_Activity.class);
//                                    ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
//                                    startActivity(i, options.toBundle());
//                                    FragmentTransaction ft = getFragmentManager().beginTransaction();
//                                    ft.detach(FriendsFragment.this).attach(FriendsFragment.this).commit();
                            });
                            builder.setNegativeButton(android.R.string.cancel, (dialog1, which1) -> dialog1.cancel());

                        }
                        dialog.cancel();
                    });

                    builder.show();
                }else{

                    Intent i = new Intent(getActivity(), BankActivity.class);
                    ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
                    startActivity(i, options.toBundle());
                }

            });
        });
        return view;
    }


    class FriendsAdapter extends ArrayAdapter {
        FriendsAdapter(Context context, int resource, String[] objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View v=((Activity)getContext()).getLayoutInflater().inflate(R.layout.friend_layoyt,null);
            TextView txt1 = v.findViewById(R.id.textViewpasslay);
            txt1.setText(names[position]);
            ImageView img = v.findViewById(R.id.imageViewpasslay);

            img.setBackgroundResource(pics[position]);


            return v;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(tag, "onResume");

    }
    @Override
    public void onStart(){
        super.onStart();
        Log.d(tag, "onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }





}

