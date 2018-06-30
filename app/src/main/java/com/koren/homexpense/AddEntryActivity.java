package com.koren.homexpense;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.koren.homexpense.Classes.PurchaseEntry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class AddEntryActivity extends AppCompatActivity {

    String[] paymentMethods = {"לאומי-אלעד","לאומי-קרן","כאל-אלעד",""};
    String[] paymentTypes = {"אוכל", "בית", "דלק", "בילויים משותפים", "אישי-אלעד", "אישי-קרן"};

    TextView expenseDateTextView;
    AutoCompleteTextView paymentMethodsAutoCompleteTextView;
    AutoCompleteTextView paymentTypesAutoCompleteTextView;
    AutoCompleteTextView storesAutoCompleteTextView;
    EditText priceEditText;
    Button savePurchaseButton;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    Context context = this;
    PurchaseEntry purchaseEntry;
    ArrayList<HashMap<Integer, String>> dataObjectsArrayList = new ArrayList<>();
    enum dataNames {
        paymentTypes (0) , STORES (1) ;

        dataNames(int i) {
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);
        //loadStores();
        loadListsFromFirebase();

        ArrayAdapter<String> paymentMethodsAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, paymentMethods);
        ArrayAdapter<String> paymentTypesAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, paymentTypes);

        paymentMethodsAutoCompleteTextView = findViewById(R.id.actv_payment_methods);
        paymentTypesAutoCompleteTextView = findViewById(R.id.actv_payment_types);

        savePurchaseButton = findViewById(R.id.btn_save_purchase);
        expenseDateTextView = findViewById(R.id.tv_expense_date);
        priceEditText = findViewById(R.id.et_price);

        paymentMethodsAutoCompleteTextView.setThreshold(1);
        paymentTypesAutoCompleteTextView.setThreshold(1);

        paymentMethodsAutoCompleteTextView.setAdapter(paymentMethodsAdapter);
        paymentTypesAutoCompleteTextView.setAdapter(paymentTypesAdapter);


        savePurchaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPurchase();
            }
        });
    }


    private PurchaseEntry addPurchase() {
        String test = String.valueOf(storesAutoCompleteTextView.getText());
        if (!test.equals("")) {
            test = String.valueOf(paymentTypesAutoCompleteTextView.getText());
            if (!test.equals("")) {
                test = String.valueOf(paymentMethodsAutoCompleteTextView.getText());
                if (!test.equals("")) {
                    test = String.valueOf(priceEditText.getText());
                    if (!test.equals("")) {
                        purchaseEntry = new PurchaseEntry(
                                String.valueOf(paymentMethodsAutoCompleteTextView.getText()),
                                String.valueOf(paymentTypesAutoCompleteTextView.getText()),
                                String.valueOf(storesAutoCompleteTextView.getText()),
                                Double.parseDouble(String.valueOf(priceEditText.getText())));
                    }
                }
            }
        }
        if (!dataObjectsArrayList.get(dataNames.STORES.ordinal()).containsValue(purchaseEntry.getStore())) {
            DatabaseReference storesReference = database.getReference("stores");
            storesReference.child(String.valueOf(dataObjectsArrayList.get(dataNames.STORES.ordinal()).size() + 1))
                    .setValue(String.valueOf(purchaseEntry.getStore()));
            loadListsFromFirebase();
        }

        addPurchaseEntry(purchaseEntry);
        return purchaseEntry;
    }

    private void addPurchaseEntry(PurchaseEntry purchaseEntry) {

        DatabaseReference purchasesReference = database.getReference("purchases");


        purchasesReference.child("purchase_" + purchaseEntry.getId()).setValue(purchaseEntry);
        Toast.makeText(context, "רכישה הוספה בהצלחה", Toast.LENGTH_SHORT).show();
        purchasesReference.child("purchases").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadListsFromFirebase() {

        DatabaseReference dataReference = database.getReference("lists");
        dataReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot firebaseObjects : dataSnapshot.getChildren()) {
                    dataObjectsArrayList.add(new HashMap<Integer, String>());
                    for (DataSnapshot dataObject : firebaseObjects.getChildren()) {
                        dataObjectsArrayList.get(i)
                                .put(Integer.valueOf(dataObject.getKey()), (String) dataObject.getValue());

                    }
                    i++;
                }
                loadAutoCompleteLists(dataObjectsArrayList);
            }
            //ArrayAdapter<String> storesAdapter =


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void loadAutoCompleteLists(ArrayList<HashMap<Integer,String>> dataObjects){
        storesAutoCompleteTextView = findViewById(R.id.actv_stores);
        storesAutoCompleteTextView.setThreshold(1);
        storesAutoCompleteTextView.setAdapter
                (new ArrayAdapter<String>(  context,
                        android.R.layout.select_dialog_item,
                        getList(dataNames.STORES.ordinal())
                                .values().toArray
                                (new String[dataObjectsArrayList
                                        .get(dataNames.STORES.ordinal())
                                        .size()])));
    }

    private HashMap<Integer,String> getList(int index){
        return dataObjectsArrayList.get(index);
    }
}

