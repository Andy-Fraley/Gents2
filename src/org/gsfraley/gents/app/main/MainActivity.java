package org.gsfraley.gents.app.main;

import java.util.Locale;

import org.gsfraley.gents.R;
import org.gsfraley.gents.app.fragments.RunningServerFragment;
import org.gsfraley.gents.app.fragments.ServerTypeFragment;
import org.gsfraley.gents.service.ServerBoss;
import org.gsfraley.gents.service.ServerBoss.BossBinder;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener {
	
	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	MainActivityListener mListener;
	ServerBoss mService;
	boolean mBound;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Build the listener
		mListener = new MainActivityListener(this);
		
		// Build the Boss Service if not already started
		if(!ServerBoss.isRunning()) {
			// Set up the boss service
			Intent startIntent = new Intent(this, ServerBoss.class);
			startService(startIntent);
		}

		// ServerBoss should already be foregrounded, let it do its thing
		Intent bindIntent = new Intent(this, ServerBoss.class);
		bindService(bindIntent, mConnection, Context.BIND_ALLOW_OOM_MANAGEMENT);
		
		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
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
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(mConnection);
		// TODO: Create a toggle so that the ServerBoss can run outside of the activity
		Intent stopIntent = new Intent(this, ServerBoss.class);
		stopService(stopIntent);
	}
	
	public MainActivityListener getListener() {
		return mListener;
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
			// getItem is called to instantiate the fragment for the given page.
			switch(position) {
			case 0:
				RunningServerFragment runningServerFragment = new RunningServerFragment();
				mListener.rsFragment = runningServerFragment;
				return runningServerFragment;
			case 1:
				ServerTypeFragment serverTypeFragment = new ServerTypeFragment();
				mListener.stFragment = serverTypeFragment;
				return serverTypeFragment;
			}
			return null;
		}

		@Override
		public int getCount() {
			// Show 2 total pages.
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_running_server_section).toUpperCase(l);
			case 1:
				return getString(R.string.title_server_type_section).toUpperCase(l);
			}
			return null;
		}
	}
	
	private ServiceConnection mConnection  = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			BossBinder binder;
			try {
				binder = (BossBinder) service;
			} catch (ClassCastException e) {
				throw new ClassCastException(service.toString()
						+ " must be a BossBinder");
			}
			try {
				mService = (ServerBoss) binder.getService();
				mBound = true;
			} catch (ClassCastException e) {
				mBound = false;
				throw new ClassCastException(mService.toString()
						+ " must be a ServerBoss");
			}
			
			mListener.serverBoss = mService;
			mService.setMainListener(mListener);
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mBound = false;
		}
	};
}
