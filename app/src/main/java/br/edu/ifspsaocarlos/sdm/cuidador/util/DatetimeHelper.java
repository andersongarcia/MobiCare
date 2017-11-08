package br.edu.ifspsaocarlos.sdm.cuidador.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Anderson on 08/11/2017.
 */

public class DatetimeHelper {
    public static Calendar getThisTimeToday(String time){
        String array[];
        array = time.split(":");
        int hour = Integer.parseInt(array[0]);
        int minute = Integer.parseInt(array[1]);

        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(System.currentTimeMillis()); //set the current time and date for this calendar

        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);

        return cal;
    }

    public static String getFormatedDate(Calendar date, String format){
        SimpleDateFormat s = new SimpleDateFormat(format);
        return s.format(date.getTime());
    }
}
