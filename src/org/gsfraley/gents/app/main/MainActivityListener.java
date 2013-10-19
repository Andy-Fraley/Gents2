package org.gsfraley.gents.app.main;

import java.util.ArrayList;

import org.gsfraley.gents.app.fragments.RunningServerFragment;
import org.gsfraley.gents.app.fragments.ServerTypeFragment;
import org.gsfraley.gents.service.ServerBoss;
import org.gsfraley.gents.service.ServerManager;

public class MainActivityListener {
	MainActivity mainActivity;
	ServerBoss serverBoss;
	RunningServerFragment rsFragment;
	ServerTypeFragment stFragment;
	
	public MainActivityListener(MainActivity activity) {
		mainActivity = activity;
		bossListeners = new ArrayList<BossListener>();
	}
	
	public ServerManager getServerManager() {
		return serverBoss.getServerManager();
	}
	
	
	/* Sync work for the service */
	
	private ArrayList<BossListener> bossListeners;
	
	public void setBossReady() {
		for(BossListener listener : bossListeners) {
			listener.bossReady();
		}
	}
	
	public void addBossListener(BossListener listener) {
		bossListeners.add(listener);
	}
	
	public void removeBossListener(BossListener listener) {
		bossListeners.remove(listener);
	}
	
	public interface BossListener {
		public void bossReady();
	}
	
	
	/* UI control */
	
	public void setTab(int tab) {
		mainActivity.mViewPager.setCurrentItem(tab);
	}
}
