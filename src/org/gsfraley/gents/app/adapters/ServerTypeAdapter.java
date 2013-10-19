package org.gsfraley.gents.app.adapters;

import org.gsfraley.gents.R;
import org.gsfraley.gents.app.main.MainActivity;
import org.gsfraley.gents.service.ServerGhost;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ServerTypeAdapter extends ArrayAdapter<Class<ServerGhost>> {
	@SuppressWarnings("unchecked")
	public final static Class<ServerGhost>[] serverTypes = (Class<ServerGhost>[]) new Class<?>[] {
		org.gsfraley.gents.service.servers.nullserver.NullServer.class,
		org.gsfraley.gents.service.servers.helloserver.HelloServer.class,
		org.gsfraley.gents.service.servers.echoserver.EchoServer.class,
		org.gsfraley.gents.service.servers.yesserver.YesServer.class
	};
	
	private Context mContext;
	
	public ServerTypeAdapter(Context context) {
		super(context, R.id.serverTypeTitleText);
		mContext = context;
		super.addAll(serverTypes);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = ((MainActivity) mContext).getLayoutInflater();
		View row = inflater.inflate(R.layout.fragment_servertype_item, parent, false);
		
		Class<ServerGhost> serverClass = super.getItem(position);
		
		CharSequence name = null, description = null;
		try {
			name = (CharSequence) serverClass.getField("NAME").get(null);
			description = (CharSequence) serverClass.getField("DESCRIPTION").get(null);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		
		TextView serverTypeTitle = (TextView) row.findViewById(R.id.serverTypeTitleText);
		serverTypeTitle.setText(name);
		
		TextView serverTypeDescription = (TextView) row.findViewById(R.id.serverTypeDescriptionText);
		serverTypeDescription.setText(description);
		
		return row;
	}
}
