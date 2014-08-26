package app.sunshine.android.lonerunner.info.sunshine;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ShareActionProvider;
import android.widget.TextView;


public class DetailActivity extends Activity
{


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null)
        {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);

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
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public class DetailFragment extends Fragment
    {
        private static final String SHARED_HASHTAG = "#SunshineApp";
        private String mReceivedForecast;
        private ShareActionProvider mShareActionProvider;

        public DetailFragment()
        {

            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState)
        {

            Intent detailIntent = getActivity().getIntent();
            Bundle extras = detailIntent.getExtras();
            mReceivedForecast = extras.getString(Intent.EXTRA_TEXT);
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            if (detailIntent != null && extras != null)
            {
                TextView displayedText = (TextView) rootView.findViewById(R.id.detail_forecast_text);
                displayedText.setText(mReceivedForecast);
            }


            return rootView;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
        {
            // Inflate menu resource file.
            getMenuInflater().inflate(R.menu.detailsfragment, menu);

            // Locate MenuItem with ShareActionProvider
            MenuItem item = menu.findItem(R.id.action_share);

            // Fetch and store ShareActionProvider
            mShareActionProvider = (ShareActionProvider) item.getActionProvider();

            // Return true to display menu
            setShareIntent();
            super.onCreateOptionsMenu(menu,getMenuInflater());
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
            shareIntent.putExtra(Intent.EXTRA_TEXT, mReceivedForecast + " " + SHARED_HASHTAG);
            return shareIntent;
        }


    }


}
