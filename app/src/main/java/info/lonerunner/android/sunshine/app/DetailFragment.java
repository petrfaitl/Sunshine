package info.lonerunner.android.sunshine.app;


import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import info.lonerunner.android.sunshine.app.data.WeatherContract;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    private static final String SHARED_HASHTAG = "#SunshineApp";
    private ShareActionProvider mShareActionProvider;
    private static final int DETAIL_LOADER = 0;
    private String mForecastString;
    public static final String DATE = "date";
    private static final String LOCATION_KEY = "location";

    private String mLocation;

    private String[] FORECAST_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATETEXT,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,

            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING
    };

    private ImageView mIconView;
    private TextView mFriendlyDateView;
    private TextView mDateView;
    private TextView mDescriptionView;
    private TextView mHighTempView;
    private TextView mLowTempView;
    private TextView mHumidityView;
    private TextView mWindSpeed;
    private TextView mWindDirection;
    private TextView mPressureView;

    public DetailFragment()
    {

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {



        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mIconView = (ImageView) rootView.findViewById(R.id.detail_icon);
        mDateView = (TextView) rootView.findViewById(R.id.detail_date_text);
        mFriendlyDateView = (TextView) rootView.findViewById(R.id.detail_day_text);
        mDescriptionView = (TextView) rootView.findViewById(R.id.detail_forecast_text);
        mHighTempView = (TextView) rootView.findViewById(R.id.detail_high_text);
        mLowTempView = (TextView) rootView.findViewById(R.id.detail_low_text);
        mHumidityView = (TextView) rootView.findViewById(R.id.detail_humidity_text);
        mWindSpeed = (TextView) rootView.findViewById(R.id.detail_windspeed_text);
        mPressureView = (TextView) rootView.findViewById(R.id.detail_pressure_text);

        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        // Inflate menu resource file.
        inflater.inflate(R.menu.detailsfragment, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.action_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) item.getActionProvider();

        // Return true to display menu
        setShareIntent();
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setShareIntent()
    {
        Intent shareIntent = createShareIntent();
        if (mShareActionProvider != null)
        {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    private Intent createShareIntent()
    {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mForecastString + " " + SHARED_HASHTAG);
        return shareIntent;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        if (mLocation != null)
        {
            outState.putString(LOCATION_KEY, mLocation);
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null)
        {
            mLocation = savedInstanceState.getString(LOCATION_KEY);
        }
        Intent intent = getActivity().getIntent();
        if(intent != null && intent.hasExtra(DATE))
        {
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);

        }



    }

    @Override
    public void onResume()
    {
        super.onResume();
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(DATE) && mLocation != null && mLocation.equals(Utility.getPreferredLocation(getActivity())))
        {
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }


    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        Intent intent = getActivity().getIntent();
        if (intent == null || !intent.hasExtra(DATE))
        {
            return null;
        }

        String receivedDate = getActivity().getIntent().getStringExtra(DATE);
        mLocation = Utility.getPreferredLocation(getActivity());
        Uri uriWithDate = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(mLocation, receivedDate);
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATETEXT + " ASC";


        return new CursorLoader(
                getActivity(),
                uriWithDate,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor data)
    {
        if (data != null && data.moveToFirst())
        {
            int weatherCode = data.getInt(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID));
            mIconView.setImageResource(Utility.getWeatherArt(weatherCode));

            // Read date from cursor and update views for day of week and date
            String date = data.getString(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATETEXT));
            String friendlyDateText = Utility.getDayName(getActivity(), date);
            String dateText = Utility.getFormattedMonthDay(getActivity(), date);
            mFriendlyDateView.setText(friendlyDateText);
            mDateView.setText(dateText);

            // Read description from cursor and update view
            String description = data.getString(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC));
            mDescriptionView.setText(description);

            // Read high temperature from cursor and update view
            boolean isMetric = Utility.isMetric(getActivity());

            double high = data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP));
            String highString = Utility.formatTemperature(getActivity(), high, isMetric);
            mHighTempView.setText(highString);

            // Read low temperature from cursor and update view
            double low = data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP));
            String lowString = Utility.formatTemperature(getActivity(), low, isMetric);
            mLowTempView.setText(lowString);

            // Read humidity from cursor and update view
           double humidity = data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_HUMIDITY));
           mHumidityView.setText(Utility.formatHumidity(getActivity(),humidity));

            // Read wind speed and direction from cursor and update view
            double windSpeedStr = data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED));
            double windDirStr = data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DEGREES));
            //mWindView.setText(Utility.(getActivity(), windSpeedStr, windDirStr));
            mWindSpeed.setText(Utility.formatWind(getActivity(),windSpeedStr,windDirStr));

            // Read pressure from cursor and update view
            double pressure = data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_PRESSURE));
            mPressureView.setText(getActivity().getString(R.string.format_pressure, pressure));




            if (mShareActionProvider != null)
            {
                mShareActionProvider.setShareIntent(createShareIntent());
            }
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader)
    {
        getLoaderManager().restartLoader(DETAIL_LOADER, null, this);

    }
}
