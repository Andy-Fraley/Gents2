package org.gsfraley.gents.app.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.gsfraley.gents.R;
import org.gsfraley.gents.app.adapters.RunningServerAdapter;
import org.gsfraley.gents.service.ServerGhost;

/**
 * A fragment representing a list of Items.
 * <p />
 * Large screen devices (such as tablets) are supported by replacing the
 * ListView with a GridView.
 * <p />
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class RunningServerFragment extends Fragment implements
		AbsListView.OnItemClickListener {

	private AbsListView mListView;
	private RunningServerAdapter mAdapter;
	
	public RunningServerFragment() {
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mAdapter = new RunningServerAdapter(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_runningserver,
				container, false);

		// Set the adapter
		mListView = (AbsListView) view.findViewById(android.R.id.list);
		((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

		// Set OnItemClickListener so we can be notified on item clicks
		mListView.setOnItemClickListener(this);

		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		
		// Throw out preferences in case invalidated
		// Perhaps in future, implement per-server listeners?
		mAdapter.notifyDataSetInvalidated();
	}
	
	// Each item has buttons, no need to do anything onclick
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		ServerGhost server = mAdapter.getItem(position);
		startActivity(server.configIntent());
	}

	/**
	 * The default content for this Fragment has a TextView that is shown when
	 * the list is empty. If you would like to change the text, call this method
	 * to supply the text it should use.
	 */
	public void setEmptyText(CharSequence emptyText) {
		View emptyView = mListView.getEmptyView();

		if (emptyText instanceof TextView) {
			((TextView) emptyView).setText(emptyText);
		}
	}
}
