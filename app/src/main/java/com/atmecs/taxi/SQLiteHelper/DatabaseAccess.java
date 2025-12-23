package com.atmecs.taxi.SQLiteHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.atmecs.taxi.Utility.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseAccess  {

    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static DatabaseAccess instance;

    // private constructor to avoid object creation from outside class
    private DatabaseAccess(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
    }

    // to return single instance of database
    public static DatabaseAccess getInstance(Context context) {
        if(instance==null){
            instance = new DatabaseAccess(context);
        }

        return instance;
    }

    // to open database
    public void open(){
        this.database = openHelper.getWritableDatabase();
    }

    // to close database
    public void close() {
        if(database!=null){
            this.database.close();
        }
    }

    public int insertDataInUsersTable(String tableName, HashMap<String, String> insertMap) {

        try {

            ContentValues newValues = new ContentValues();
            newValues.put(Constants.USERS_COL_FIRST_NAME, insertMap.get(Constants.USERS_COL_FIRST_NAME));
            newValues.put(Constants.USERS_COL_LAST_NAME, insertMap.get(Constants.USERS_COL_LAST_NAME));
            newValues.put(Constants.USERS_COL_PHONE_NUMBER, insertMap.get(Constants.USERS_COL_PHONE_NUMBER));
            newValues.put(Constants.USERS_COL_EMAIL_ADDRESS, insertMap.get(Constants.USERS_COL_EMAIL_ADDRESS));
            newValues.put(Constants.USERS_COL_PASSWORD, insertMap.get(Constants.USERS_COL_PASSWORD));

            long rowInserted = database.insert(tableName, null, newValues);
            if(rowInserted != -1)
                return 1;
            else
                return 0;

        } catch (Exception ex) {
            return 2;
        }
    }

    public int updateDataInUsersTable(String tableName, HashMap<String, String> updateMap, String phoneNumber) {
        try {

            ContentValues newValues = new ContentValues();
            newValues.put(Constants.USERS_COL_FIRST_NAME, updateMap.get(Constants.USERS_COL_FIRST_NAME));
            newValues.put(Constants.USERS_COL_LAST_NAME, updateMap.get(Constants.USERS_COL_LAST_NAME));
            newValues.put(Constants.USERS_COL_PHONE_NUMBER, updateMap.get(Constants.USERS_COL_PHONE_NUMBER));
            newValues.put(Constants.USERS_COL_EMAIL_ADDRESS, updateMap.get(Constants.USERS_COL_EMAIL_ADDRESS));

            long rowUpdated = database.update(tableName, newValues, String.format("%s = ?", Constants.USERS_COL_PHONE_NUMBER), new String[]{phoneNumber});
            if(rowUpdated != -1)
                return 1;
            else
                return 0;

        } catch (Exception ex) {
            return 2;
        }
    }

    public int updatePassword(String tableName, String newPassword, String phoneNumber) {
        try {

            ContentValues newValues = new ContentValues();
            newValues.put(Constants.USERS_COL_PASSWORD, newPassword);

            long rowUpdated = database.update(tableName, newValues, String.format("%s = ?", Constants.USERS_COL_PHONE_NUMBER), new String[]{phoneNumber});
            if(rowUpdated != -1)
                return 1;
            else
                return 0;

        } catch (Exception ex) {
            return 2;
        }
    }

    public HashMap<String, String> fetchUserDetails(String tableName, String phoneNumber) {
        HashMap<String, String> fetchedDataMap = new HashMap<>();
        String queryString = "SELECT * FROM "+tableName+" where PhoneNumber="+phoneNumber+";";
        try {
            Log.d("queryString", ""+queryString);
            Cursor cursor = database.rawQuery(queryString, null);
            Log.d("cursor.count::", ""+cursor.getCount());
            cursor.moveToFirst();
            int coulumnCount = cursor.getColumnCount();
            while (!cursor.isAfterLast()) {
                for (int index=0; index<coulumnCount; index++) {
                    String cellValue = cursor.getString(index);
                    String keyName = cursor.getColumnName(index);
                    Log.d("cellValue::", ""+cellValue);
                    fetchedDataMap.put(keyName, cellValue);
                }
                cursor.moveToNext();
            }
            cursor.close();
        } catch (SQLException mSQLException) {
            System.out.println("getData >>" + mSQLException.toString());
            Log.d("getData >>", ""+mSQLException.toString());
            throw mSQLException;
        }

        return fetchedDataMap;
    }

    public int insertDataInTripsTable(String tableName, HashMap<String, String> insertTripMap) {

        try {

            ContentValues newValues = new ContentValues();
            newValues.put(Constants.TRIPS_COL_PICKUP_LOCATION, insertTripMap.get(Constants.TRIPS_COL_PICKUP_LOCATION));
            newValues.put(Constants.TRIPS_COL_DROP_LOCATION, insertTripMap.get(Constants.TRIPS_COL_DROP_LOCATION));
            newValues.put(Constants.TRIPS_COL_TRIP_DATE, insertTripMap.get(Constants.TRIPS_COL_TRIP_DATE));
            newValues.put(Constants.TRIPS_COL_TRIP_TIME, insertTripMap.get(Constants.TRIPS_COL_TRIP_TIME));
            newValues.put(Constants.TRIPS_COL_RIDER_PHONE, insertTripMap.get(Constants.TRIPS_COL_RIDER_PHONE));
            newValues.put(Constants.TRIPS_COL_DRIVER_ID, insertTripMap.get(Constants.TRIPS_COL_DRIVER_ID));
            newValues.put(Constants.TRIPS_COL_STATUS, insertTripMap.get(Constants.TRIPS_COL_STATUS));

            long rowInserted = database.insert(tableName, null, newValues);
            if(rowInserted != -1)
                return 1;
            else
                return 0;

        } catch (Exception ex) {
            return 2;
        }
    }

    public int updateTripStatus(String tableName, String tripStatus, String tripID) {

        try {
            ContentValues newValues = new ContentValues();
            newValues.put(Constants.TRIPS_COL_STATUS, tripStatus);
            long rowUpdated = database.update(tableName, newValues, String.format("%s = ?", Constants.TRIPS_COL_TRIP_ID), new String[]{tripID});
            if(rowUpdated != -1)
                return 1;
            else
                return 0;

        } catch (Exception ex) {
            return 2;
        }
    }

    public List<HashMap<String, String>> fetchTripDetails(String phoneNumber) {
        List<HashMap<String, String>> fetchedObjectsList = new ArrayList<>();
//        String queryString = "SELECT * FROM "+tableName+" where PhoneNumber="+phoneNumber+";";
        String queryString = "SELECT * FROM Trips INNER JOIN Drivers ON Trips.DriverID=Drivers.DriverID where Trips.RiderPhone="+phoneNumber+";";
        try {
            Log.d("queryString", ""+queryString);
            Cursor cursor = database.rawQuery(queryString, null);
            Log.d("cursor.count::", ""+cursor.getCount());
            cursor.moveToFirst();
            int coulumnCount = cursor.getColumnCount();
            while (!cursor.isAfterLast()) {
                HashMap<String, String> fetchedDataMap = new HashMap<>();
                for (int index=0; index<coulumnCount; index++) {
                    String cellValue = cursor.getString(index);
                    String keyName = cursor.getColumnName(index);
                    Log.d("cellValue::", ""+cellValue);
                    fetchedDataMap.put(keyName, cellValue);
                }
                fetchedObjectsList.add(fetchedDataMap);
                cursor.moveToNext();
            }
            cursor.close();
        } catch (SQLException mSQLException) {
            System.out.println("getData >>" + mSQLException.toString());
            Log.d("getData >>", ""+mSQLException.toString());
            throw mSQLException;
        }

        return fetchedObjectsList;
    }

    public int deleteUser(String phoneNumber) {
        int deleteStatus = 0;
        String queryString = "DELETE FROM Users where PhoneNumber="+phoneNumber+";";
        try {
            Log.d("queryString", ""+queryString);
            Cursor cursor = database.rawQuery(queryString, null);
            deleteStatus = 1;
            cursor.close();
        } catch (SQLException mSQLException) {
            System.out.println("getData >>" + mSQLException.toString());
            Log.d("getData >>", ""+mSQLException.toString());
            throw mSQLException;
        }

        return deleteStatus;
    }
}