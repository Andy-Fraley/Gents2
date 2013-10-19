package org.gsfraley.gents.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;

public class ServerManager implements Iterable<ServerGhost> {
	int sidGenerator;
	Context mContext;
	
	private ArrayList<ServerGhost> serverList;
	private ArrayList<ManagerListener> serverListeners;
	
	public ServerManager(Context context) {
		mContext = context;
		
		serverList = new ArrayList<ServerGhost>();
		serverListeners = new ArrayList<ManagerListener>();
	}

	public void addListener(ManagerListener listener) {
		serverListeners.add(listener);
	}
	
	public void removeListener(ManagerListener listener) {
		serverListeners.remove(listener);
	}
	
	public void addServer(ServerGhost server) {
		for(ManagerListener l : serverListeners)
			l.serverAdded(server);
		serverList.add(server);
	}
	
	public void removeServer(ServerGhost server) {
		for(ManagerListener l : serverListeners)
			l.serverRemoved(server);
		serverList.remove(server);
	}
	
	public void startServer(ServerGhost server) {
		server.start();
		for(ManagerListener l : serverListeners)
			l.serverStarted(server);
	}
	
	public void stopServer(ServerGhost server) {
		server.stop();
		for(ManagerListener l : serverListeners)
			l.serverStopped(server);
	}
	
	public ServerGhost getServer(int position) {
		return serverList.get(position);
	}

	@SuppressWarnings("unchecked")
	public ServerGhost createServer(String classString, int serverId) {
		Class<ServerGhost> serverClass = null;
		try {
			serverClass = (Class<ServerGhost>) ServerManager.class.getClassLoader().loadClass(classString);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (ClassCastException e) {
			throw new ClassCastException(classString + " must be a subclass of ServerGhost");
		}
		
		ServerGhost loadedServer = null;
		try {
			loadedServer = (ServerGhost) serverClass.getConstructor(new Class<?>[]{Context.class, int.class}).newInstance(mContext, serverId);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassCastException e) {
			throw new ClassCastException("Could not cast " + serverClass.toString() + " to a ServerGhost");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		
		addServer(loadedServer);
		return loadedServer;
	}
	
	public ServerGhost createServer(String classString) {
		ServerGhost newServer = createServer(classString, sidGenerator);
		sidGenerator++;
		return newServer;
	}
	
	@Override
	public Iterator<ServerGhost> iterator() {
		return serverList.iterator();
	}
}
