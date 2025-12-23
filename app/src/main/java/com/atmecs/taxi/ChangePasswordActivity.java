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

public class ChangePasswordActivity extends AppCompatActivity {
    EditText oldPasswordText;
    EditText newPasswordText;
    EditText confirmNewPasswordText;
    HashMap<String, String> userDataMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Change Password");
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

    public void changePassword(View v) {
        oldPasswordText = findViewById(R.id.txtOldPassword);
        newPasswordText = findViewById(R.id.txtNewPassword);
        confirmNewPasswordText = findViewById(R.id.txtConfirmNewPassword);

        String oldPasswordString = oldPasswordText.getText().toString().trim();
        String newPasswordString = newPasswordText.getText().toString().trim();
        String confirmPasswordString = confirmNewPasswordText.getText().toString().trim();

        if (oldPasswordString.length()==0) {
            showAlert(Alerts.ALERT_TITLE_CHANGE_PASSWORD,"Old Password can't be empty!");
            return;
        } else if (newPasswordString.length()==0) {
            showAlert(Alerts.ALERT_TITLE_CHANGE_PASSWORD,"New Password can't be empty!");
            return;
        } else if (newPasswordString.length()<6) {
            showAlert(Alerts.ALERT_TITLE_CHANGE_PASSWORD,"New Password character length should be of 6 or greater than 6!");
            return;
        } else if (confirmPasswordString.length()==0) {
            showAlert(Alerts.ALERT_TITLE_CHANGE_PASSWORD,"Re-Enter your new password!");
            return;
        } else if (!newPasswordString.equals(confirmPasswordString)) {
            showAlert(Alerts.ALERT_TITLE_CHANGE_PASSWORD, "New Password and Confirm Password fields do not match!");
            return;
        } else if (oldPasswordString.equals(newPasswordString)) {
            showAlert(Alerts.ALERT_TITLE_CHANGE_PASSWORD, "Consider a new password which is not same as older password!");
            return;
        }

        userDataMap = new HashMap<>();

        DatabaseOperations dbOperation = new DatabaseOperations(ChangePasswordActivity.this);
        String phoneString = Utils.getMobileNumber(getApplicationContext());
        userDataMap = dbOperation.fetchUserData(phoneString);

        String dbPasswordString = userDataMap.get(Constants.USERS_COL_PASSWORD);

        if (!oldPasswordString.equals(dbPasswordString)) {
            showAlert(Alerts.ALERT_TITLE_CHANGE_PASSWORD, "Wrong old password!");
            return;
        }

        DatabaseOperations updateDbOperation = new DatabaseOperations(ChangePasswordActivity.this);
        int insertStatus = updateDbOperation.updatePassword(newPasswordString, Utils.getMobileNumber(getApplicationContext()));

        if (insertStatus==1) {
            AlertDialog alertDialog = new AlertDialog.Builder(ChangePasswordActivity.this).create();
            alertDialog.setTitle(Alerts.ALERT_TITLE_CHANGE_PASSWORD);
            alertDialog.setMessage("Password Changed Successfully!");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });
            alertDialog.show();
        } else {
            showAlert(Alerts.ALERT_TITLE_CHANGE_PASSWORD,"Failed to Change Password!");
        }

    }

    public void showAlert(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(ChangePasswordActivity.this).create();
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
}
