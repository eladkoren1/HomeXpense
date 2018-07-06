package com.koren.homexpense.data;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Elad on 02/02/2018.
 */

public class FireBaseUtils  {

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    public FireBaseUtils(){}



    public ArrayList<HashMap<Integer, String>> loadListsFromFirebase() {
        final ArrayList<HashMap<Integer, String>> newListArray = new ArrayList<>();
        DatabaseReference dataReference = database.getReference("lists");
        dataReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot list : dataSnapshot.getChildren()) {
                    newListArray.add(new HashMap<Integer, String>());
                    for (DataSnapshot listItem : list.getChildren()) {
                        newListArray.get(i)
                                .put(Integer.valueOf(listItem.getKey()), (String) listItem.getValue());

                    }
                    i++;
                }
                //loadAutoCompleteLists(listsArrayList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return newListArray;
    }

}
