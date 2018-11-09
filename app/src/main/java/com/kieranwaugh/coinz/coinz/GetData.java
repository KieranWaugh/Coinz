package com.kieranwaugh.coinz.coinz;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class GetData {

    String tag = "GetData";
    String dateDB = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    String UID = FirebaseAuth.getInstance().getUid();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<Double> result = new ArrayList<Double>(3); // 0 - SHIL, 1 - QUID, 2- DOLR, 3 - PENY

    public ArrayList getCoinsTotal(){

        result.add(0,0.0);
        result.add(1,0.0);
        result.add(2,0.0);
        result.add(3,0.0);
        db.collection("wallet").document(UID).collection("collected ("+dateDB +")").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (e == null){
                    for (DocumentChange documentChange : documentSnapshots.getDocumentChanges()) {
                        String currency =  documentChange.getDocument().getData().get("currency").toString();
                        double value =  Double.parseDouble(documentChange.getDocument().getData().get("value").toString());
                        //Log.d(tag, "[getCollected] " + currency + " " + value + " " + result.get(2));

                        switch (currency){
                            case "SHIL":
                                double tmp = result.get(0);
                                result.set(0, tmp + value);
                            case "QUID":
                                double tmp1 = result.get(1);
                                Log.d(tag, "before " + tmp1);
                                result.set(1, tmp1 + value);
                                Log.d(tag, "after " + result.get(1));
                                Log.d(tag, "running total QUID " + result.get(1).toString());
                            case "DOLR":
                                double tmp2 = result.get(2);
                                result.set(2, tmp2 + value);
                            case "PENY":
                                double tmp3 = result.get(3);
                                result.set(3, tmp3 + value);
                        }
                    }
                }
            }
        });
        Log.d(tag, "array" + result.toString());
        return result;
    }


    public void getCollected(){
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        CollectionReference walletRef = rootRef.collection("wallet(" + UID + dateDB +")");
        //DocumentSnapshot df = walletRef.get();
        walletRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        //coin c = document.toObject(coin.class);
                        String id = document.getString("id");
                        Log.d(tag, "[getCollected] " +id);
                        //Log.d(tag, "[getCollected] the id is " +c.getId());
                        //collected.add(c);
                        //collectedIds.add(id);
                    }
                }
            }
        });
    }

}
