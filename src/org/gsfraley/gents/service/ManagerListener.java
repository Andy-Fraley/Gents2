package org.gsfraley.gents.service;

public interface ManagerListener {
	public void serverAdded(ServerGhost server);
	public void serverRemoved(ServerGhost server);
	public void serverStarted(ServerGhost server);
	public void serverStopped(ServerGhost server);
}
