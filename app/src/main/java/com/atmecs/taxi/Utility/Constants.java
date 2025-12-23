package com.atmecs.taxi.Utility;

public class Constants {

    /*
        Login table details
     */
    public final static String USERS_TABLE_NAME = "Users";
    public final static String USERS_COL_FIRST_NAME = "FirstName";
    public final static String USERS_COL_LAST_NAME = "LastName";
    public final static String USERS_COL_PHONE_NUMBER = "PhoneNumber";
    public final static String USERS_COL_EMAIL_ADDRESS = "EmailAddress";
    public final static String USERS_COL_PASSWORD = "Password";

    /*
        Trips table details
     */
    public final static String TRIPS_TABLE_NAME = "Trips";
    public final static String TRIPS_COL_TRIP_ID = "TripID";
    public final static String TRIPS_COL_PICKUP_LOCATION = "PickupLocation";
    public final static String TRIPS_COL_DROP_LOCATION = "DropLocation";
    public final static String TRIPS_COL_TRIP_DATE = "TripDate";
    public final static String TRIPS_COL_TRIP_TIME = "TripTime";
    public final static String TRIPS_COL_RIDER_PHONE = "RiderPhone";
    public final static String TRIPS_COL_DRIVER_ID = "DriverID";
    public final static String TRIPS_COL_STATUS = "Status";

    // Trip Status Constant Strings
    public final static String TRIP_STATUS_IN_PROGRESS = "In-Progress";
    public final static String TRIP_STATUS_CANCELLED = "Cancelled";
    public final static String TRIP_STATUS_COMPLETED = "Completed";
    public final static String TRIP_STATUS_UPCOMING = "Upcoming";
    public final static String TRIP_STATUS_NOT_AVAILABLE = "Not Available";

    /*
        Drivers table details
     */
    public final static String DRIVERS_TABLE_NAME = "Drivers";
    public final static String DRIVERS_COL_DRIVER_NAME = "DriverName";
    public final static String DRIVERS_COL_DRIVER_PHONE = "DriverPhone";
    public final static String DRIVERS_COL_TAXI_NUMBER = "TaxiNumber";

    // Mail
    public static final String INBOX = "content://sms/inbox";
    public static final String SENT = "content://sms/sent";
    public static final String DRAFT = "content://sms/draft";
}
