package org.gsfraley.gents.service.servers.echoserver;

import java.io.IOException;
import java.util.Map;

import org.gsfraley.gents.service.ServerGhost;

import fi.iki.elonen.NanoHTTPD;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

public class EchoServer extends ServerGhost implements OnSharedPreferenceChangeListener {

	public final static String NAME = "EchoServer";
	public final static String DESCRIPTION = "A server that yells back!";
	
	public final static String SERVER_PORT = "server_port";
	public final static int DEFAULT_SERVER_PORT = 8080;
	
	public final static String DEFAULT_DOC_TITLE = "Say something!!";
	
	private EchoServerContainer esContainer;
	private SharedPreferences mPreferences;
	
	public boolean isConfigurable() { return true; }
	
	public String getName() {
		return NAME + ":" + Integer.toString(getServerId());
	}
	
	public String getDescription() {
		return DESCRIPTION;
	}
	
	public EchoServer(Context context, int sid) {
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
		esContainer = new EchoServerContainer();
		esContainer.run();
	}
	
	public void stopServer() {
		esContainer.stopServer();
		esContainer = null;
	}
	
	@Override
	public Intent configIntent() {
		Intent sendIntent = new Intent(getContext(), EchoServerPreferenceActivity.class);
		sendIntent.putExtra(EchoServerPreferenceActivity.SERVER_PREF_NAME, getName());
		return sendIntent;
	}
	
	public static class EchoServerInstance extends NanoHTTPD {
		String title;
		
		public EchoServerInstance(int port, String titleString) {
			super(port);
			title = titleString;
		}
		
		@Override
	    public Response serve(String uri, Method method, Map<String, String> header, Map<String, String> parms, Map<String, String> files) {
			StringBuilder parmBuilder = new StringBuilder();
			
			for(String key : parms.keySet())
				if(key.length() < 22 || !key.substring(0,22).equals("NanoHttpd.QUERY_STRING"))
					parmBuilder.append(key + " = " + parms.get(key) + "\n<br>\n");
			
			String res =
					"<html>\n" +
					"<head><title>" + title + "</title></head>\n" +
					"<body>\n" +
					uri.substring(1) + "\n<br>\n" +
					"<blockquote style='font-family:monospace'>\n" +
					parmBuilder.toString() + "\n" +
					"</blockquote>\n" +
					"</body>\n" +
					"</html>";
	        return new Response(res);
	    }
	}
	
	public class EchoServerContainer extends Thread {
		EchoServerInstance esInstance;
		
		public EchoServerContainer() {
			super("EchoServerContainer");
			
			esInstance = new EchoServerInstance(
					Integer.parseInt(mPreferences.getString(SERVER_PORT, Integer.toString(DEFAULT_SERVER_PORT))),
					DEFAULT_DOC_TITLE);
		}
		
		@Override
		public void run() {
			startServer();
		}
		
		public void startServer() {
			try {
				esInstance.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void stopServer() {
			esInstance.stop();
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {
		restart();
	}
}
