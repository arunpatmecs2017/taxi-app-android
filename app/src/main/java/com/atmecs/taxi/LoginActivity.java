package com.atmecs.taxi;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.atmecs.taxi.SQLiteHelper.DatabaseOperations;
import com.atmecs.taxi.Utility.Alerts;
import com.atmecs.taxi.Utility.Utils;
//import com.atmecs.toasterlibrary.ToasterMessage;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    EditText phoneText;
    EditText passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Login");
//        ToasterMessage.showToastMessage(this, "Hello from Git & JitPack");
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_SMS,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.INTERNET }, 0);

        phoneText = findViewById(R.id.txtLoginPhone);
        passwordText = findViewById(R.id.txtLoginPassword);

        TextView register = findViewById(R.id.lnkRegister);
        register.setMovementMethod(LinkMovementMethod.getInstance());
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFields();
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });

        Button loginButton = findViewById(R.id.btnLogin);
        loginButton.setMovementMethod(LinkMovementMethod.getInstance());

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phoneString = phoneText.getText().toString().trim();
                String passwordString = passwordText.getText().toString().trim();

                if (phoneString.trim().length()==0) {
                    showAlert(Alerts.ALERT_TITLE_LOGIN,"Phone Number can't be empty!");
                    return;
                } else if (phoneString.trim().length() < 10) {
                    showAlert(Alerts.ALERT_TITLE_LOGIN,"Enter 10 digit phone number!");
                    return;
                } else if (passwordString.length()==0) {
                    showAlert(Alerts.ALERT_TITLE_LOGIN,"Password can't be empty!");
                    return;
                }

                HashMap<String, String> userDataMap = new HashMap<>();

                DatabaseOperations dbOperation = new DatabaseOperations(LoginActivity.this);
                userDataMap = dbOperation.fetchUserData(phoneString);

                if (userDataMap.isEmpty()) {
                    showAlert(Alerts.ALERT_TITLE_LOGIN,"No user registered with mobile number "+phoneString);
                    return;
                } else {
                    if (!passwordString.equals(userDataMap.get("Password"))) {
                        showAlert(Alerts.ALERT_TITLE_LOGIN,"Wrong password!");
                        return;
                    } else {
                        Utils.setMobileNumber(getApplicationContext(), phoneString);
                        clearFields();
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);

                    }
                }
            }
        });

    }

    public void showAlert(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
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

    public void clearFields() {
        phoneText.setText("");
        passwordText.setText("");
    }
}
