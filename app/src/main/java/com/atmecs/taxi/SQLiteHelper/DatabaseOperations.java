package com.atmecs.taxi.SQLiteHelper;

import android.content.Context;

import com.atmecs.taxi.Utility.Constants;

import java.util.HashMap;
import java.util.List;

public class DatabaseOperations {

    Context context ;
    DatabaseAccess databaseAccess;


    public DatabaseOperations(Context context) {
        this.context = context;
        databaseAccess = DatabaseAccess.getInstance(context);
    }

    public int insertUserData(HashMap<String, String> insertMap) {
        databaseAccess.open();
        int insertStatus = databaseAccess.insertDataInUsersTable(Constants.USERS_TABLE_NAME, insertMap);
        databaseAccess.close();
        return insertStatus;
    }

    public int updateUserData(HashMap<String, String> updateMap, String phoneNumber) {
        databaseAccess.open();
        int updateStatus = databaseAccess.updateDataInUsersTable(Constants.USERS_TABLE_NAME, updateMap, phoneNumber);
        databaseAccess.close();
        return updateStatus;
    }

    public HashMap<String, String> fetchUserData(String phoneNumber) {
        databaseAccess.open();
        HashMap<String, String> userDataMap = databaseAccess.fetchUserDetails(Constants.USERS_TABLE_NAME, phoneNumber);
        databaseAccess.close();
        return userDataMap;
    }

    public int updatePassword(String newPassword, String phoneNumber) {
        databaseAccess.open();
        int updateStatus = databaseAccess.updatePassword(Constants.USERS_TABLE_NAME, newPassword, phoneNumber);
        databaseAccess.close();
        return updateStatus;

    }

    public int insertTripData(HashMap<String, String> insertTripMap) {
        databaseAccess.open();
        int insertStatus = databaseAccess.insertDataInTripsTable(Constants.TRIPS_TABLE_NAME, insertTripMap);
        databaseAccess.close();
        return insertStatus;
    }

    public List<HashMap<String, String>> fetchTripData(String phoneNumber) {
        databaseAccess.open();
        List<HashMap<String, String>> tripDataMap = databaseAccess.fetchTripDetails(phoneNumber);
        databaseAccess.close();
        return tripDataMap;
    }

    public int deleteUser(String phoneNumber) {
        databaseAccess.open();
        int updateStatus = databaseAccess.deleteUser(phoneNumber);
        databaseAccess.close();
        return updateStatus;
    }
}
