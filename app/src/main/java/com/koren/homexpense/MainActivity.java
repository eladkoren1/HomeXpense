package com.koren.homexpense;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.koren.homexpense.Classes.User;

import java.util.HashMap;
import java.util.Locale;

    //TODO: implement user
    //TODO: get info from leumi-card and use to show expenses

public class MainActivity extends AppCompatActivity {


    FirebaseDatabase homeXpenseDatabase = FirebaseDatabase.getInstance();
    HashMap<String, TextView> expenseSumHashMap = new HashMap<>();
    Context context = this;
    SharedPreferences sharedPref;

    double expenseSum = 0;
    String userUID;

    TextView userNameTextView;
    TextView gasSumTextView;
    TextView homeSumTextView;
    TextView foodSumTextView;
    TextView recreationTimeSumTextView;
    TextView personalEladSumTextView;
    TextView personalKerenSumTextView;
    TextView currentUserTextView;
    private View loginDialogView;
    private EditText userRegisterFirstName;
    private EditText userRegisterLastName;
    Button addExpenseButton;
    Button userEditButton;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        loginDialogView = getLayoutInflater().inflate(R.layout.activity_main_dialog_login, null,false);

        Resources res = getResources();
        Configuration newConfig = new Configuration(res.getConfiguration());
        Locale locale = new Locale("he");
        newConfig.locale = locale;
        newConfig.setLayoutDirection(locale);
        res.updateConfiguration(newConfig, null);

        userEditButton = findViewById(R.id.user_edit_btn);
        addExpenseButton = findViewById(R.id.add_expense_btn);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        userUID = sharedPref.getString("UserUID","-1");

        ValueEventListener userUIDvalueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) {
                    showLoginDialog();
                }
                else {
                    userEditButton.setVisibility(View.VISIBLE);
                    addExpenseButton.setVisibility(View.VISIBLE);
                    checkPermissions();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };

        DatabaseReference userUIDRef = homeXpenseDatabase.getReference().child("users").child(userUID);
        userUIDRef.addValueEventListener(userUIDvalueListener);

        userNameTextView = findViewById(R.id.user_name_tv);
        gasSumTextView = findViewById(R.id.gas_sum_tv);
        homeSumTextView = findViewById(R.id.home_sum_tv);
        foodSumTextView = findViewById(R.id.food_sum_tv);
        recreationTimeSumTextView = findViewById(R.id.recreation_time_sum_tv);
        personalEladSumTextView = findViewById(R.id.personal_elad_sum_tv);
        personalKerenSumTextView = findViewById(R.id.personal_keren_sum_tv);

        expenseSumHashMap.put("דלק", gasSumTextView);
        expenseSumHashMap.put("בית", homeSumTextView);
        expenseSumHashMap.put("אוכל", foodSumTextView);
        expenseSumHashMap.put("בילויים משותפים", recreationTimeSumTextView);
        expenseSumHashMap.put("אישי-אלעד", personalEladSumTextView);
        expenseSumHashMap.put("אישי-קרן", personalKerenSumTextView);
        sumPaymentType();


        userEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userEditIntent = new Intent(context,UserEditActivity.class);
                userEditIntent.putExtra("userUID", userUID);
                startActivity(userEditIntent);
            }
        });


        addExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addEntryIntent = new Intent(getBaseContext(), AddEntryActivity.class);
                addEntryIntent.putExtra("userUID",userUID);
                startActivity(addEntryIntent);
            }
        });
    }

    private void sumPaymentType() {

        DatabaseReference purchasesReference = homeXpenseDatabase.getReference("purchases");
        purchasesReference.addValueEventListener(new ValueEventListener() {
            @Override

            public void onDataChange(DataSnapshot dataSnapshot) {
                for (String expenseType : expenseSumHashMap.keySet()) {
                    for (DataSnapshot purchase : dataSnapshot.getChildren()) {
                        if (String.valueOf(purchase.child("paymentType").getValue()).contentEquals(expenseType)) {
                            expenseSum = expenseSum + Double.parseDouble(String.valueOf(purchase.child("price").getValue()));
                        }
                    }
                    expenseSumHashMap.get(expenseType).setText(expenseType + ": " + String.valueOf(expenseSum));
                    expenseSum = 0;

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void checkPermissions() {
        //Permission asking for writing storage (for DB), fine location, and coarse location
        if ((ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }

        //If location permission is already granted, get google map
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            initStoreLocationService();
        }
        //If location permission is already granted, get google map
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            initStoreLocationService();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        // If request is cancelled, the result arrays are empty.

        //Fine location permission result check
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            initStoreLocationService();
        }

        else {
            Toast.makeText(this,"In order to enable smart shop service, you need to give location permissions",Toast.LENGTH_LONG).show();
        }
    }

    private void initStoreLocationService() {
        Intent storeLocationServiceIntent = new Intent(context,StoreLocationService.class);
        storeLocationServiceIntent.putExtra("userUID",userUID);
        context.startService(storeLocationServiceIntent);

    }

    private void showLoginDialog() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);

        final Button registerUserButton = loginDialogView.findViewById(R.id.register_user_btn);

        userRegisterFirstName = loginDialogView.findViewById(R.id.new_user_login_first_name_et);
        userRegisterLastName = loginDialogView.findViewById(R.id.new_user_login_last_name_et);

        mBuilder.setView(loginDialogView);
        final AlertDialog dialog = mBuilder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        registerUserButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (!userRegisterFirstName.getText().toString().isEmpty() && !userRegisterLastName.getText().toString().isEmpty()) {
                    DatabaseReference homeXpenseUsersReference = homeXpenseDatabase.getReference("users");
                    User user = new User();
                    Toast.makeText(context, "ברוך הבא " + userRegisterFirstName.getText(), Toast.LENGTH_SHORT).show();

                    user.setFirstName(String.valueOf(userRegisterFirstName.getText()));
                    user.setLastName(String.valueOf(userRegisterLastName.getText()));
                    user.setPreferredExpenseMethod("");

                    DatabaseReference pushedUserRef = homeXpenseUsersReference.push();
                    pushedUserRef.setValue(user);

                    userUID = pushedUserRef.getKey();
                    SharedPreferences.Editor prefEditor = sharedPref.edit();
                    prefEditor.putString("UserUID",userUID);
                    prefEditor.commit();

                    userEditButton.setVisibility(View.VISIBLE);
                    addExpenseButton.setVisibility(View.VISIBLE);

                    dialog.dismiss();
                    checkPermissions();
                    ViewGroup parent = (ViewGroup) loginDialogView.getParent();
                    parent.removeView(loginDialogView);

                }
                else {
                    Toast.makeText(context, "השלם שדות חסרים", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
