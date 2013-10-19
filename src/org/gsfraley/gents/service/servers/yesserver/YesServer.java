package org.gsfraley.gents.service.servers.yesserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import org.gsfraley.gents.R;
import org.gsfraley.gents.service.ServerGhost;

import fi.iki.elonen.NanoHTTPD;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

public class YesServer extends ServerGhost implements OnSharedPreferenceChangeListener {

	public final static String NAME = "Yes";
	public final static String DESCRIPTION = "Something special.";
	
	public final static String SERVER_PORT = "server_port";
	public final static int DEFAULT_SERVER_PORT = 8080;
	
	public final static String DEFAULT_DOC_TITLE = "Yes.";
	
	private YesServerContainer ysContainer;
	private SharedPreferences mPreferences;
	
	private String document;
	
	public boolean isConfigurable() { return true; }
	
	public String getName() {
		return NAME + ":" + Integer.toString(getServerId());
	}
	
	public String getDescription() {
		return DESCRIPTION;
	}
	
	public YesServer(Context context, int sid) {
		super(context, sid);
		mPreferences = context.getSharedPreferences(getName(), Context.MODE_PRIVATE);
		mPreferences.registerOnSharedPreferenceChangeListener(this);
		
		try {
			document = buildDoc();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void start() {
		if(status == RUNNING) {
			return;
		}
		
		startServer();
		status = RUNNING;
	}

	@Override
	public void stop() {
		if(status == STOPPED) {
			return;
		}
		
		stopServer();
		status = STOPPED;
	}

	public void startServer() {
		ysContainer = new YesServerContainer();
		ysContainer.run();
	}
	
	public void stopServer() {
		ysContainer.stopServer();
		ysContainer = null;
	}
	
	@Override
	public Intent configIntent() {
		Intent sendIntent = new Intent(getContext(), YesServerPreferenceActivity.class);
		sendIntent.putExtra(YesServerPreferenceActivity.SERVER_PREF_NAME, getName());
		return sendIntent;
	}
	
	public static class YesServerInstance extends NanoHTTPD {
		String title, doc;
		
		public YesServerInstance(int port, String titleString, String docString) {
			super(port);
			title = titleString;
			doc = docString;
		}
		
		@Override
	    public Response serve(String uri, Method method, Map<String, String> header, Map<String, String> parms, Map<String, String> files) {
	        return new Response(doc);
	    }
	}
	
	public class YesServerContainer extends Thread {
		YesServerInstance ysInstance;
		
		public YesServerContainer() {
			super("YesServerContainer");
			
			ysInstance = new YesServerInstance(
					Integer.parseInt(mPreferences.getString(SERVER_PORT, Integer.toString(DEFAULT_SERVER_PORT))),
					DEFAULT_DOC_TITLE,
					document);
		}
		
		@Override
		public void run() {
			startServer();
		}
		
		public void startServer() {
			try {
				ysInstance.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void stopServer() {
			ysInstance.stop();
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {
		restart();
	}
	
	private String buildDoc() throws IOException {
		StringBuilder docBuilder = new StringBuilder();
		BufferedReader fileIn = new BufferedReader(
				new InputStreamReader(
						getContext().getResources().openRawResource(R.raw.servers_yesserver_index)));
		
		String line = fileIn.readLine();
		while(line != null) {
			docBuilder.append(line + "\n");
			line = fileIn.readLine();
		}
		
		fileIn.close();
		
		return docBuilder.toString();
	}
}
