package com.atmecs.taxi;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.atmecs.taxi.SQLiteHelper.DatabaseOperations;
import com.atmecs.taxi.Utility.Alerts;
import com.atmecs.taxi.Utility.Utils;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Settings");

        String[] settingsItemArray = {"Change Password", "Delete my account"};

        ArrayAdapter adapter = new ArrayAdapter<>(this,
                R.layout.activity_settings_listview, settingsItemArray);

        ListView settingsListView = findViewById(R.id.settings_list);
        settingsListView.setAdapter(adapter);

        settingsListView.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> parentAdapter, View view, int position,
                                    long id) {
                if(position==0) {
                    Intent changePasswordIntent = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
                    startActivity(changePasswordIntent);
                } else if (position==1) {
                    settingsAlert(Alerts.ALERT_TITLE_DELETE_ACCOUNT, "Are you sure you want to delete your account?");
                }
            }
        });
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

    public void settingsAlert(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(SettingsActivity.this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (title.equals(Alerts.ALERT_TITLE_DELETE_ACCOUNT)) {
                            int status = deleteAccount();
                            if (status==1) {
                                Utils.removeLoggedinPreferenceKey(getApplicationContext());
                                startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                                finish();
                            } else {
                                showAlert(Alerts.ALERT_TITLE_DELETE_ACCOUNT, "Failed to delete account!!");
                            }

                        } else {
                            dialog.dismiss();
                        }

                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private int deleteAccount() {
        DatabaseOperations dbOperation = new DatabaseOperations(SettingsActivity.this);
        String phoneString = Utils.getMobileNumber(getApplicationContext());
        int status = dbOperation.deleteUser(phoneString);
        return status;
    }

    public void showAlert(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(SettingsActivity.this).create();
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
