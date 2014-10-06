package info.lonerunner.android.sunshine.app;

/**
 * Created by Petr on 05/09/2014.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import info.lonerunner.android.sunshine.app.data.WeatherContract;

public class Utility
{
    // Format used for storing dates in the database.  ALso used for converting those strings
// back into date objects for comparison/processing.
    public static final String DATE_FORMAT = "yyyyMMdd";

    /**
     * Helper method to convert the database representation of the date into something to display
     * to users.  As classy and polished a user experience as "20140102" is, we can do better.
     *
     * @param context Context to use for resource localization
     * @param dateStr The db formatted date string, expected to be of the form specified
     *                in Utility.DATE_FORMAT
     * @return a user-friendly representation of the date.
     */
    public static String getFriendlyDayString(Context context, String dateStr) {
        // The day string for forecast uses the following logic:
        // For today: "Today, June 8"
        // For tomorrow:  "Tomorrow"
        // For the next 5 days: "Wednesday" (just the day name)
        // For all days after that: "Mon Jun 8"

        Date todayDate = new Date();
        String todayStr = WeatherContract.getDbDateString(todayDate);
        Date inputDate = WeatherContract.getDateFromDb(dateStr);

        // If the date we're building the String for is today's date, the format
        // is "Today, June 24"
        if (todayStr.equals(dateStr)) {
            String today = context.getString(R.string.today);
            return context.getString(
                    R.string.format_full_friendly_date,
                    today,
                    getFormattedMonthDay(context, dateStr));
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTime(todayDate);
            cal.add(Calendar.DATE, 7);
            String weekFutureString = WeatherContract.getDbDateString(cal.getTime());

            if (dateStr.compareTo(weekFutureString) < 0) {
                // If the input date is less than a week in the future, just return the day name.
                return getDayName(context, dateStr);
            } else {
                // Otherwise, use the form "Mon Jun 3"
                SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
                return shortenedDateFormat.format(inputDate);
            }
        }
    }

    /**
     * Given a day, returns just the name to use for that day.
     * E.g "today", "tomorrow", "wednesday".
     *
     * @param context Context to use for resource localization
     * @param dateStr The db formatted date string, expected to be of the form specified
     *                in Utility.DATE_FORMAT
     * @return
     */
    public static String getDayName(Context context, String dateStr) {
        SimpleDateFormat dbDateFormat = new SimpleDateFormat(Utility.DATE_FORMAT);
        try {
            Date inputDate = dbDateFormat.parse(dateStr);
            Date todayDate = new Date();
            // If the date is today, return the localized version of "Today" instead of the actual
            // day name.
            if (WeatherContract.getDbDateString(todayDate).equals(dateStr)) {
                return context.getString(R.string.today);
            } else {
                // If the date is set for tomorrow, the format is "Tomorrow".
                Calendar cal = Calendar.getInstance();
                cal.setTime(todayDate);
                cal.add(Calendar.DATE, 1);
                Date tomorrowDate = cal.getTime();
                if (WeatherContract.getDbDateString(tomorrowDate).equals(
                        dateStr)) {
                    return context.getString(R.string.tomorrow);
                } else {
                    // Otherwise, the format is just the day of the week (e.g "Wednesday".
                    SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
                    return dayFormat.format(inputDate);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            // It couldn't process the date correctly.
            return "";
        }
    }

    /**
     * Converts db date format to the format "Month day", e.g "June 24".
     * @param context Context to use for resource localization
     * @param dateStr The db formatted date string, expected to be of the form specified
     *                in Utility.DATE_FORMAT
     * @return The day in the form of a string formatted "December 6"
     */
    public static String getFormattedMonthDay(Context context, String dateStr) {
        SimpleDateFormat dbDateFormat = new SimpleDateFormat(Utility.DATE_FORMAT);
        try {
            Date inputDate = dbDateFormat.parse(dateStr);
            SimpleDateFormat monthDayFormat = new SimpleDateFormat("MMMM dd");
            String monthDayString = monthDayFormat.format(inputDate);
            return monthDayString;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
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

    static String formatTemperature(Context context,double temperature, boolean isMetric)
    {
        double temp;
        if (!isMetric)
        {
            temp = 9 * temperature / 5 + 32;
        } else
        {
            temp = temperature;
        }
        return context.getString(R.string.format_temperature, temp);
    }

    static String formatPressure(Context context, double pressure)
    {
        return context.getString(R.string.format_pressure, pressure);
    }

    static String formatHumidity(Context context, double humidity)
    {
        try
        {
            return context.getString(R.string.format_humudity, humidity);
        }catch(Exception e)
        {
            return context.getString(R.string.humidity_na);
        }

    }

    static String formatWindspeed(Context context, double windspeed)
    {
        if(isMetric(context))
        {
            return context.getString(R.string.format_windspeed, windspeed);
        }else
        {
            windspeed = windspeed * 0.621371192;
            return  context.getString(R.string.format_windspeed_miles, windspeed);
        }

    }

    static String formatWind(Context context,double windspeed, double degrees)
    {
        double upperLimit = 348.75;
        if(degrees > upperLimit)
        {
            degrees = degrees % upperLimit;
            System.out.println(degrees);
        }


        String [][] degreesTocardinals =
                              {{"0", "11.25", "N"},
                               {"11.25", "33.75","NNE"},
                               {"33.75", "56.25","NE"},
                               {"56.25", "78.75","ENE"},
                               {"78.75", "101.25","E"},
                               {"101.25", "123.75", "ESE"},
                               {"123.75", "146.25","SE"},
                               {"146.25", "168.75","SSE"},
                               {"168.75", "191.25","S"},
                               {"191.25", "213.75","SSW"},
                               {"213.75", "236.25", "SW"},
                               {"236.25", "258.75", "WSW"},
                               {"258.75", "281.25","W"},
                               {"281.25", "303.75","WNW"},
                               {"303.75", "326.25","NW"},
                               {"326.25", "348.75","NNW"}};

        for(String[] value : degreesTocardinals)
        {
            if(isEqualOrGreater(degrees, value[0]) && isSmaller(degrees,value[1]))
            {
                return new StringBuilder().append(formatWindspeed(context,windspeed)).append(" ").append(value[2]).toString();
            }
        }
        return formatWindspeed(context,windspeed);

    }

    static boolean isEqualOrGreater(double degrees, String value)
    {
        return (degrees >= Double.parseDouble(value));

    }

    static boolean isSmaller(double degrees, String value)
    {
        return (degrees< Double.parseDouble(value));

    }

    static String formatDate(String dateString)
    {
        SimpleDateFormat format = new SimpleDateFormat("EEEE dd MMM");
        Date date = WeatherContract.getDateFromDb(dateString);
        return format.format(date);
    }

    public static int getWeatherArt(int forecast)
    {
        final Integer [][] FORECAST_ARRAY = {
                {800,800, R.drawable.art_clear},
                {801,802, R.drawable.art_light_clouds},
                {803, 804, R.drawable.art_clouds},
                {701,762,R.drawable.art_fog},
                {300,500,R.drawable.art_light_rain},
                {501,531,R.drawable.art_rain},
                {600,622,R.drawable.art_snow},
                {200,232,R.drawable.art_storm},

        };


        for(int i=0; i<FORECAST_ARRAY.length;i++)
        {
            if (forecast >= FORECAST_ARRAY[i][0] && forecast <= FORECAST_ARRAY[i][1])
            {
                return FORECAST_ARRAY[i][2];
            }

        }
        return -1;

    }

    public static int getWeatherIcon(int forecast)
    {
        final Integer [][] FORECAST_ARRAY = {
                {800,800, R.drawable.ic_clear},
                {801,802, R.drawable.ic_light_clouds},
                {803, 804, R.drawable.ic_cloudy},
                {701,762,R.drawable.ic_fog},
                {300,500,R.drawable.ic_light_rain},
                {501,531,R.drawable.ic_rain},
                {600,622,R.drawable.ic_snow},
                {200,232,R.drawable.ic_storm},

        };


        for(int i=0; i<FORECAST_ARRAY.length;i++)
        {
            if (forecast >= FORECAST_ARRAY[i][0] && forecast <= FORECAST_ARRAY[i][1])
            {

                return FORECAST_ARRAY[i][2];
            }

        }
        return -1;

    }
}
