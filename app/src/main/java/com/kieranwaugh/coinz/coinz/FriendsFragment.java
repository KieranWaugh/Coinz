package com.kieranwaugh.coinz.coinz;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
    // Store instance variables
    private String email = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    //private int[] image = {R.drawable.ic_icons8_plus};
    private String tag = "FriendsFragment";



    private ListView listView;
    private ArrayList<User> friends = new ArrayList<>();
    private List<String> friendNames = new ArrayList<>();
    private List<String> friendsEmails = new ArrayList<>();
    private List<Integer> profilePics = new ArrayList<>();
    private String[] names;
    private int[] pics;

    // newInstance constructor for creating fragment with arguments
    public static FriendsFragment newInstance( ) {
        FriendsFragment fragmentFirst = new FriendsFragment();
        Bundle args = new Bundle();


        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        friendNames.add(0, "New Friend");
        profilePics.add(0, R.drawable.ic_icons8_plus);

        //friends.add(0, new User("Add Friend", "", ""));
        //https://www.mkyong.com/android/android-listview-example/




    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();

        CollectionReference cr = rootRef.collection("user").document(email).collection("Friends");
        cr.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    friends.add(document.toObject(User.class));
                    friendNames.add(document.get("name").toString());
                    friendsEmails.add(document.get("email").toString());
                    int id = Integer.parseInt(document.get("iconID").toString());
                    switch (id) {
                        case 1:
                            profilePics.add(R.mipmap.default_user_icon_round);
                            break;
                        case 2:
                            profilePics.add(R.mipmap.icon_2_round);
                            break;
                        case 3:
                            profilePics.add(R.mipmap.icon_3_round);
                            break;
                        case 4:
                            profilePics.add(R.mipmap.icon_4_round);
                            break;
                        case 5:
                            profilePics.add(R.mipmap.icon_5_round);
                            break;
                    }

                }
                //profilePics.remove(1);

            }
            //profilePics.remove(1);
            names = friendNames.toArray(new String[0]);
            pics = profilePics.stream().mapToInt(i -> i).toArray();
            ListView li = (ListView) view.findViewById(R.id.listViewPassword);
            li.setAdapter(new FriendsAdapter(getActivity(), R.layout.fragment_friends, names));

            li.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    //Cursor cursor = (Cursor) li.getItemAtPosition(position);
                    //id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
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
                        final EditText input = (EditText) viewInflated.findViewById(R.id.input);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                        builder.setView(viewInflated);

// Set up the buttons
                        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                newEmail[0] = input.getText().toString();
                                Log.d(tag, "email is " +  newEmail[0]);
                                final User[] newFriend = new User[1];
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                if (!newEmail[0].equals("")) {

                                    CollectionReference cr = db.collection("user").document(newEmail[0]).collection("INFO");
                                    cr.get().addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
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
                                                    Snackbar.make(getActivity().findViewById(R.id.viewSnack), "You are already friends!",Snackbar.LENGTH_SHORT).show();
                                                }

                                            }
                                            else {
                                                Snackbar.make(getActivity().findViewById(R.id.viewSnack), "Email does not exist, Try again!",Snackbar.LENGTH_SHORT).show();

                                                Log.d(tag, "cant add friend");

                                            }
                                        }
//                                    Intent i = new Intent(getApplicationContext(), Test_Player_Activity.class);
//                                    ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
//                                    startActivity(i, options.toBundle());
//                                    FragmentTransaction ft = getFragmentManager().beginTransaction();
//                                    ft.detach(FriendsFragment.this).attach(FriendsFragment.this).commit();
                                    });
                                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });

                                }
                                dialog.cancel();
                            }

                        });

                        builder.show();
                    }else{

                        Intent i = new Intent(getActivity(), BankActivity.class);
                        ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
                        startActivity(i, options.toBundle());
                    }

                }
            });
        });
        return view;
    }


    class FriendsAdapter extends ArrayAdapter {

        public FriendsAdapter(Context context, int resource, String[] objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v=((Activity)getContext()).getLayoutInflater().inflate(R.layout.friend_layoyt,null);
            //friendNames.remove(1);
//            profilePics.remove(1);
            TextView txt1 = (TextView) v.findViewById(R.id.textViewpasslay);
            txt1.setText(names[position]);
            ImageView img = (ImageView) v.findViewById(R.id.imageViewpasslay);

            img.setBackgroundResource(pics[position]);


            return v;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(tag, "onResume");
        //getFragmentManager().beginTransaction().detach(this).attach(this).commit();

    }
    @Override
    public void onStart(){
        super.onStart();
        Log.d(tag, "onStart");
        //getFragmentManager().beginTransaction().detach(this).attach(this).commit();

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

