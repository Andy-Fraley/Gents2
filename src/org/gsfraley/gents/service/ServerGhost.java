package org.gsfraley.gents.service;

import android.content.Context;
import android.content.Intent;

public abstract class ServerGhost {
	public final static String NAME = "ServerGhost";
	public final static String DESCRIPTION = "A server.";
	
	public final static int
		// Main status types
		STOPPED = 0,
		RUNNING = 1;
	
	public abstract void start();
	public abstract void stop();
	public abstract boolean isConfigurable();
	public abstract Intent configIntent();
	
	protected int status;
	protected final int serverId;
	
	private Context mContext;
	
	protected Context getContext() {
		return mContext;
	}
	
	public int getStatus() {
		return status;
	}
	
	public int getServerId() {
		return serverId;
	}
	
	public void restart() {
		stop();
		start();
	}
	
	public ServerGhost(Context context, int sid) {
		serverId = sid;
		mContext = context;
	}
}