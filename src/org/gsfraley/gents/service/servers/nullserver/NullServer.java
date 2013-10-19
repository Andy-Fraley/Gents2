package org.gsfraley.gents.service.servers.nullserver;

import org.gsfraley.gents.service.ServerGhost;

import android.content.Context;
import android.content.Intent;

public class NullServer extends ServerGhost {
	public final static String NAME = "NullServer";
	public final static String DESCRIPTION = "A server that does nothing!";
	
	// Not configurable!
	public boolean isConfigurable() { return false; }
	
	public String getName() {
		return NAME + ":" + Integer.toString(getServerId());
	}
	
	public String getDescription() {
		return DESCRIPTION;
	}
	
	public NullServer(Context context, int sid) {
		super(context, sid);
		System.out.println(getName() + " created!");
	}
	
	@Override
	public void start() {
		if(status == RUNNING) {
			System.out.println(getName() + " already started!");
			return;
		}
		
		status = RUNNING;
		System.out.println(getName() + " started!");
	}

	@Override
	public void stop() {
		if(status == STOPPED) {
			System.out.println(getName() + " already stopped!");
			return;
		}
		
		status = STOPPED;
		System.out.println(getName() + " stopped!");
	}

	@Override
	public Intent configIntent() {
		return null;
	}
}
