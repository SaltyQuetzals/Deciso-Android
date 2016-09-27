package com.example.quont.queueband;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements SuggestedFragment.OnFragmentInteractionListener, QueuedFragment.OnFragmentInteractionListener {

    private static String code;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        code = getIntent().getExtras().getString("code", null);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(1);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToSearch();
            }
        });
        Intent serviceIntent = new Intent(getBaseContext(), SocketService.class);
        serviceIntent.putExtras(getIntent().getExtras());
        startService(serviceIntent);
    }

    private void switchToSearch() {
        Intent intent = new Intent(this, Search.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }


    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(final SuggestionEvent event) throws JSONException {
        mSectionsPagerAdapter.suggestionsFragment.addSong(event.jsonObject);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(final ChangedEvent event) throws JSONException {
        mSectionsPagerAdapter.nowPlayingFragment.setData(event.jsonObject.getJSONObject("song"));
        mSectionsPagerAdapter.queuedFragment.addSong(event.jsonObject.getJSONObject("song"));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(final SuggestionRemoveEvent event) throws JSONException {
        mSectionsPagerAdapter.suggestionsFragment.removeSong(event.string);
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onEventMainThread(final VoteUpdateEvent event) throws JSONException {
//        for (int i = 0; i < mSectionsPagerAdapter.suggestionsFragment.getSongs().size(); i++) {
//            if (event.jsonObject.getString("id").equals(mSectionsPagerAdapter.suggestionsFragment.getSongs())) {
//                mSectionsPagerAdapter.suggestionsFragment.getSongs().set(i, new Song(event.jsonObject.getString("title"), event.jsonObject.getString("author"), event.jsonObject.getString("source"), event.jsonObject.getInt("points"), event.jsonObject.getString("thumbnail"), event.jsonObject.getString("id")));
//            }
//        }
//        mSectionsPagerAdapter.queuedFragment.initializeData();
//    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class NowPlayingFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private ViewGroup viewGroup;
        TextView nowplayingtitle, nowplayingauthor, nowplayingsource;
        ImageView albumArt;

        public NowPlayingFragment() {
        }

        public void setUserVisibleHint(boolean isVisibleToUser) {
            super.setUserVisibleHint(isVisibleToUser);
            if (isVisibleToUser) {
                Activity a = getActivity();
                if (a != null) a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static NowPlayingFragment newInstance(int sectionNumber) {
            NowPlayingFragment fragment = new NowPlayingFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            nowplayingtitle = (TextView) rootView.findViewById(R.id.nowplayingtitle);
            nowplayingauthor = (TextView) rootView.findViewById(R.id.nowplayingauthor);
            nowplayingsource = (TextView) rootView.findViewById(R.id.nowplayingsource);
            albumArt = (ImageView) rootView.findViewById(R.id.albumArt);
            initializeData();
            return rootView;
        }

        public void setData(JSONObject data) throws JSONException {

            nowplayingtitle.setText(data.getString("title"));
            nowplayingauthor.setText(data.getString("author"));
            nowplayingsource.setText(data.getString("source"));
            if (!data.getString("thumbnail").equals("")) {
                Picasso.with(getContext()).load(data.getString("thumbnail")).resize(250, 250).centerCrop().into(albumArt);
            } else {
                nowplayingtitle.setText("No song is playing");
            }
        }

        public void initializeData() {
            RequestQueue queue = Volley.newRequestQueue(getContext());
            String url = "https://deciso.audio/" + code + ".json";
            // Request a string response from the provided URL.
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(final JSONObject response) {
                            // Display the first 500 characters of the response string.
                            try {
                                Log.i("nowplaying", String.valueOf(response.getJSONObject("nowplaying")));
                                setData(response.getJSONObject("nowplaying"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            queue.add(jsonObjectRequest);
            queue.start();
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        NowPlayingFragment nowPlayingFragment;
        SuggestedFragment suggestionsFragment;
        QueuedFragment queuedFragment;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            queuedFragment = QueuedFragment.newInstance("QueuedFragment", "Instance 1", code);
            nowPlayingFragment = NowPlayingFragment.newInstance(1);
            suggestionsFragment = SuggestedFragment.newInstance("SuggestedFragment", "Instance 1", code);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a NowPlayingFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return queuedFragment;
                case 1:
                    return nowPlayingFragment;
                case 2:
                    return suggestionsFragment;
                default:
                    return NowPlayingFragment.newInstance(0);
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Queued";
                case 1:
                    return "Now Playing";
                case 2:
                    return "Suggestions";
            }
            return null;
        }
    }
}
