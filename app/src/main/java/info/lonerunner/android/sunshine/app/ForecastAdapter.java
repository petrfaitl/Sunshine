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
    private final int VIEW_TYPE_TODAY = 0;
    private final int VIEW_TYPE_FUTURE = 1;

    public ForecastAdapter(Context context, Cursor c, int flags)
    {
        super(context, c, flags);
    }



    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {

        int position = getItemViewType(cursor.getPosition());
        int positionId = -1;
        if(position == VIEW_TYPE_TODAY)
        {
            positionId =  R.layout.list_item_forecast_today;
        }else if(position== VIEW_TYPE_FUTURE)
        {
            positionId =  R.layout.list_item_forecast;
        }

        View view = LayoutInflater.from(context).inflate(positionId,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public int getItemViewType(int position)
    {
        if(position == 0)
        {
            return VIEW_TYPE_TODAY;
        }else
        {
            return VIEW_TYPE_FUTURE;
        }
    }

    @Override
    public int getViewTypeCount()
    {
        return 2;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        // Read weather icon ID from cursor
        int weatherId = cursor.getInt(ForecastFragment.COL_WEATHER_ID);
        ViewHolder holder = (ViewHolder) view.getTag();


        // Use placeholder image for now

        holder.iconView.setImageResource(R.drawable.ic_launcher);

        // Read date from cursor
        String dateString = cursor.getString(ForecastFragment.COL_WEATHER_DATE);
        // Find TextView and set formatted date on it

        holder.dateView.setText(Utility.getFriendlyDayString(context,dateString));

        // Read weather forecast from cursor
        String forecastString = cursor.getString(ForecastFragment.COL_WEATHER_DESC);

        // Find TextView and set weather forecast on it

        holder.forecastView.setText(forecastString);

        // Read user preference for metric or imperial temperature units

        boolean isMetric = Utility.isMetric(context);

        // Read high temperature from cursor
        double highString = cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);

        holder.highView.setText(Utility.formatTemperature(highString, isMetric));

        // Read low temperature from cursor

        double lowString = cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);

        holder.lowView.setText(Utility.formatTemperature(lowString, isMetric));


    }

    static class ViewHolder
    {
       public static ImageView iconView;
       public static TextView dateView;
       public static TextView forecastView;
       public static TextView highView;
       public static TextView lowView;

        public ViewHolder(View view)
        {
            iconView = (ImageView)view.findViewById(R.id.list_item_icon);
            dateView = (TextView)view.findViewById(R.id.list_item_date_textview);
            forecastView = (TextView)view.findViewById(R.id.list_item_forecast_textview);
            highView = (TextView)view.findViewById(R.id.list_item_high_textview);
            lowView = (TextView)view.findViewById(R.id.list_item_low_textview);
        }

    }
}
