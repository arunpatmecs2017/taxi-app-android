package com.atmecs.taxi;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.EditText;

import com.atmecs.taxi.Utility.SmsListener;
import com.atmecs.taxi.Utility.SmsReceiver;

public class VerifyMobileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_mobile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Verify Mobile Number");

        SmsReceiver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                System.out.println("messageReceived:: "+messageText);
                EditText otpText = findViewById(R.id.txtOTP);
                otpText.setText(messageText);
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
}
