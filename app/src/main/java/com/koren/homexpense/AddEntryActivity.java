package com.koren.homexpense;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.koren.homexpense.Classes.ExpensePlace;
import com.koren.homexpense.Classes.Expense;
import com.koren.homexpense.Classes.User;

import java.util.ArrayList;
import java.util.HashMap;

public class AddEntryActivity extends AppCompatActivity {

    TextView expenseDateTextView;
    Spinner expenseMethodSpinner;
    Spinner expenseTypesSpinner;
    Spinner expensePlacesSpinner;
    EditText amountEditText;
    Button saveExpenseButton;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference usersFirebaseReference = FirebaseDatabase.getInstance().getReference("users");
    Context context = this;
    //ArrayList<HashMap<Integer, String>> listsArrayList = new ArrayList<>();

    ArrayList<String> expenseMethodsArray = new ArrayList<>();
    ArrayList<String> expenseTypesArray = new ArrayList<>();
    ArrayList<String> expensePlacesArray = new ArrayList<>();

    HashMap<Integer, ExpensePlace> expensePlaces = new HashMap<>();

    String userUID;
    private int expensePlaceKey = -1;

    User user;
    Expense expense;


    /*enum dataNames {
        PAYMENT_METHODS(0), PAYMENT_TYPES(1), STORES(2) ;

        dataNames(int i) {
        }
    }*/
    //TODO: Sort all of the data retrieving from Firebase
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);

        userUID = this.getIntent().getStringExtra("userUID");
        expensePlaceKey = this.getIntent().getIntExtra("expensePlaceKey", -1);

        expenseMethodSpinner = findViewById(R.id.expense_method_spinner);
        expenseTypesSpinner = findViewById(R.id.expense_type_spinner);
        expensePlacesSpinner = findViewById(R.id.expense_places_spinner);
        setSpinners();

        expenseDateTextView = findViewById(R.id.expense_date_tv);
        amountEditText = findViewById(R.id.amount_et);

        saveExpenseButton = findViewById(R.id.save_expense_btn);
        saveExpenseButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                addExpense();
            }
        });

        if (!userUID.isEmpty()) {

            usersFirebaseReference.child(userUID).addListenerForSingleValueEvent(new ValueEventListener() {
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        user = dataSnapshot.getValue(User.class);
                        Log.d("", "");
                        expenseMethodsArray = user.getExpenseMethods();
                        expenseMethodSpinner.setAdapter(
                                new ArrayAdapter<String>(context, android.R.layout.select_dialog_item, expenseMethodsArray));
                        expenseMethodSpinner.setSelection(user.getExpenseMethods().indexOf(user.getPreferredExpenseMethod()));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }


    private void addExpense() {
        String test = String.valueOf(expenseMethodSpinner.getSelectedItem());
        if (!test.equals("")) {
            test = String.valueOf(expenseTypesSpinner.getSelectedItem());
            if (!test.equals("")) {
                test = String.valueOf(expensePlacesSpinner.getSelectedItem());
                if (!test.equals("")) {
                    test = String.valueOf(amountEditText.getText());
                    if (!test.equals("")) {
                        expense = new Expense(
                                String.valueOf(expenseMethodSpinner.getSelectedItem()),
                                String.valueOf(expenseTypesSpinner.getSelectedItem()),
                                String.valueOf(expensePlacesSpinner.getSelectedItem()),
                                Double.parseDouble(String.valueOf(amountEditText.getText())));

                        //TODO: IMPLEMENT ADDSTORE IN MAIN ACTIVITY
                        //addStore(purchaseEntry);
                        DatabaseReference purchasesReference = database.getReference("purchases");
                        purchasesReference.child(String.valueOf(expense.getDateId())).setValue(expense);
                        Toast.makeText(context, "רכישה הוספה בהצלחה", Toast.LENGTH_SHORT).show();
                        finish();
                    } else Toast.makeText(this, "אנא הכנס הוצאה", Toast.LENGTH_SHORT).show();
                } else Toast.makeText(this, "אנא הכנס מקום הוצאה", Toast.LENGTH_SHORT).show();
            } else Toast.makeText(this, "אנא הכנס סוג הוצאה", Toast.LENGTH_SHORT).show();
        } else Toast.makeText(this, "אנא הכנס דרך הוצאה", Toast.LENGTH_SHORT).show();
    }

   /* private void addStore(Expense purchaseEntry) {

        if (!listsArrayList.get(dataNames.STORES.ordinal()).containsValue(purchaseEntry.getStore())) {
            DatabaseReference storesReference = database.getReference("stores");
            storesReference.child(String.valueOf(listsArrayList.get(dataNames.STORES.ordinal()).size() + 1))
                    .setValue(String.valueOf(purchaseEntry.getStore()));
            loadListsFromFirebase();
        }
    }*/

    private void setSpinners() {

        //Load expense types from Firebase and set adapter
        DatabaseReference expenseTypesReference = database.getReference("expenseTypes");
        expenseTypesReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    expenseTypesArray.add(String.valueOf(item.getValue()));
                }
                expenseTypesSpinner.setAdapter
                        (new ArrayAdapter<String>(context, android.R.layout.select_dialog_item, expenseTypesArray));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Load expense places From and set adapter
        DatabaseReference expensePlacesReference = database.getReference("expensePlaces");
        expensePlacesReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot expensePlaceKey : dataSnapshot.getChildren()) {
                    for (DataSnapshot expensePlaceName : expensePlaceKey.getChildren()) {

                        //TODO: CHANGE placeName to name IN DB!!!!!!111!!111

                        if (String.valueOf(expensePlaceName.getKey()).contentEquals("placeName"))
                            expensePlacesArray.add(String.valueOf(expensePlaceName.getValue()));
                    }
                }
                expensePlacesSpinner.setAdapter
                        (new ArrayAdapter<String>(context, android.R.layout.select_dialog_item, expensePlacesArray));
                if (expensePlaceKey != -1) expensePlacesSpinner.setSelection(expensePlaceKey);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

    //private void setAutoCompleteLists(ArrayList<HashMap<Integer,String>> dataObjects){


       /* storesSpinner.setAdapter
                (new ArrayAdapter<String>(  context,
                        android.R.layout.select_dialog_item,
                        getList(dataNames.STORES.ordinal())
                                .values().toArray
                                (new String[dataObjects
                                        .get(dataNames.STORES.ordinal())
                                        .size()])));*/


        /*expenseMethodsSpinner.setAdapter
                (new ArrayAdapter<String>(  context,
                        android.R.layout.select_dialog_item,
                        user.getExpenseMethods()));

                        getList(dataNames.PAYMENT_METHODS.ordinal())
                                .values().toArray
                              (new String[dataObjects
                                        .get(dataNames.PAYMENT_METHODS.ordinal())
                                        .size()])));*/

        /*expenseTypesSpinner.setAdapter
                (new ArrayAdapter<String>(  context,
                        android.R.layout.select_dialog_item,
                        getList(dataNames.PAYMENT_TYPES.ordinal())
                                .values().toArray
                                (new String[dataObjects
                                        .get(dataNames.PAYMENT_TYPES.ordinal())
                                        .size()])));*

    /*private HashMap<Integer,String> getList(int index){
        return listsArrayList.get(index);
    }*/

    /*private void getExpensePlacesFromFirebase(){

        final DatabaseReference expensePlacesReference = database.getReference("ExpensePlaces");
        expensePlacesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot placeKey : dataSnapshot.getChildren()){
                    String placeAddress = (String) placeKey.child("placeAddress").getValue();
                    String placeName = (String) placeKey.child("placeName").getValue();
                    LatLng placeCoordinates = new LatLng(
                            (double)placeKey.child("placeCoordinates").child("latitude").getValue(),
                            (double)placeKey.child("placeCoordinates").child("longitude").getValue());
                    String expenseType = (String) placeKey.child("expenseType").getValue();

                    expensePlaces.put(Integer.parseInt(placeKey.getKey()),
                            new ExpensePlace(placeName,placeAddress,expenseType,placeCoordinates));
                }

                expenseTypesSpinner.setText(expensePlaces.get(expensePlaceKey).getExpenseType());


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }*/
