package org.gsfraley.gents.service;

import java.util.HashSet;

import org.gsfraley.gents.app.main.MainActivity;
import org.gsfraley.gents.app.main.MainActivityListener;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;

public class ServerBoss extends Service {
	
	public final static String
		SERVER_BOSS_PREFERENCES = "server_boss_preferences",
		SERVER_SID = "server_side",
		SERVER_LIST = "server_list",
		
		SERVER_COMMAND = "server_command",
			STOP_SERVER = "stop_server",
			START_SERVER = "start_server",
			CREATE_SERVER = "create_server",
			DESTROY_SERVER = "destroy_server";

	private SharedPreferences mPreferences;

	/* Service management */
	private MainActivityListener mListener;
	
	private static boolean running = false;
	
	public static boolean isRunning() {
		return running;
	}
	
	private final IBinder mBinder = new BossBinder();
	
	private Notification buildNotification() {
		Context context = getBaseContext();
		Notification.Builder builder = new Notification.Builder(context)
			.setDefaults(Notification.FLAG_FOREGROUND_SERVICE)
			.setSmallIcon(org.gsfraley.gents.R.drawable.ic_stat_server_boss)
			.setContentTitle("Gents is running...")
			.setAutoCancel(false)
			.setContentIntent(
					PendingIntent.getActivity(context, 0, new Intent(this, MainActivity.class),
							PendingIntent.FLAG_UPDATE_CURRENT));
		
		return builder.build();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return Service.START_NOT_STICKY;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	@Override
	public void onCreate() {
		running = true;
		
		// Promote the service to the foreground
		Notification bossNotification = buildNotification();
		startForeground(1, bossNotification);
		
		// Load shared prefs
		mPreferences = getSharedPreferences(SERVER_BOSS_PREFERENCES, MODE_PRIVATE);
		
		// Reload any stored servers
		createServerManager();
		loadServers();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		running = false;
		
		// Drop the servers into storage
		storeServers();
		
		// Store the sid
		mPreferences.edit().putInt(SERVER_SID, serverManager.sidGenerator).apply();
	}
	
	public void setMainListener(MainActivityListener listener) {
		mListener = listener;
		mListener.setBossReady();
	}
	
	public class BossBinder extends Binder {
		public ServerBoss getService() {
			return ServerBoss.this;
		}
	}
	
	
	/* Server management */
	private ServerManager serverManager;
	
	public ServerManager getServerManager() {
		return serverManager;
	}
	
	public void createServerManager() {
		serverManager = new ServerManager(getBaseContext());
		
		// Set the sid on the manager
		serverManager.sidGenerator = mPreferences.getInt(SERVER_SID, 0);
	}
	
	private void loadServers() {
		
		for(String serverString : mPreferences.getStringSet(SERVER_LIST, new HashSet<String>())) {
			System.out.println(serverString);
			String classString = serverString.split(":")[0];
			int serverId = Integer.parseInt(serverString.split(":")[1]);
			int status = Integer.parseInt(serverString.split(":")[2]);
			
			ServerGhost server = serverManager.createServer(classString, serverId);
			
			if(status == ServerGhost.RUNNING) {
				server.start();
			}
		}
	}
	
	private void storeServers() {
		HashSet<String> serverStrings = new HashSet<String>();
		SharedPreferences.Editor ed = getSharedPreferences(SERVER_BOSS_PREFERENCES, MODE_PRIVATE).edit();
		
		for(ServerGhost server : serverManager) {
			int status = server.getStatus();
			server.stop();
			serverStrings.add(server.getClass().getName() + ":" + Integer.toString(server.getServerId()) + ":" + Integer.toString(status));
		}
		
		ed.putStringSet(SERVER_LIST, serverStrings);
		ed.apply();
	}
}
