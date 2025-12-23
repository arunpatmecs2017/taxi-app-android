package com.atmecs.taxi.Utility;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Random;

public class Utils {
    private static String LOGGED_IN_PREF_KEY = "UserMobile";
    private static int MY_PERMISSIONS_REQUEST_READ_SMS;
    private static int MY_PERMISSIONS_REQUEST_LOCATION;
    /**
     *
     * @param emailAddress
     * @return
     */
    public static boolean isEmailValid(String emailAddress) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (emailAddress.matches(emailPattern) && emailAddress.length() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     * @param context
     * @return
     */
    private static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     *
     * @param context
     * @param mobileNumber
     */
    public static void setMobileNumber(Context context, String mobileNumber) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(LOGGED_IN_PREF_KEY, mobileNumber);
        editor.apply();
    }

    /**
     *
     * @param context
     * @return
     */
    public static String getMobileNumber(Context context) {
        return getPreferences(context).getString(LOGGED_IN_PREF_KEY, "false");
    }

    /**
     *
     * @param context
     */
    public static void removeLoggedinPreferenceKey(Context context) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.remove(LOGGED_IN_PREF_KEY);
        editor.commit();
    }

    /**
     *
     * @return
     */
    public static int getDriverID() {
        int min=1;
        int max=10;
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    public static void askForReadSMSPermission(Activity thisActivity) {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(thisActivity,
                Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
                    Manifest.permission.READ_SMS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(thisActivity,
                        new String[]{Manifest.permission.READ_SMS},
                        MY_PERMISSIONS_REQUEST_READ_SMS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
    }

    public static void askForLocationPermission(Activity thisActivity) {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(thisActivity,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(thisActivity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
    }
}
