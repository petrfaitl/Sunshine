package info.lonerunner.android.sunshine.app;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Petr on 23/09/2014.
 */
public class ForecastAdapter extends CursorAdapter
{
    public ForecastAdapter(Context context, Cursor c, int flags)
    {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        return LayoutInflater.from(context).inflate(R.layout.list_item_forecast_today,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        // Read weather icon ID from cursor
        int weatherId = cursor.getInt(ForecastFragment.COL_WEATHER_ID);

        // Use placeholder image for now
        ImageView iconView = (ImageView)view.findViewById(R.id.list_item_icon);
        iconView.setImageResource(R.drawable.ic_launcher);

        // Read date from cursor
        String dateString = cursor.getString(ForecastFragment.COL_WEATHER_DATE);
        // Find TextView and set formatted date on it
        TextView dateView = (TextView)view.findViewById(R.id.list_item_date_textview);
        dateView.setText(Utility.getFriendlyDayString(context,dateString));

        // Read weather forecast from cursor
        String forecastString = cursor.getString(ForecastFragment.COL_WEATHER_DESC);

        // Find TextView and set weather forecast on it
        TextView forecastView = (TextView)view.findViewById(R.id.list_item_forecast_textview);
        forecastView.setText(forecastString);

        // Read user preference for metric or imperial temperature units

        boolean isMetric = Utility.isMetric(context);

        // Read high temperature from cursor
        double highString = cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
        TextView highView = (TextView)view.findViewById(R.id.list_item_high_textview);
        highView.setText(Utility.formatTemperature(highString, isMetric));

        // Read low temperature from cursor

        double lowString = cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);
        TextView lowView = (TextView)view.findViewById(R.id.list_item_low_textview);
        lowView.setText(Utility.formatTemperature(lowString, isMetric));


    }
}
