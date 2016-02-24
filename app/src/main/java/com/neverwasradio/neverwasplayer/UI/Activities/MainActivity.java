package com.neverwasradio.neverwasplayer.UI.Activities;

import java.util.Locale;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.neverwasradio.neverwasplayer.Core.PlayerService;
import com.neverwasradio.neverwasplayer.Core.StreamingDataLoader;
import com.neverwasradio.neverwasplayer.R;
import com.neverwasradio.neverwasplayer.UI.CustomView.CustomMenuTab;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {


    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

    public static PlayerFragment playerFragment;
    static InfoFragment infoFragment;
    static ProgramFragment programFragment;
    static NewsFragment newsFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);



        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setCustomView(new CustomMenuTab(this, i, mSectionsPagerAdapter.getPageTitle(i).toString()))
                            .setTabListener(this));
        }

        StreamingDataLoader dataLoader = new StreamingDataLoader();
        dataLoader.execute();

    }

    @Override
    protected void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(PlayerService.isActive() && playerFragment!=null) {
            playerFragment.setPauseButton();
        }
        else if (playerFragment!=null) { playerFragment.setPlayButton();}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
        CustomMenuTab tabView = (CustomMenuTab) tab.getCustomView();
        tabView.setTabSelected(true);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        CustomMenuTab tabView = (CustomMenuTab) tab.getCustomView();
        tabView.setTabSelected(false);
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    if(playerFragment==null) { playerFragment=PlayerFragment.newInstance();}
                    return playerFragment;
                case 1:
                    if(programFragment==null) { programFragment=ProgramFragment.newInstance();}
                    return programFragment;

                case 2:
                    if(newsFragment==null) { newsFragment=NewsFragment.newInstance();}
                    return newsFragment;
                case 3:
                    if(infoFragment==null) { infoFragment=InfoFragment.newInstance();}
                    return infoFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return "ASCOLTA";
                case 1:
                    return "PALINSESTO";
                case 2:
                    return "NEWS";
                case 3:
                    return "CONTATTI";
            }
            return null;
        }
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();

        boolean connected = ni != null && ni.isConnectedOrConnecting();

        if (!connected) {
            // There are no active networks.
            return false;
        } else
            return true;
    }


}
