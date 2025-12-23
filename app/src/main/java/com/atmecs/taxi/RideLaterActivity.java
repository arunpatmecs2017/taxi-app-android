package com.atmecs.taxi;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.app.NavUtils;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.atmecs.taxi.SQLiteHelper.DatabaseOperations;
import com.atmecs.taxi.Utility.Alerts;
import com.atmecs.taxi.Utility.Constants;
import com.atmecs.taxi.Utility.Utils;

import java.util.Calendar;
import java.util.HashMap;

public class RideLaterActivity extends AppCompatActivity implements View.OnClickListener {

    HashMap<String, String> rideLaterTripDataMap;
    Button btnDatePicker, btnTimePicker;
    EditText dateText, timeText, pickupText, dropText;

    private int year, month, day, hour, minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ridelater);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Ride Later");

        btnDatePicker = findViewById(R.id.btnSelectDate);
        btnTimePicker = findViewById(R.id.btnSelectTime);
        dateText = findViewById(R.id.txtDate);
        timeText = findViewById(R.id.txtTime);
        pickupText = findViewById(R.id.txtRlPickup);
        dropText = findViewById(R.id.txtRlDrop);

        btnDatePicker.setOnClickListener(this);
        btnTimePicker.setOnClickListener(this);

        Intent intent = getIntent();
        String pickupString = intent.getStringExtra("pickupLocationString");
        String dropString = intent.getStringExtra("dropLocationString");

        pickupText.setText(pickupString);
        dropText.setText(dropString);
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

    @Override
    public void onClick(View v) {
        if (v == btnDatePicker) {

            // Get Current Date
            final Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            System.out.println("monthOfYear:: "+monthOfYear);
                            String monthDay = (dayOfMonth<10) ? "0"+dayOfMonth : String.valueOf(dayOfMonth);
                            monthOfYear = monthOfYear+1;
                            String yearMonth = (monthOfYear<10) ? "0"+monthOfYear : String.valueOf(monthOfYear);
                            dateText.setText(monthDay + "/" + yearMonth + "/" + year);

                        }
                    }, year, month, day);
            datePickerDialog.show();
        }
        if (v == btnTimePicker) {

            // Get Current Time
            final Calendar calendar = Calendar.getInstance();
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            String hour = (hourOfDay<10) ? "0"+hourOfDay : String.valueOf(hourOfDay);
                            String minutes = (minute<10) ? "0"+minute : String.valueOf(minute);
                            timeText.setText(hour + ":" + minutes);
                        }
                    }, hour, minute, true);
            timePickerDialog.show();
        }
    }

    public void saveRideLaterTripDetails(View view) {
        rideLaterTripDataMap = new HashMap<>();

        String dateString = dateText.getText().toString().trim();
        String timeString = timeText.getText().toString().trim();
        String pickupString = pickupText.getText().toString().trim();
        String dropString = dropText.getText().toString().trim();

        if (pickupString.length()==0) {
            showAlert(Alerts.ALERT_TITLE_RIDE_LATER,"Enter Pickup Location!");
            return;
        } else if (dropString.length()==0) {
            showAlert(Alerts.ALERT_TITLE_RIDE_LATER,"Enter Drop Location!");
            return;
        } else if (dateString.length()==0) {
            showAlert(Alerts.ALERT_TITLE_RIDE_LATER,"Enter Trip Date!");
            return;
        } else if (timeString.length()==0) {
            showAlert(Alerts.ALERT_TITLE_RIDE_LATER,"Enter Trip Time!");
            return;
        }

        rideLaterTripDataMap.put(Constants.TRIPS_COL_PICKUP_LOCATION, pickupString);
        rideLaterTripDataMap.put(Constants.TRIPS_COL_DROP_LOCATION, dropString);
        rideLaterTripDataMap.put(Constants.TRIPS_COL_TRIP_DATE, dateString);
        rideLaterTripDataMap.put(Constants.TRIPS_COL_TRIP_TIME, timeString);
        rideLaterTripDataMap.put(Constants.TRIPS_COL_RIDER_PHONE, Utils.getMobileNumber(getApplicationContext()));
        rideLaterTripDataMap.put(Constants.TRIPS_COL_DRIVER_ID, String.valueOf(Utils.getDriverID()));
        rideLaterTripDataMap.put(Constants.TRIPS_COL_STATUS, Constants.TRIP_STATUS_UPCOMING);

        DatabaseOperations insertDbOperation = new DatabaseOperations(RideLaterActivity.this);
        int insertStatus = insertDbOperation.insertTripData(rideLaterTripDataMap);

        if (insertStatus==1) {
            AlertDialog alertDialog = new AlertDialog.Builder(RideLaterActivity.this).create();
            alertDialog.setTitle(Alerts.ALERT_TITLE_BOOKING_CONFIRMATION);
            alertDialog.setMessage("Your Ride is booked for "+dropString+" on "+dateString);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });
            alertDialog.show();
        } else {
            showAlert(Alerts.ALERT_TITLE_BOOKING_FAILED,"Failed to book your ride, Please try again!");
        }
    }

    public void showAlert(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(RideLaterActivity.this).create();
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
