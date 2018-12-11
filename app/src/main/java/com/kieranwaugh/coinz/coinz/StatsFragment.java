package com.kieranwaugh.coinz.coinz;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class StatsFragment extends android.support.v4.app.Fragment {
    private PlayerStats playerStats;
    private TextView statView;
    private String email = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();

    public StatsFragment() {
    }


    // newInstance constructor for creating fragment with arguments
    public static StatsFragment newInstance() {
        StatsFragment fragmentStats = new StatsFragment();
        Bundle args = new Bundle();
        fragmentStats.setArguments(args);
        return fragmentStats;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        getStats();
        super.onCreate(savedInstanceState);
        assert getArguments() != null;
        playerStats = (PlayerStats) getArguments().getSerializable("stats");
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false); // attaches the fragment to the activity
        statView = view.findViewById(R.id.statsView); // sets fragment layout
        return view;
    }

    public void getStats(){ // function to pull the players stats from firebase
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        CollectionReference cr = rootRef.collection("user").document(email).collection("STATS");
        cr.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    playerStats = document.toObject(PlayerStats.class); // re-creates the stats object for the player
                }

                assert playerStats != null;
                Log.d("StatsFragment", "[getStats] " + playerStats.getDolrs());
            }

            statView.setText("Total Distance Walked: " + playerStats.getDistance() + " metres\nDOLR: " + playerStats.getDolrs() +
                    "\nQUID: " + playerStats.getQuids() + "\nPENY: " + playerStats.getPenys() + "\nSHIL: " + playerStats.getShils());
        }); // updates the view with the players stats
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Refresh your fragment here
            assert getFragmentManager() != null;
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
            Log.i("IsRefresh", "Yes");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getStats();
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
