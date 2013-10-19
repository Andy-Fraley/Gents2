package org.gsfraley.gents.app.adapters;

import org.gsfraley.gents.R;
import org.gsfraley.gents.app.main.MainActivity;
import org.gsfraley.gents.app.main.MainActivityListener;
import org.gsfraley.gents.app.main.MainActivityListener.BossListener;
import org.gsfraley.gents.service.ManagerListener;
import org.gsfraley.gents.service.ServerGhost;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class RunningServerAdapter extends ArrayAdapter<ServerGhost> implements ManagerListener, BossListener {
	private Context mContext;
	private MainActivityListener mListener;
	
	public RunningServerAdapter(Context context) {
		super(context, R.id.serverTitleText);
		
		mContext = context;
		mListener = ((MainActivity) context).getListener();
		
		mListener.addBossListener(this);
	}
	
	public void connectServers() {
		for(ServerGhost server : mListener.getServerManager()) {
			super.add(server);
		}
		
		mListener.getServerManager().addListener(this);
	}

	@Override
	public void bossReady() {
		connectServers();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = ((MainActivity) mContext).getLayoutInflater();
		View row = inflater.inflate(R.layout.fragment_runningserver_item, parent, false);
		
		ServerGhost server = super.getItem(position);
		boolean isRunning = server.getStatus() == ServerGhost.RUNNING;

		@SuppressWarnings("unchecked")
		Class<ServerGhost> serverClass = (Class<ServerGhost>) server.getClass();
		
		CharSequence name = null;
		try {
			name = (CharSequence) serverClass.getField("NAME").get(null);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		
		TextView serverTitle = (TextView) row.findViewById(R.id.serverTitleText);
		serverTitle.setText(name);
		
		
		ImageButton serverSwitch = (ImageButton) row.findViewById(R.id.serverStatusButton);
		
		Drawable buttonImage = mContext.getResources().getDrawable(isRunning ?
				R.drawable.ic_action_stop_server
				: R.drawable.ic_action_start_server);
		serverSwitch.setImageDrawable(buttonImage);
		
		serverSwitch.setOnClickListener(isRunning ?
				new StopListener(server)
				: new StartListener(server));
		
		ImageButton serverDestroy = (ImageButton) row.findViewById(R.id.serverDestroyButton);
		serverDestroy.setOnClickListener(new DestroyListener(server));
		
		ImageButton serverConfig = (ImageButton) row.findViewById(R.id.serverConfigButton);
		if(server.isConfigurable())
			serverConfig.setOnClickListener(new ConfigListener(server));
		else
			serverConfig.setEnabled(false);
		
		return row;
	}

	
	/* ManagerListener */
	
	@Override
	public void serverAdded(ServerGhost server) {
		super.add(server);
	}

	@Override
	public void serverRemoved(ServerGhost server) {
		super.remove(server);
	}

	@Override
	public void serverStarted(ServerGhost server) {
		notifyDataSetChanged();
	}

	@Override
	public void serverStopped(ServerGhost server) {
		notifyDataSetChanged();
	}
	
	
	/* Button Listeners */
	
	public class DestroyListener implements OnClickListener {
		private ServerGhost mServer;
		
		public DestroyListener(ServerGhost server) {
			mServer = server;
		}
		
		@Override
		public void onClick(View v) {
			// This adapter is already a declared listener, no need to remove it twice
			mServer.stop();
			RunningServerAdapter.this.mListener.getServerManager().removeServer(mServer);
		}
	}
	
	public class ConfigListener implements OnClickListener {
		private ServerGhost mServer;
		
		public ConfigListener(ServerGhost server) {
			mServer = server;
		}
		
		@Override
		public void onClick(View v) {
			getContext().startActivity(mServer.configIntent());
		}
	}
	
	public class StopListener implements OnClickListener {
		private ServerGhost mServer;
		
		public StopListener(ServerGhost server) {
			mServer = server;
		}
		
		@Override
		public void onClick(View v) {
			RunningServerAdapter.this.mListener.getServerManager().stopServer(mServer);
		}
	}
	
	public class StartListener implements OnClickListener {
		private ServerGhost mServer;
		
		public StartListener(ServerGhost server) {
			mServer = server;
		}
		
		@Override
		public void onClick(View v) {
			RunningServerAdapter.this.mListener.getServerManager().startServer(mServer);
		}
	}
}
