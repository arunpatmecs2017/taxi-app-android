package com.atmecs.taxi.Utility;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    public static String getCurrentDateTime(String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date date = new Date();

        return formatter.format(date);
    }
}
