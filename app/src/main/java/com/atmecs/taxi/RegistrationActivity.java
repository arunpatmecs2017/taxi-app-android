package com.atmecs.taxi;

import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
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
//import com.atmecs.toasterlibrary.SMSReader;

import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity {
    HashMap<String, String> registrationDataMap, userDataMap;

    EditText firstNameText;
    EditText lastNameText;
    EditText phoneText;
    EditText emailText;
    EditText passwordText;
    EditText confirmPasswordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Registration");

//        getMessageBodyBySender("+917730851308");
//        readAllSMS();
//        getSingleSMSDetails("BA-ACTGRP");
//        SMSReader smsReader = new SMSReader();
//        smsReader.getSingleSMSDetails(this, "BA-ACTGRP");
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

    public void registerUser(View view) {
//        Intent intent = new Intent(RegistrationActivity.this, VerifyMobileActivity.class);
//        startActivity(intent);
        registrationDataMap = new HashMap<>();
        userDataMap = new HashMap<>();

        firstNameText = findViewById(R.id.txtFirstName);
        lastNameText = findViewById(R.id.txtLastName);
        phoneText = findViewById(R.id.txtPhone);
        emailText = findViewById(R.id.txtEmail);
        passwordText = findViewById(R.id.txtPassword);
        confirmPasswordText = findViewById(R.id.txtConfirmPassword);

        String firstNameString = firstNameText.getText().toString().trim();
        String lastNameString = lastNameText.getText().toString().trim();
        String phoneString = phoneText.getText().toString().trim();
        String emailString = emailText.getText().toString().trim();
        String passwordString = passwordText.getText().toString().trim();
        String confirmPasswordString = confirmPasswordText.getText().toString().trim();

        if (firstNameString.trim().length()==0) {
            showAlert(Alerts.ALERT_TITLE_RESGISTRATION,"First Name can't be empty!");
            return;
        } else if (lastNameString.trim().length()==0) {
            showAlert(Alerts.ALERT_TITLE_RESGISTRATION,"Last Name can't be empty!");
            return;
        } else if (phoneString.trim().length()==0) {
            showAlert(Alerts.ALERT_TITLE_RESGISTRATION,"Phone Number can't be empty!");
            return;
        } else if (phoneString.trim().length() < 10) {
            showAlert(Alerts.ALERT_TITLE_RESGISTRATION,"Enter 10 digit phone number!");
            return;
        } else if (!Utils.isEmailValid(emailString.trim())) {
            showAlert(Alerts.ALERT_TITLE_RESGISTRATION,"Enter a valid email address!");
            return;
        } else if (passwordString.length()==0) {
            showAlert(Alerts.ALERT_TITLE_RESGISTRATION,"Password can't be empty!");
            return;
        } else if (confirmPasswordString.length()==0) {
            showAlert(Alerts.ALERT_TITLE_RESGISTRATION,"Confirm Password can't be empty!");
            return;
        } else if (passwordString.length()<6) {
            showAlert(Alerts.ALERT_TITLE_RESGISTRATION,"Password character length should be of 6 or greater than 6!");
            return;
        } else if (!passwordString.equals(confirmPasswordString)) {
            showAlert(Alerts.ALERT_TITLE_RESGISTRATION,"Password and Confirm Password fields do not match!");
            return;
        }

        DatabaseOperations fetchDbOperation = new DatabaseOperations(RegistrationActivity.this);
        userDataMap = fetchDbOperation.fetchUserData(phoneString);

        if (!userDataMap.isEmpty())  {
            showAlert(Alerts.ALERT_TITLE_RESGISTRATION,"User already exists with mobile number "+phoneString);
            return;
        }

        registrationDataMap.put(Constants.USERS_COL_FIRST_NAME, firstNameString);
        registrationDataMap.put(Constants.USERS_COL_LAST_NAME, lastNameString);
        registrationDataMap.put(Constants.USERS_COL_PHONE_NUMBER, phoneString);
        registrationDataMap.put(Constants.USERS_COL_EMAIL_ADDRESS, emailString);
        registrationDataMap.put(Constants.USERS_COL_PASSWORD, passwordString);

        DatabaseOperations insertDbOperation = new DatabaseOperations(RegistrationActivity.this);
        int insertStatus = insertDbOperation.insertUserData(registrationDataMap);

        if (insertStatus==1) {
            AlertDialog alertDialog = new AlertDialog.Builder(RegistrationActivity.this).create();
            alertDialog.setTitle(Alerts.ALERT_TITLE_RESGISTRATION);
            alertDialog.setMessage("User Registered Successfully!");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });
            alertDialog.show();
        } else {
            showAlert(Alerts.ALERT_TITLE_RESGISTRATION,"User Registration Failed!");
        }
    }

    public void showAlert(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(RegistrationActivity.this).create();
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

    public String getOTPByMessageContent(String content) {
        String otpString = "";
        Cursor cursor = getContentResolver().query(Uri.parse(Constants.INBOX), null, null, null, null);
        System.out.println("cursor_count:: "+cursor.getCount());
        if (cursor.moveToFirst()) { // must check the result to prevent exception
            do {
                String messageBody = "";
                for(int idx=0;idx<cursor.getColumnCount();idx++)
                {
                    String columnName = cursor.getColumnName(idx);
                    if (columnName.equals("body")) {
                        messageBody = cursor.getString(idx);
                        if (messageBody.contains(content)) {
                            otpString = messageBody.replaceAll("[^0-9]","");
                        }
                    }
                }
                System.out.println("messageBody:: " +
                        ""+messageBody);
            } while (cursor.moveToNext());

            return otpString;
        } else {
            // empty box, no SMS
            System.out.println("empty box, no SMS");
            return "empty box, no SMS";
        }
    }

    public void getMessageBodyBySender (String senderAddress) {
        String searchQuery = "address like '%" + senderAddress + "%'";
        Cursor cursor = getContentResolver().query(Uri.parse(Constants.INBOX), null, searchQuery, null, null);
        System.out.println("cursor_count:: "+cursor.getCount());
        if (cursor.moveToFirst()) { // must check the result to prevent exception
            do {
                String messageBody = "";
                for(int idx=0;idx<cursor.getColumnCount();idx++)
                {
                    String columnName = cursor.getColumnName(idx);
                    if (columnName.equals("body")) {
                        messageBody = cursor.getString(idx);
                    }
                }

                System.out.println("messageBody:: "+messageBody);
                String messageString = messageBody.replaceAll("[^0-9]","");
                System.out.println("messageString:: "+messageString);
            } while (cursor.moveToNext());
        } else {
            // empty box, no SMS
            System.out.println("empty box, no SMS");
        }
    }

    public void getSingleSMSDetails(String senderAddress) {
        String searchQuery = "address like '%" + senderAddress + "%'";
        Cursor cursor = getContentResolver().query(Uri.parse(Constants.INBOX), null, searchQuery, null, null);
        System.out.println("cursor_count:: "+cursor.getCount());
        if (cursor.moveToFirst()) { // must check the result to prevent exception
            do {
                String messageBody = "";
                for(int idx=0;idx<cursor.getColumnCount();idx++)
                {
                    messageBody += " " + cursor.getColumnName(idx) + ":" + cursor.getString(idx);
                }

                System.out.println("messageBody:: "+messageBody);
            } while (cursor.moveToNext());
        } else {
            // empty box, no SMS
            System.out.println("empty box, no SMS");
        }
    }

    public void readAllSMS() {
        Cursor cursor = getContentResolver().query(Uri.parse(Constants.INBOX), null, null, null, null);
        System.out.println("cursor_count:: "+cursor.getCount());
        if (cursor.moveToFirst()) { // must check the result to prevent exception
            do {
                String messageBody = "";
                for(int idx=0;idx<cursor.getColumnCount();idx++)
                {
                    messageBody += " " + cursor.getColumnName(idx) + ":" + cursor.getString(idx);
                }

                System.out.println("messageBody:: "+messageBody);
            } while (cursor.moveToNext());
        } else {
            // empty box, no SMS
            System.out.println("empty box, no SMS");
        }
    }
}