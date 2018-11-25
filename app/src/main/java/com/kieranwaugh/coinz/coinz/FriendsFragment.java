package com.kieranwaugh.coinz.coinz;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class FriendsFragment extends android.support.v4.app.Fragment {
    // Store instance variables
    private String email = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
    private int[] image = {R.mipmap.default_user_icon_round};


    private ListView listView;
    private List<String> friends = new ArrayList<>();
    private String[] names;

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
        friends.add(0, "New Friend");
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
                    friends.add(document.get("name").toString());
                }

            }
            friends.remove(1);
            names = friends.toArray(new String[0]);
            ListView li=(ListView)view.findViewById(R.id.listViewPassword);
            li.setAdapter(new FriendsAdapter(getActivity(),R.layout.fragment_friends,names));


        });
        return view;


    }

    public void getStats(){

    }

    class FriendsAdapter extends ArrayAdapter {

        public FriendsAdapter(Context context, int resource, String[] objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v=((Activity)getContext()).getLayoutInflater().inflate(R.layout.friend_layoyt,null);
            TextView txt1 = (TextView) v.findViewById(R.id.textViewpasslay);
            txt1.setText(names[position]);
            ImageView img = (ImageView) v.findViewById(R.id.imageViewpasslay);
            img.setBackgroundResource(image[position]);


            return v;
        }
    }

}

