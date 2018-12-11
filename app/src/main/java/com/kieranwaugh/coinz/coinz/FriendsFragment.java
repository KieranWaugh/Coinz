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

@SuppressWarnings("all") // suppresses a warning about a super constructor on line 181
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
            names = friendNames.toArray(new String[0]); // creates an array of the friends names
            pics = profilePics.stream().mapToInt(i -> i).toArray(); // creates an array of the profile pictures
            ListView li = view.findViewById(R.id.listViewPassword); // creates a list view in the fragment (RecyclerView could have been used ofr material design, however listview looked more 'gamey')
            li.setAdapter(new FriendsAdapter(getActivity(), R.layout.fragment_friends, names)); // sets the listView adapter

            li.setOnItemClickListener((parent, view1, position, id) -> { // on listView click will open the bank


                Log.d("FriendsFragment", "id is " + position);
                if (position == 0) {
                    Log.d(tag, "id in 0");
                    final String[] newEmail = new String[1];
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Enter Email");

                    View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.email_input, (ViewGroup) getView(), false); // Displays the dialog Using fragment, so using getView() to provide ViewGroup

                    final EditText input = viewInflated.findViewById(R.id.input);
                    builder.setView(viewInflated);

                    builder.setPositiveButton(android.R.string.ok, (dialog, which) -> { // Set up the buttons (OK button)

                        newEmail[0] = input.getText().toString(); // gets the inputted email
                        Log.d(tag, "email is " +  newEmail[0]);
                        final User[] newFriend = new User[1]; // creates a user array of size 1 (required by fireBase as its local instead of global)
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        if (!newEmail[0].equals("")) { // Only if the text input is not blank.

                            CollectionReference cr1 = db.collection("user").document(newEmail[0]).collection("INFO"); // getting the new friends info
                            cr1.get().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    for (DocumentSnapshot document : Objects.requireNonNull(task1.getResult())) {
                                        newFriend[0] = document.toObject(User.class); // user object
                                    }
                                    if (newFriend[0] != null) { // player has inputted an email
                                        if (!friendsEmails.contains(newEmail[0])){ // User is not already friends with this email
                                            db.collection("user").document(email).collection("Friends").add(newFriend[0]); // adds user to players friends list
                                            Intent i = new Intent(getApplicationContext(), PlayerActivity.class); // refreshes the activity to update the list from fireBase
                                            ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out); // adds animation to refresh
                                            startActivity(i, options.toBundle()); // starts activity
                                        }else{
                                            Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(R.id.viewSnack), "You are already friends!",Snackbar.LENGTH_SHORT).show();
                                            // Player is already friends with the inputted email
                                        }

                                    }
                                    else {
                                        Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(R.id.viewSnack), "Email does not exist, Try again!",Snackbar.LENGTH_SHORT).show();
                                        // email does not exist within fireBase
                                        Log.d(tag, "cant add friend");

                                    }
                                }
//                                    Intent i = new Intent(getApplicationContext(), PlayerActivity.class);
//                                    ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
//                                    startActivity(i, options.toBundle());
//                                    FragmentTransaction ft = getFragmentManager().beginTransaction();
//                                    ft.detach(FriendsFragment.this).attach(FriendsFragment.this).commit();
                            });
                            builder.setNegativeButton(android.R.string.cancel, (dialog1, which1) -> dialog1.cancel()); // cancel button

                        }
                        dialog.cancel(); // closes dialog
                    });

                    builder.show(); // shows dialog box
                }else{

                    Intent i = new Intent(getActivity(), BankActivity.class); // refresh of the activity
                    ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
                    startActivity(i, options.toBundle());
                }

            });
        });
        return view; // returns the fragments view.
    }


    class FriendsAdapter extends ArrayAdapter { // adapter for the friend list
        FriendsAdapter(Context context, int resource, String[] objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View v=((Activity)getContext()).getLayoutInflater().inflate(R.layout.friend_layout,null); // sets the layout for each list item
            TextView txt1 = v.findViewById(R.id.textViewpasslay); // text layout
            txt1.setText(names[position]); // sets the text from the friends array populated in onCreate
            ImageView img = v.findViewById(R.id.imageViewpasslay); // image layout (profile pic)
            img.setBackgroundResource(pics[position]); // sets the pic from the pics array populated in onCreate


            return v; //returns the view
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

