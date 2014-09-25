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
import android.widget.ShareActionProvider;
import android.widget.TextView;

import info.lonerunner.android.sunshine.app.data.WeatherContract;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    private static final String SHARED_HASHTAG = "#SunshineApp";
    private String mReceivedDate;
    private ShareActionProvider mShareActionProvider;
    private static final int DETAIL_LOADER = 0;
    private String mForecastString;
    public static final String DATE = "date";
    private static final String LOCATION_KEY = "location";

    private String mLocation;

    private String[] FORECAST_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
    };

    public DetailFragment()
    {

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Intent detailIntent = getActivity().getIntent();
        mReceivedDate = detailIntent.getStringExtra(DATE);


        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

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
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);


    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (mLocation != null && mLocation.equals(Utility.getPreferredLocation(getActivity())))
        {
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }


    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {


        mLocation = Utility.getPreferredLocation(getActivity());
        Uri uriWithDate = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(mLocation, mReceivedDate);


        return new CursorLoader(
                getActivity(),
                uriWithDate,
                FORECAST_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor data)
    {
        if (!data.moveToFirst())
        {
            return;
        }
        boolean isMetric = Utility.isMetric(getActivity());


        String mDetailForecast = data.getString(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC));
        double mDetailMax = data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP));
        double mDetailMin = data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP));
        String dateString = Utility.formatDate(mReceivedDate);
        String maxTemp = Utility.formatTemperature(getActivity(), mDetailMax, isMetric);
        String minTemp = Utility.formatTemperature(getActivity(), mDetailMin, isMetric);
        double humidity = data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_HUMIDITY));
        double pressure = data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_PRESSURE));
        double wind = data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED));
        double degrees = data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DEGREES));
        String humidityString = ;
        String pressureString = ;
        String windString= ;
        String degreesString = ;

        TextView forecastText = (TextView) getView().findViewById(R.id.detail_forecast_text);
        TextView dateText = (TextView) getView().findViewById(R.id.detail_date_text);
        TextView minTempText = (TextView) getView().findViewById(R.id.detail_low_text);
        TextView maxTempText = (TextView) getView().findViewById(R.id.detail_high_text);
        TextView humidityText =(TextView) getView().findViewById(R.id.humidity);


        dateText.setText(dateString);
        maxTempText.setText(maxTemp);
        minTempText.setText(minTemp);
        forecastText.setText(mDetailForecast);

        //mForecastString = String.format("%s - %s - %s/%s", dateString, mDetailForecast, maxTemp, minTemp);

        //Log.v("forecast string", mForecastString);

        if (mShareActionProvider != null)
        {
            mShareActionProvider.setShareIntent(createShareIntent());
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader)
    {
        getLoaderManager().restartLoader(DETAIL_LOADER, null, this);

    }
}
