package com.atmecs.taxi;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.atmecs.taxi.Utility.Alerts;

import java.util.Random;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Help");
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

    public void callHelp(View view) {
        Random random = new Random();
        int num = random.nextInt(10);
        if (num%2==0) {
            showAlert(Alerts.ALERT_TITLE_HELP,"Successfully sent an email of your help request, One of our customer care executive will contact you on your registered mobile number.");
        } else {
            showAlert(Alerts.ALERT_TITLE_HELP,"Failed to send an email of your help request, Please try again!");
        }
    }

    public void showAlert(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(HelpActivity.this).create();
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
