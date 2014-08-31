package app.sunshine.android.lonerunner.info.sunshine;



import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ShareActionProvider;
import android.widget.TextView;


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
        inflater.inflate(R.menu.detailsfragment, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.action_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) item.getActionProvider();

        // Return true to display menu
        setShareIntent();
        super.onCreateOptionsMenu(menu,inflater);
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
