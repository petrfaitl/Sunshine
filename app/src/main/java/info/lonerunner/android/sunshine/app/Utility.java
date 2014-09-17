package info.lonerunner.android.sunshine.app;

/**
 * Created by Petr on 05/09/2014.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Date;

import info.lonerunner.android.sunshine.app.data.WeatherContract;

public class Utility
{
    public static String getPreferredLocation(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_location_key),
                               context.getString(R.string.pref_location_default));
    }

    public static boolean isMetric(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_units_key),
                               context.getString(R.string.pref_units_metric))
                .equals(context.getString(R.string.pref_units_metric));
    }

    static String formatTemperature(double temperature, boolean isMetric)
    {
        double temp;
        if (!isMetric)
        {
            temp = 9 * temperature / 5 + 32;
        } else
        {
            temp = temperature;
        }
        return String.format("%.0f", temp);
    }

    static String formatDate(String dateString)
    {
        SimpleDateFormat format = new SimpleDateFormat("EEEE dd MMM");
        Date date = WeatherContract.getDateFromDb(dateString);
        return format.format(date);
    }
}
