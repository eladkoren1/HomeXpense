package com.koren.homexpense;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.koren.homexpense.Classes.User;

public class UserEditActivity extends AppCompatActivity {


    DatabaseReference usersFirebaseReference  = FirebaseDatabase.getInstance().getReference("users");
    String userUID;
    User user;
    Context context = this;

    private Button addExpenseMethodButton;
    private Button saveUserButton;

    private EditText userFirstNameEditText;
    private EditText userLastNameEditText;
    private Spinner preferredExpenseMethodSpinner;

    private View addExpenseMethodDialogView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addExpenseMethodDialogView = getLayoutInflater().inflate(R.layout.activity_user_edit_dialog_add_expense_method, null,false);
        setContentView(R.layout.activity_user_edit);

        userUID = this.getIntent().getStringExtra("userUID");

        userFirstNameEditText = findViewById(R.id.user_first_name_et);
        userLastNameEditText = findViewById(R.id.user_last_name_et);
        preferredExpenseMethodSpinner = findViewById(R.id.preferred_expense_method_spinner);
        preferredExpenseMethodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                user.setPreferredExpenseMethod(String.valueOf(parent.getItemAtPosition(position)));
                preferredExpenseMethodSpinner.setSelection(user.getExpenseMethods().indexOf(user.getPreferredExpenseMethod()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        saveUserButton = findViewById(R.id.save_user_btn);
        saveUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                user.setFirstName(String.valueOf(userFirstNameEditText.getText()));
                user.setLastName(String.valueOf(userLastNameEditText.getText()));
                usersFirebaseReference.child(userUID).setValue(user);
                finish();
            }
        });

        addExpenseMethodButton = findViewById(R.id.add_expense_method_btn);
        addExpenseMethodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddExpenseMethodDialog();
            }
        });

        if (!userUID.isEmpty()){

            usersFirebaseReference.child(userUID).addListenerForSingleValueEvent(new ValueEventListener() {
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        user = dataSnapshot.getValue(User.class);
                        Log.d("","");
                        userFirstNameEditText.setText(user.getFirstName());
                        userLastNameEditText.setText(user.getLastName());
                        preferredExpenseMethodSpinner.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, user.getExpenseMethods()));
                        preferredExpenseMethodSpinner.setSelection(user.getExpenseMethods().indexOf(user.getPreferredExpenseMethod()));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    private void setPreferredExpenseMethodList(){

    }
    private void showAddExpenseMethodDialog(){

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        final Button submitExpenseMethodButton = addExpenseMethodDialogView.findViewById(R.id.submit_expense_method_btn);
        final EditText addExpenseMethodEditText = addExpenseMethodDialogView.findViewById(R.id.add_expense_method_et);

        mBuilder.setView(addExpenseMethodDialogView);
        final AlertDialog addExpenseMethodDialog = mBuilder.create();
        addExpenseMethodDialog.setCancelable(true);
        addExpenseMethodDialog.setCanceledOnTouchOutside(true);
        addExpenseMethodDialog.show();

        submitExpenseMethodButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (!addExpenseMethodEditText.getText().toString().isEmpty()) {
                    DatabaseReference userExpenseMethodsReference = usersFirebaseReference.child(userUID);
                    String newExpenseMethod = String.valueOf(addExpenseMethodEditText.getText());
                    user.addExpenseMethod(newExpenseMethod);
                    userExpenseMethodsReference.setValue(user);

                    addExpenseMethodDialog.dismiss();

                    ViewGroup parent = (ViewGroup) addExpenseMethodDialogView.getParent();
                    parent.removeView(addExpenseMethodDialogView);
                }
                else {
                    Toast.makeText(context, "שדה לא יכול להיות ריק ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

