package com.koren.homexpense;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    FirebaseDatabase database = FirebaseDatabase.getInstance();

    String[] paymentTypes = {"אוכל", "בית", "דלק", "בילויים משותפים", "אישי-אלעד", "אישי-קרן"};
    HashMap<String,TextView> expenseSumHashMap = new HashMap<>();

    double paymentSum=0;


    TextView gasSumTextView;
    TextView homeSumTextView;
    TextView foodSumTextView;
    TextView recreationTimeSumTextView;
    TextView personalEladSumTextView;
    TextView personalKerenSumTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Resources res = getResources();

        Configuration newConfig = new Configuration( res.getConfiguration() );
        Locale locale = new Locale("he");
        newConfig.locale = locale;
        newConfig.setLayoutDirection( locale );
        res.updateConfiguration( newConfig, null );

        gasSumTextView = findViewById(R.id.gas_sum_tv);
        homeSumTextView = findViewById(R.id.home_sum_tv);
        foodSumTextView = findViewById(R.id.food_sum_tv);
        recreationTimeSumTextView = findViewById(R.id.recreation_time_sum_tv);
        personalEladSumTextView = findViewById(R.id.personal_elad_sum_tv);
        personalKerenSumTextView = findViewById(R.id.personal_keren_sum_tv);

        expenseSumHashMap.put("דלק",gasSumTextView);
        expenseSumHashMap.put("בית",homeSumTextView);
        expenseSumHashMap.put("אוכל",foodSumTextView);
        expenseSumHashMap.put("בילויים משותפים",recreationTimeSumTextView);
        expenseSumHashMap.put("אישי-אלעד",personalEladSumTextView);
        expenseSumHashMap.put("אישי-קרן",personalKerenSumTextView);
        sumPaymentType();


        Button addPurchaseButton = findViewById(R.id.btn_enter_purchase_activity);
        addPurchaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(),AddEntryActivity.class);
                startActivity(intent);
            }
        });

    }

    private void sumPaymentType(){

        DatabaseReference purchasesReference = database.getReference("purchases");
        purchasesReference.addValueEventListener(new ValueEventListener() {
            @Override

            public void onDataChange(DataSnapshot dataSnapshot) {
                for (String paymentType: expenseSumHashMap.keySet()) {
                    for (DataSnapshot purchase : dataSnapshot.getChildren()) {
                        if (String.valueOf(purchase.child("paymentType").getValue()).contentEquals(paymentType)) {
                            paymentSum = paymentSum + Double.parseDouble(String.valueOf(purchase.child("price").getValue()));
                        }
                    }
                    expenseSumHashMap.get(paymentType).setText(paymentType+": "+String.valueOf(paymentSum));
                    paymentSum=0;

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
