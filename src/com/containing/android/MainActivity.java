package com.containing.android;

import java.util.Date;
import java.util.Random;

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
	
	private ContainersIncomingOutgoingGraph graphContainersInOut = new ContainersIncomingOutgoingGraph();
	private StorageAreaGraph graphStorageArea = new StorageAreaGraph();
	private GraphicalView graphContainersInOutView;
	private GraphicalView graphStorageAreaView;
	
	private ZMQ.Context zmqContext = ZMQ.context(1);
	private ZMQ.Socket subscriber;

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

		Thread thread = new Thread() {
			public void run() {
				Random rnd = new Random();
				for(long i = 0;; i+=3600 * 60) {
					try {
						Thread.sleep(2000);
						//Thread.sleep(200);
					}
					catch(Exception e) {
						e.printStackTrace();
					}
					graphContainersInOut.addNewPoint(new Date((long)1357654232 + i), rnd.nextInt(50000), ContainersIncomingOutgoingGraph.LINE.INCOMING);
					graphContainersInOut.addNewPoint(new Date((long)1357654232 + i), rnd.nextInt(10000), ContainersIncomingOutgoingGraph.LINE.OUTGOING);
					graphContainersInOutView.repaint();

					String[] areas = {"Foo", "Bar", "Baz", "Foobar", "BarBaz"};
					int[] areasv = {rnd.nextInt(50),rnd.nextInt(50),rnd.nextInt(50),rnd.nextInt(50),rnd.nextInt(50)};
					try {
						graphStorageArea.addAreas(areas, areasv);
					}
					catch(Exception e) {
						e.printStackTrace();
					}
					graphStorageAreaView.repaint();
				}
			}
		};
		thread.start();
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
	 * Connect/Disconnect with controller
	 * @param view
	 */
	public void toggleConnection(View view) {
		boolean on = ((ToggleButton) view).isChecked();
		Log.d("CONNECTION", on ? "connect" : "disconnect");

		if(on) {
			EditText con_hostname = (EditText) findViewById(R.id.con_hostname);
			String connection_string = con_hostname.getText().toString();
			Log.d("CONNECTION", connection_string);
			if(connection_string.length() == 0) {
				((ToggleButton) view).setChecked(!on);
				Toast.makeText(getApplicationContext(), "Hostname should not be empty!", Toast.LENGTH_SHORT).show();
				return;
			}
			
			try {
				subscriber = zmqContext.socket(ZMQ.SUB);
				boolean success = subscriber.connect(connection_string);
				if(!success)
					throw new Exception("Failed to connect to " + connection_string);
				subscriber.subscribe("stats");
				Toast.makeText(getApplicationContext(), "Connection succeeded", Toast.LENGTH_SHORT).show();
			}
			catch(Exception e) {
				Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}
		else if(subscriber != null) {
			subscriber.close();
			Toast.makeText(getApplicationContext(), "Connection closed", Toast.LENGTH_SHORT).show();
		}
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
