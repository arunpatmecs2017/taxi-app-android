package com.atmecs.taxi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.navigation.NavigationView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.atmecs.taxi.SQLiteHelper.DatabaseOperations;
import com.atmecs.taxi.Utility.Alerts;
import com.atmecs.taxi.Utility.Constants;
import com.atmecs.taxi.Utility.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback,
        NavigationView.OnNavigationItemSelectedListener, LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;

    HashMap<String, String> rideLaterTripDataMap;
    EditText pickupText;
    EditText dropText;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setTitle("Book Taxi");

        pickupText = findViewById(R.id.txtPickup);
        dropText = findViewById(R.id.txtDrop);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button loginButton = (Button) findViewById(R.id.btnRideLater);
        loginButton.setMovementMethod(LinkMovementMethod.getInstance());
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, RideLaterActivity.class);
                intent.putExtra("pickupLocationString", pickupText.getText().toString().trim());
                intent.putExtra("dropLocationString", dropText.getText().toString().trim());
                startActivity(intent);
            }
        });

        // Get the widget reference from xml layout
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigationView);

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close);

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView.setNavigationItemSelectedListener(HomeActivity.this);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        clearFields();
        int id = item.getItemId();
        switch (id) {
            case R.id.profile:
                Intent profileIntent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(profileIntent);
                break;
            case R.id.history:
                Intent historyIntent = new Intent(HomeActivity.this, YourTripsActivity.class);
                startActivity(historyIntent);
                break;
            case R.id.help:
                Intent helpIntent = new Intent(HomeActivity.this, HelpActivity.class);
                startActivity(helpIntent);
                break;
            case R.id.settings:
                Intent settingsIntent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;
            case R.id.logout:
                Utils.removeLoggedinPreferenceKey(getApplicationContext());
                finish();
                break;
            default:
                return true;
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    public void rideNowClicked(View view) {
        rideLaterTripDataMap = new HashMap<>();

        Date currentTime = new Date(System.currentTimeMillis() + 5 * 60 * 1000);
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy_HH:mm");
        String formattedDate = df.format(currentTime);
        String[] dateTimeArray = formattedDate.split("_");

        String dateString = dateTimeArray[0];
        String timeString = dateTimeArray[1];
        String pickupString = pickupText.getText().toString().trim();
        String dropString = dropText.getText().toString().trim();

        if (pickupString.length() == 0) {
            showAlert(Alerts.ALERT_TITLE_RIDE_LATER, "Enter Pickup Location!");
            return;
        } else if (dropString.length() == 0) {
            showAlert(Alerts.ALERT_TITLE_RIDE_LATER, "Enter Drop Location!");
            return;
        }

        rideLaterTripDataMap.put(Constants.TRIPS_COL_PICKUP_LOCATION, pickupString);
        rideLaterTripDataMap.put(Constants.TRIPS_COL_DROP_LOCATION, dropString);
        rideLaterTripDataMap.put(Constants.TRIPS_COL_TRIP_DATE, dateString);
        rideLaterTripDataMap.put(Constants.TRIPS_COL_TRIP_TIME, timeString);
        rideLaterTripDataMap.put(Constants.TRIPS_COL_RIDER_PHONE, Utils.getMobileNumber(getApplicationContext()));
        rideLaterTripDataMap.put(Constants.TRIPS_COL_DRIVER_ID, String.valueOf(Utils.getDriverID()));
        rideLaterTripDataMap.put(Constants.TRIPS_COL_STATUS, Constants.TRIP_STATUS_UPCOMING);

        DatabaseOperations insertDbOperation = new DatabaseOperations(HomeActivity.this);
        int insertStatus = insertDbOperation.insertTripData(rideLaterTripDataMap);

        if (insertStatus == 1) {
            AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this).create();
            alertDialog.setTitle(Alerts.ALERT_TITLE_BOOKING_CONFIRMATION);
            alertDialog.setMessage("Your Ride is booked for " + dropString + " on " + dateString);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            clearFields();
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        } else {
            showAlert(Alerts.ALERT_TITLE_BOOKING_FAILED, "Failed to book your ride, Please try again!");
        }
    }

    public void showAlert(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this).create();
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
        pickupText.setText("");
        dropText.setText("");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void searchLocation(View view) {
        EditText locationSearch = findViewById(R.id.txtPickup);
        String location = locationSearch.getText().toString();
        List<Address> addressList = null;

        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title(location));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
//            Toast.makeText(getApplicationContext(),address.getLatitude()+" "+address.getLongitude(),Toast.LENGTH_LONG).show();
        }
    }
}
