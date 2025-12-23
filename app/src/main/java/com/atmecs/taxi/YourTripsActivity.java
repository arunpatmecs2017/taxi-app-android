package com.atmecs.taxi;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.atmecs.taxi.SQLiteHelper.DatabaseOperations;
import com.atmecs.taxi.Utility.Constants;
import com.atmecs.taxi.Utility.DateUtils;
import com.atmecs.taxi.Utility.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class YourTripsActivity extends AppCompatActivity {

    HashMap<String, String> tripHistoryMap;
    List<HashMap<String, String>> tripsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yourtrips);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Your Trips");

        tripHistoryMap = new HashMap<>();
        tripsList = new ArrayList<>();

        DatabaseOperations dbOperation = new DatabaseOperations(YourTripsActivity.this);
        String phoneString = Utils.getMobileNumber(getApplicationContext());
        tripsList = dbOperation.fetchTripData(phoneString);
        System.out.println("tripsList Size:: "+tripsList.size());

        String[] tripsArray = new String[tripsList.size()];

        for (int index=0; index<tripsList.size(); index++) {
            tripHistoryMap = tripsList.get(index);
            String fromLocationString = tripHistoryMap.get(Constants.TRIPS_COL_PICKUP_LOCATION);
            String toLocationString = tripHistoryMap.get(Constants.TRIPS_COL_DROP_LOCATION);
            String dateString = tripHistoryMap.get(Constants.TRIPS_COL_TRIP_DATE);
            String timeString = tripHistoryMap.get(Constants.TRIPS_COL_TRIP_TIME);
            String driverNameString = tripHistoryMap.get(Constants.DRIVERS_COL_DRIVER_NAME);
            String taxiNumberString = tripHistoryMap.get(Constants.DRIVERS_COL_TAXI_NUMBER);
//            String statusString = tripHistoryMap.get(Constants.TRIPS_COL_STATUS);
//            System.out.println(fromLocationString+" to "+toLocationString+"\n"+dateString+", "+timeString+"\nDriver: "+driverNameString+"\nTaxi Number: "+taxiNumberString);

            String statusString = getTripStatus(dateString, timeString);

            String trip = fromLocationString+" to "+toLocationString+"\n"+dateString+", "+timeString+"\nDriver: "+driverNameString+"\nTaxi Number: "+taxiNumberString+"\nStatus: "+statusString;

            tripsArray[index] = trip;
        }

//        String[] tripsArray = {"Manikonda to Kondapur\n09/07/2019, 08:30\nRamesh Kumar\nTaxi Number: 3321",
//                "Manikonda to Kondapur\n09/07/2019, 08:30\nRamesh Kumar\nTaxi Number: 3321"};

        ArrayAdapter adapter = new ArrayAdapter<>(this,
                R.layout.activity_trips_listview, tripsArray);

        ListView settingsListView = findViewById(R.id.trips_list);
        settingsListView.setAdapter(adapter);

//        settingsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            public void onItemClick(AdapterView<?> parentAdapter, View view, int position,
//                                    long id) {
//                if(position==0) {
//                    Intent changePasswordIntent = new Intent(YourTripsActivity.this, ChangePasswordActivity.class);
//                    startActivity(changePasswordIntent);
//                } else if (position==1) {
//                    showAlert(Alerts.ALERT_TITLE_DELETE_ACCOUNT, "Are you sure you want to delete your account?");
//                }
//            }
//        });
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

    private String getTripStatus(String date, String time) {
        String tripStatus = Constants.TRIP_STATUS_NOT_AVAILABLE;
        String tripDateString = date+" "+time;
        String tripDateFormat = "dd/MM/yyyy HH:mm";

        SimpleDateFormat tripSimpleDateFormat = new SimpleDateFormat(tripDateFormat);

        String currentDateString = DateUtils.getCurrentDateTime(tripDateFormat);

        Date tripDate = null;
        Date currentDate = null;

        try {
            tripDate = tripSimpleDateFormat.parse(tripDateString);
            currentDate = tripSimpleDateFormat.parse(currentDateString);
        } catch (ParseException parseException) {
            parseException.getLocalizedMessage();
        }

        if (tripDate!=null) {
            int result = tripDate.compareTo(currentDate);
            System.out.println("result: " + result);

            if (result == 0) {
                tripStatus = Constants.TRIP_STATUS_IN_PROGRESS;
            } else if (result > 0) {
                tripStatus = Constants.TRIP_STATUS_UPCOMING;
            } else if (result < 0) {
                tripStatus = Constants.TRIP_STATUS_COMPLETED;
            } else {
                tripStatus = Constants.TRIP_STATUS_NOT_AVAILABLE;
            }
        } else {
            System.out.println("Failed to get trip date");
        }

        return tripStatus;
    }
}
