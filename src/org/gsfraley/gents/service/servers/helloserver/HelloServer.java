package org.gsfraley.gents.service.servers.helloserver;

import java.io.IOException;
import java.util.Map;

import org.gsfraley.gents.service.ServerGhost;

import fi.iki.elonen.NanoHTTPD;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

public class HelloServer extends ServerGhost implements OnSharedPreferenceChangeListener {

	public final static String NAME = "HelloServer";
	public final static String DESCRIPTION = "A server that just says \"hi\"!";
	
	public final static String SERVER_PORT = "server_port";
	public final static int DEFAULT_SERVER_PORT = 8080;
	
	public final static String DOC_TITLE = "doc_title";
	public final static String DEFAULT_DOC_TITLE = "Hello!";
	
	public final static String DOC_BODY = "doc_body";
	public final static String DEFAULT_DOC_BODY = "How are you?";
	
	private HelloServerContainer hsContainer;
	private SharedPreferences mPreferences;
	
	public boolean isConfigurable() { return true; }
	
	public String getName() {
		return NAME + ":" + Integer.toString(getServerId());
	}
	
	public String getDescription() {
		return DESCRIPTION;
	}
	
	public HelloServer(Context context, int sid) {
		super(context, sid);
		mPreferences = context.getSharedPreferences(getName(), Context.MODE_PRIVATE);
		mPreferences.registerOnSharedPreferenceChangeListener(this);
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
		hsContainer = new HelloServerContainer();
		hsContainer.run();
	}
	
	public void stopServer() {
		hsContainer.stopServer();
		hsContainer = null;
	}
	
	@Override
	public Intent configIntent() {
		Intent sendIntent = new Intent(getContext(), HelloServerPreferenceActivity.class);
		sendIntent.putExtra(HelloServerPreferenceActivity.SERVER_PREF_NAME, getName());
		return sendIntent;
	}
	
	public static class HelloServerInstance extends NanoHTTPD {
		String title, body;
		
		public HelloServerInstance(int port, String titleString, String bodyString) {
			super(port);
			title = titleString;
			body = bodyString;
		}
		
		@Override
	    public Response serve(String uri, Method method, Map<String, String> header, Map<String, String> parms, Map<String, String> files) {
			String res =
					"<html>\n" +
					"<head><title>" + title + "</title></head>\n" +
					"<body>" + body + "</body>\n" +
					"</html>";
	        return new Response(res);
	    }
	}
	
	public class HelloServerContainer extends Thread {
		HelloServerInstance hsInstance;
		
		public HelloServerContainer() {
			super("HelloServerContainer");
			
			hsInstance = new HelloServerInstance(
					Integer.parseInt(mPreferences.getString(SERVER_PORT, Integer.toString(DEFAULT_SERVER_PORT))),
					mPreferences.getString(DOC_TITLE, DEFAULT_DOC_TITLE),
					mPreferences.getString(DOC_BODY, DEFAULT_DOC_BODY));
		}
		
		@Override
		public void run() {
			startServer();
		}
		
		public void startServer() {
			try {
				hsInstance.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void stopServer() {
			hsInstance.stop();
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {
		restart();
	}
}
