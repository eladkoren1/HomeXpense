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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.koren.homexpense.Classes.PurchaseEntry;
import com.koren.homexpense.data.FireBaseUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class AddEntryActivity extends AppCompatActivity {

    TextView expenseDateTextView;
    AutoCompleteTextView paymentMethodsAutoCompleteTextView;
    AutoCompleteTextView paymentTypesAutoCompleteTextView;
    AutoCompleteTextView storesAutoCompleteTextView;
    EditText priceEditText;
    Button savePurchaseButton;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    Context context = this;
    PurchaseEntry purchaseEntry;
    ArrayList<HashMap<Integer, String>> listsArrayList = new ArrayList<>();

    enum dataNames {
        PAYMENT_METHODS(0), PAYMENT_TYPES(1), STORES(2) ;

        dataNames(int i) {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);

        paymentMethodsAutoCompleteTextView = findViewById(R.id.actv_payment_methods);
        paymentTypesAutoCompleteTextView = findViewById(R.id.actv_payment_types);
        storesAutoCompleteTextView = findViewById(R.id.actv_stores);
        savePurchaseButton = findViewById(R.id.btn_save_purchase);
        expenseDateTextView = findViewById(R.id.tv_expense_date);
        priceEditText = findViewById(R.id.et_price);

        loadListsFromFirebase();

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

        addStore(purchaseEntry);

        DatabaseReference purchasesReference = database.getReference("purchases");
        purchasesReference.child("purchase_" + purchaseEntry.getId()).setValue(purchaseEntry);
        Toast.makeText(context, "רכישה הוספה בהצלחה", Toast.LENGTH_SHORT).show();
        return purchaseEntry;
    }

    private void addStore(PurchaseEntry purchaseEntry) {

        if (!listsArrayList.get(dataNames.STORES.ordinal()).containsValue(purchaseEntry.getStore())) {
            DatabaseReference storesReference = database.getReference("stores");
            storesReference.child(String.valueOf(listsArrayList.get(dataNames.STORES.ordinal()).size() + 1))
                    .setValue(String.valueOf(purchaseEntry.getStore()));
            loadListsFromFirebase();
        }
    }

    private void loadListsFromFirebase() {

        DatabaseReference dataReference = database.getReference("lists");
        dataReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot list : dataSnapshot.getChildren()) {
                    listsArrayList.add(new HashMap<Integer, String>());
                    for (DataSnapshot listItem : list.getChildren()) {
                        listsArrayList.get(i)
                                .put(Integer.valueOf(listItem.getKey()), (String) listItem.getValue());

                    }
                    i++;
                }
                loadAutoCompleteLists(listsArrayList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void loadAutoCompleteLists(ArrayList<HashMap<Integer,String>> dataObjects){

        storesAutoCompleteTextView.setThreshold(1);
        storesAutoCompleteTextView.setAdapter
                (new ArrayAdapter<String>(  context,
                        android.R.layout.select_dialog_item,
                        getList(dataNames.STORES.ordinal())
                                .values().toArray
                                (new String[dataObjects
                                        .get(dataNames.STORES.ordinal())
                                        .size()])));

        paymentMethodsAutoCompleteTextView.setThreshold(1);
        paymentMethodsAutoCompleteTextView.setAdapter
                (new ArrayAdapter<String>(  context,
                        android.R.layout.select_dialog_item,
                        getList(dataNames.PAYMENT_METHODS.ordinal())
                                .values().toArray
                                (new String[dataObjects
                                        .get(dataNames.PAYMENT_METHODS.ordinal())
                                        .size()])));

        paymentTypesAutoCompleteTextView.setThreshold(1);
        paymentTypesAutoCompleteTextView.setAdapter
                (new ArrayAdapter<String>(  context,
                        android.R.layout.select_dialog_item,
                        getList(dataNames.PAYMENT_TYPES.ordinal())
                                .values().toArray
                                (new String[dataObjects
                                        .get(dataNames.PAYMENT_TYPES.ordinal())
                                        .size()])));
    }

    private HashMap<Integer,String> getList(int index){
        return listsArrayList.get(index);
    }
}

