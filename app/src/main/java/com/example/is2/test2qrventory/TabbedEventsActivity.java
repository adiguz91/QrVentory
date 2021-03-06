package com.example.is2.test2qrventory;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.example.is2.test2qrventory.connection.EventAccess;
import com.example.is2.test2qrventory.connection.VolleyResponseListener;
import com.example.is2.test2qrventory.model.Domain;
import com.example.is2.test2qrventory.model.Event;
import com.example.is2.test2qrventory.model.User;

import java.util.ArrayList;
import java.util.List;

public class TabbedEventsActivity extends AppCompatActivity implements ClosedEventsFragment.OnFragmentInteractionListener, OpenEventsFragment.OnFragmentInteractionListener, FinishedEventFragment.OnFragmentInteractionListener {

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
    private TabLayout tabLayout;
    private ProgressDialog pDialog;
    User user = null;
    Domain domain = null;
    //Event event = null;
    List<Event> events = new ArrayList<>();
    private com.github.clans.fab.FloatingActionButton fab_add_event;

    public List<Event> getEvents() { return events; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed_events);

        user = getIntent().getParcelableExtra("user");
        domain = getIntent().getParcelableExtra("domain");

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);
        //mViewPager.setAdapter(mSectionsPagerAdapter);


        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        fab_add_event = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.menu_item_add_event);

        fab_add_event.setOnClickListener(onAddEventHandler);

    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    if (extras != null) {


                        finish();
                        startActivity(getIntent());
                        //itemAccess.getEventItemsThatNotExists(this, event.getIdDomain(), event.getId());
                    }
                }
                break;
            default:
                break;
        }
    }*/

    View.OnClickListener onAddEventHandler = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(getBaseContext(), CreateEventActivity.class);
            intent.putExtra("user", user);
            intent.putExtra("domain", domain);
            startActivity(intent);
        }
    };

    private void setupViewPager(ViewPager viewPager) {
        mSectionsPagerAdapter.addFragment(new OpenEventsFragment(), "Open Events");
        mSectionsPagerAdapter.addFragment(new ClosedEventsFragment(), "Running Events");
        mSectionsPagerAdapter.addFragment(new FinishedEventFragment(), "Finished Events");
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    @Override
    public void onOpenFragmentInteraction(Uri uri) {
        //do stuff
    }

    @Override
    public void onClosedFragmentInteraction(Uri uri) {
        //do stuff
    }

    @Override
    public void onFinishedFragmentInteraction(Uri uri) {
        //do stuff
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tabbed_events, menu);
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
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_tabbed_events, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //return PlaceholderFragment.newInstance(position + 1);

            /*switch (position) {
                case 0:
                    // Top Rated fragment activity
                    return new OpenEventsFragment();
                case 1:
                    // Games fragment activity
                    return new ClosedEventsFragment();
            }*/

            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            /*switch (position) {
                case 0:
                    return "Open";
                case 1:
                    return "Closed";
                case 2:
                    return "SECTION 3";
            }*/
            return mFragmentTitleList.get(position);
        }
    }
}
