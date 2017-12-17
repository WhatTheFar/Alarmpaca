package com.alpaca.alarmpaca.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alpaca.alarmpaca.R;
import com.alpaca.alarmpaca.fragment.AlarmFragment;
import com.alpaca.alarmpaca.fragment.FragmentTemplateFull;
import com.alpaca.alarmpaca.fragment.TaskFragment;

public class MainActivity extends AppCompatActivity {

    ViewPager mViewPager;
    ActionMode actionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_main);

        initInstance();

    }

    private void initInstance() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(onPageChangeListener);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(floatingActionBtnOnClickListener);
    }

    @Override
    public void onSupportActionModeStarted(@NonNull ActionMode mode) {
        Log.wtf("MainActivity", "ActionMode : onSupportActionModeStarted");
        this.actionMode = mode;
        super.onSupportActionModeStarted(mode);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_delete:
                Log.wtf("MainActivity", "Menu : Delete clicked");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    final View.OnClickListener floatingActionBtnOnClickListener = view -> {
        switch (mViewPager.getCurrentItem()) {
            case 0:
                Intent taskIntent = new Intent(MainActivity.this, TaskDetailActivity.class);
                startActivity(taskIntent);
                break;
            case 1:
                Intent alarmIntent = new Intent(MainActivity.this, AlarmDetailActivity.class);
                startActivity(alarmIntent);
                break;
        }
    };

    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//            Log.wtf("MainActivity", "ViewPager : onPageScrolled");
            if (actionMode != null) {
                Log.wtf("MainActivity", "ActionMode : finish");
                actionMode.finish();
                actionMode = null;
            }
        }

        @Override
        public void onPageSelected(int position) {
            invalidateOptionsMenu();
        }

        @Override
        public void onPageScrollStateChanged(int state) {
//            Log.wtf("MainActivity", "ViewPager : onPageScrollStateChanged " + state);
        }
    };

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return TaskFragment.newInstance();
                case 1:
                    return AlarmFragment.newInstance();
            }
            return FragmentTemplateFull.newInstance();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Tasks";
                case 1:
                    return "Alarm";
                default:
                    return "Default";
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }
    }
}
