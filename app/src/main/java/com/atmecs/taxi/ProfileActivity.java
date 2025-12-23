package com.atmecs.taxi;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.atmecs.taxi.SQLiteHelper.DatabaseOperations;
import com.atmecs.taxi.Utility.Alerts;
import com.atmecs.taxi.Utility.Constants;
import com.atmecs.taxi.Utility.Utils;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    HashMap<String, String> userDataMap, updatedUserDataMap;
    EditText firstNameText;
    EditText lastNameText;
    EditText phoneText;
    EditText emailText;
//    EditText passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Profile");

        userDataMap = new HashMap<>();

        DatabaseOperations dbOperation = new DatabaseOperations(ProfileActivity.this);
        String phoneString = Utils.getMobileNumber(getApplicationContext());
        userDataMap = dbOperation.fetchUserData(phoneString);

        firstNameText = findViewById(R.id.profileFirstName);
        lastNameText = findViewById(R.id.profileLastName);
        phoneText = findViewById(R.id.profilePhone);
        emailText = findViewById(R.id.profileEmail);

        firstNameText.setText(userDataMap.get(Constants.USERS_COL_FIRST_NAME));
        lastNameText.setText(userDataMap.get(Constants.USERS_COL_LAST_NAME));
        phoneText.setText(userDataMap.get(Constants.USERS_COL_PHONE_NUMBER));
        emailText.setText(userDataMap.get(Constants.USERS_COL_EMAIL_ADDRESS));

        phoneText.setEnabled(false);
    }

    public void updateUserProfile(View v) {

        updatedUserDataMap = new HashMap<>();

        String firstNameString = firstNameText.getText().toString().trim();
        String lastNameString = lastNameText.getText().toString().trim();
        String phoneString = phoneText.getText().toString().trim();
        String emailString = emailText.getText().toString().trim();

        if (firstNameString.trim().length()==0) {
            showAlert(Alerts.ALERT_TITLE_PROFILE,"First Name can't be empty!");
            return;
        } else if (lastNameString.trim().length()==0) {
            showAlert(Alerts.ALERT_TITLE_PROFILE,"Last Name can't be empty!");
            return;
        }  else if (!Utils.isEmailValid(emailString.trim())) {
            showAlert(Alerts.ALERT_TITLE_PROFILE, "Enter a valid email address!");
            return;
        }

        updatedUserDataMap.put(Constants.USERS_COL_FIRST_NAME, firstNameString);
        updatedUserDataMap.put(Constants.USERS_COL_LAST_NAME, lastNameString);
        updatedUserDataMap.put(Constants.USERS_COL_PHONE_NUMBER, phoneString);
        updatedUserDataMap.put(Constants.USERS_COL_EMAIL_ADDRESS, emailString);

        DatabaseOperations dbOperation = new DatabaseOperations(ProfileActivity.this);
        int insertStatus = dbOperation.updateUserData(updatedUserDataMap, phoneString);

        if (insertStatus==1) {
            showAlert(Alerts.ALERT_TITLE_PROFILE,"User Profile Updated Successfully!");
        } else {
            showAlert(Alerts.ALERT_TITLE_PROFILE,"Failed to update Profile!");
        }
    }

    public void showAlert(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(ProfileActivity.this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
