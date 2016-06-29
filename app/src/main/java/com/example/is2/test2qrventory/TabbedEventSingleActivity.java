package com.example.is2.test2qrventory;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
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
import android.widget.Toast;

import com.example.is2.test2qrventory.connection.EventAccess;
import com.example.is2.test2qrventory.connection.ItemAccess;
import com.example.is2.test2qrventory.connection.VolleyResponseListener;
import com.example.is2.test2qrventory.model.Event;
import com.example.is2.test2qrventory.model.Item;
import com.example.is2.test2qrventory.model.User;

import java.util.ArrayList;
import java.util.List;

public class TabbedEventSingleActivity extends AppCompatActivity implements VolleyResponseListener, EventInfoFragment.OnFragmentInteractionListener, OpenItemsFragment.OnFragmentInteractionListener, ClosedItemsFragment.OnFragmentInteractionListener {

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

    Event event = null;
    private com.github.clans.fab.FloatingActionButton fab_scan_items;
    private static final int REQUEST_ACTIVITY_SCAN_ITEMS = 1;
    /*String itemId;
    String eventId;
    int codeType;*/
    User user = null;
    EventAccess eventAccess = null;
    ItemAccess itemAccess = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed_event_single);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        //mViewPager.setAdapter(mSectionsPagerAdapter);
        setupViewPager(mViewPager);


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

        fab_scan_items = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.menu_item_scan_items);

        fab_scan_items.setOnClickListener(onScanItemsHandler);

        user = getIntent().getParcelableExtra("user");
        event = getIntent().getParcelableExtra("event");
        String userApiKey = user.getApiKey();
        eventAccess = new EventAccess(userApiKey);

        itemAccess = new ItemAccess(userApiKey);
    }

    View.OnClickListener onScanItemsHandler = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(getBaseContext(), ScanActivity.class);

            startActivityForResult(intent, REQUEST_ACTIVITY_SCAN_ITEMS);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ACTIVITY_SCAN_ITEMS:
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        String concatenatedCode = extras.getString("data");
                        int codeType = extras.getInt("type");
                        String itemId = null;
                        if (codeType == 8) {
                            itemId = concatenatedCode.substring(0, concatenatedCode.length() - 1);
                            //String scannedEventId = concatenatedCodeWithoutLastDigit.substring(0, Math.min(concatenatedCodeWithoutLastDigit.length(), 3));
                            //eventId = Integer.valueOf(scannedEventId).toString();
                            //String scannedItemId = concatenatedCodeWithoutLastDigit.substring(concatenatedCodeWithoutLastDigit.length() - 4);
                            itemId = Integer.valueOf(itemId).toString();
                        } else if (codeType == 13) {
                            itemId = concatenatedCode.substring(0, concatenatedCode.length() - 1);
                            //String scannedEventId = concatenatedCodeWithoutLastDigit.substring(0, Math.min(concatenatedCodeWithoutLastDigit.length(), 5));
                            //eventId = Integer.valueOf(scannedEventId).toString();
                            //String scannedItemId = concatenatedCodeWithoutLastDigit.substring(concatenatedCodeWithoutLastDigit.length() - 7);
                            itemId = Integer.valueOf(itemId).toString();
                        } else if (codeType == 64) {
                            itemId = concatenatedCode;
                            //String[] separatedIds = concatenatedCode.split(",");
                            //eventId = separatedIds[0];
                            //itemId = separatedIds[1];
                        }

                        eventAccess.setEventItemChecked(this, event.getId(), Long.parseLong(itemId));
                        //itemAccess.getEventItemsThatNotExists(this, event.getIdDomain(), event.getId());
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onEventInfoFragmentInteraction(Uri uri) {
        //do stuff
    }

    @Override
    public void onOpenItemsFragmentInteraction(Uri uri) {

    }

    @Override
    public void onClosedItemsFragmentInteraction(Uri uri) {

    }

    private void setupViewPager(ViewPager viewPager) {
        mSectionsPagerAdapter.addFragment(new EventInfoFragment(), "Event Info");
        mSectionsPagerAdapter.addFragment(new OpenItemsFragment(), "Open Items");
        mSectionsPagerAdapter.addFragment(new ClosedItemsFragment(), "Closed Items");
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tabbed_event_single, menu);
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
    public void onError(String message) {

    }

    @Override
    public void onResponse(Object response) {

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
            View rootView = inflater.inflate(R.layout.fragment_tabbed_event_single, container, false);
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
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }*/
            return mFragmentTitleList.get(position);
        }
    }
}
