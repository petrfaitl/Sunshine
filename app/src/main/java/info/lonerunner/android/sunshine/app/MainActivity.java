package info.lonerunner.android.sunshine.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends Activity implements ForecastFragment.Callback
{
    private boolean mTwoPane;




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(findViewById(R.id.weather_detail_container) != null)
        {
            mTwoPane = true;

            if(savedInstanceState == null)
            {
                getFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, new DetailFragment())
                        .commit();
            }
        }else
        {
            mTwoPane = false;
        }

        ForecastFragment forecastFragment = (ForecastFragment) getFragmentManager().findFragmentById(R.id.fragment_forecast);
        forecastFragment.setUseTodayLayout(!mTwoPane);



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        //super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            Intent settingsIntent = new Intent(this,SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }else if(id == R.id.display_pref_location_on_map)
        {
            openPrefLocationOnMap();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openPrefLocationOnMap()
    {
        Intent geoIntent = new Intent(Intent.ACTION_VIEW);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String location = prefs.getString(getString(R.string.pref_location_key),getString(R.string.pref_location_default));

        Uri geoLocation = Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q",location)
                .build();
        geoIntent.setData(geoLocation);
        if(geoIntent.resolveActivity(getPackageManager()) != null)
        {
            startActivity(geoIntent);
        }else
        {
            installMapsApp();

        }

    }

    private void installMapsApp()
    {
        Uri playMarketLink = Uri.parse("https://play.google.com/store/search?q=maps");
        Intent installIntent = new Intent(Intent.ACTION_VIEW);
        installIntent.setData(playMarketLink);
        startActivity(installIntent);

    }


    @Override
    public void onItemSelected(String date)
    {
        if(mTwoPane)
        {
            Bundle args = new Bundle();
            args.putString(DetailActivity.DATE_KEY, date);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container,fragment)
                    .commit();
        }else
        {
            Intent intent = new Intent(this, DetailActivity.class)
                    .putExtra(DetailActivity.DATE_KEY, date);
            startActivity(intent);
        }
    }
}
