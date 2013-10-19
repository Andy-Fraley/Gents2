package org.gsfraley.gents.service.servers.echoserver;

import org.gsfraley.gents.R;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.view.Menu;

public class EchoServerPreferenceActivity extends Activity {
	public final static String SERVER_PREF_NAME = "server_pref_name";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		String serverPrefName = getIntent().getExtras().getString(SERVER_PREF_NAME, EchoServer.NAME);
		
		PreferenceFragment prefFragment = new HelloServerPreferenceFragment();
		Bundle args = new Bundle();
		args.putString(SERVER_PREF_NAME, serverPrefName);
		prefFragment.setArguments(args);
		
		getFragmentManager().beginTransaction()
			.replace(android.R.id.content, prefFragment)
			.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.echo_server_preference, menu);
		return true;
	}

	
	public static class HelloServerPreferenceFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			Bundle args = getArguments();
			
			PreferenceManager pm = getPreferenceManager();
			pm.setSharedPreferencesName(args.getString(SERVER_PREF_NAME));
			pm.setSharedPreferencesMode(MODE_PRIVATE);
			
			addPreferencesFromResource(R.xml.servers_echoserver_preference);
		}
	}
}
