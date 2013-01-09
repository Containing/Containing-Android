package com.containing.android;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import Network.StatsMessage;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.util.Log;
import org.jeromq.*;
import org.jeromq.ZMQ.Msg;
import org.achartengine.*;

import com.containing.graph.BarGraph;
import com.containing.graph.ContainersIncomingOutgoingGraph;
import com.containing.graph.LineGraph;
import com.containing.graph.PieGraph;
import com.containing.graph.StorageAreaGraph;
import org.jeromq.*;

import zmq.ZError;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener {

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current tab position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	
	volatile private ContainersIncomingOutgoingGraph graphContainersInOut = new ContainersIncomingOutgoingGraph();
	volatile private StorageAreaGraph graphStorageArea = new StorageAreaGraph();
	volatile private GraphicalView graphContainersInOutView;
	volatile private GraphicalView graphStorageAreaView;
	
	private ZMQ.Context zmqContext = ZMQ.context(1);
	volatile private ZMQ.Socket subscriber;
	private Thread statsThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.i("ZMQ", ZMQ.getVersionString());
		
		// Set up the action bar to show tabs.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// For each of the sections in the app, add a tab to the action bar.
		actionBar.addTab(actionBar.newTab().setText(R.string.title_section1)
				.setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText(R.string.title_section2)
				.setTabListener(this));
		
		graphContainersInOutView = graphContainersInOut.getView(this);
		graphStorageAreaView = graphStorageArea.getView(this);
		
		LinearLayout layout = (LinearLayout) findViewById(R.id.chartContainersInOut);
		layout.addView(graphContainersInOutView);

		layout = (LinearLayout) findViewById(R.id.chartContainersStorageArea);
		layout.addView(graphStorageAreaView);
		// layout = (LinearLayout) findViewById(R.id.chartVehiclesAvailability);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current tab position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current tab position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
				.getSelectedNavigationIndex());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	
		LinearLayout chartsLayout = (LinearLayout) findViewById(R.id.chartsLayout);
		LinearLayout connectionLayout = (LinearLayout) findViewById(R.id.connectionLayout);
		
		switch(tab.getPosition()) {
			case 0:
				chartsLayout.setVisibility(LinearLayout.VISIBLE);
				connectionLayout.setVisibility(LinearLayout.GONE);
				break;
			case 1:
				chartsLayout.setVisibility(LinearLayout.GONE);
				connectionLayout.setVisibility(LinearLayout.VISIBLE);
				break;
		}			
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * Connect/Disconnect with controller when toggling the connect/disconnect button
	 * and setups statsThread
	 * @param view
	 */
	public void toggleConnection(View view) {
		boolean on = ((ToggleButton) view).isChecked();
		Log.d("CONNECTION", on ? "connect" : "disconnect");

		if(statsThread != null)
			statsThread.interrupt();
		
		if(on) {
			EditText con_hostname = (EditText) findViewById(R.id.con_hostname);
			String connection_string = con_hostname.getText().toString();
			Log.d("CONNECTION", connection_string);
			if(connection_string.length() == 0) {
				((ToggleButton) view).setChecked(!on);
				Toast.makeText(getApplicationContext(), "Connection string should not be empty!", Toast.LENGTH_SHORT).show();
				return;
			}
			
			try {				
				setupConnection(connection_string);

				statsThread = new Thread() {
					public void run() {
						try {
							while(!Thread.interrupted()) {
								Thread.sleep(100);

								StatsMessage msg = null;
								byte[] data = subscriber.recv();
								try {
									ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
									msg = (StatsMessage) ois.readObject();
									ois.close();
								}
								catch(Exception e) {
									Log.d("ZMQ", "error: " + e.getMessage());
								}
								
								if(msg != null) {
									Log.d("ZMQ", msg.toString());
									// First graph
									Date d = new Date();
									graphContainersInOut.addNewPoint(d, (int)msg.containers_outgoing, ContainersIncomingOutgoingGraph.LINE.INCOMING);
									graphContainersInOut.addNewPoint(d, (int)msg.containers_incoming, ContainersIncomingOutgoingGraph.LINE.OUTGOING);

									// Second graph
									Set set = msg.areas.entrySet();
									Iterator it = set.iterator();
									String[] areas = new String[set.size()];
									Integer[] areasv = new Integer[set.size()];
									int index = 0;
									while(it.hasNext()) {
										Map.Entry me = (Map.Entry)it.next();
										areas[index] = (String)me.getKey();
										areasv[index] = (Integer)me.getValue();
										index++;
									}

									try {
										graphStorageArea.addAreas(areas, areasv);
									}
									catch(Exception e) {
										e.printStackTrace();
									}
									
									// Redraw graphs
									graphContainersInOutView.repaint();
									graphStorageAreaView.repaint();
								}
							}
						}
						catch(InterruptedException e) {
							Thread.currentThread().interrupt();
						}
					}
				};
				statsThread.start();
				
				Toast.makeText(getApplicationContext(), "Connection succeeded", Toast.LENGTH_SHORT).show();
			}
			catch(Exception e) {
				Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}
		else if(subscriber != null) {
			subscriber.close();
			subscriber = null;
			Toast.makeText(getApplicationContext(), "Connection closed", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * Starts ZMQ connection
	 * @param connection_string
	 * @throws Exception
	 */
	private void setupConnection(String connection_string) throws Exception {
		subscriber = zmqContext.socket(ZMQ.SUB);
		boolean success = subscriber.connect(connection_string);
		if(!success)
			throw new Exception("Failed to connect to " + connection_string);
		subscriber.subscribe("");
	}
	
	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			// Create a new TextView and set its text to the fragment's section
			// number argument value.
			TextView textView = new TextView(getActivity());
			textView.setGravity(Gravity.CENTER);
			textView.setText(Integer.toString(getArguments().getInt(
					ARG_SECTION_NUMBER)));
			return textView;
		}
	}
}
