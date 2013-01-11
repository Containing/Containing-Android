package com.containing.graph;

import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.containing.graph.ContainersIncomingOutgoingGraph.LINE;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

public class AvailableVehiclesGraph implements IGraph {

	private int maxItems = 10;
	
	private GraphicalView view;
	private HashMap<String, TimeSeries> datasets = new HashMap<String, TimeSeries>();
	private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();	
	private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
	Random rnd = new Random(2);
	
	/**
	 * Constructor
	 */
	public AvailableVehiclesGraph() {
		mRenderer.setXTitle("Time");
		mRenderer.setYTitle("Available");
		mRenderer.setZoomEnabled(false);
	}
	
	/**
	 * Return chart view
	 * @param context
	 * @return GraphicalView
	 */
	@Override
	public GraphicalView getView(Context context) {
		view = ChartFactory.getTimeChartView(context, mDataset, mRenderer, "H:mm:ss");
		return view;
	}

	/**
	 * Add a new point to the graph
	 * @param key
	 * @param date
	 * @param y
	 */
	private void addNewPoint(String key, Date date, int y) {
		TimeSeries dataset = (TimeSeries)datasets.get(key);
		
		if(dataset == null) {
			Log.w("AvailableVehiclesGraph", "Vehicle category " + key + " doesn't exist yet");
			dataset = new TimeSeries(key);
			datasets.put(key, dataset);
			XYSeriesRenderer renderer = new XYSeriesRenderer();
			renderer.setColor(Color.rgb(rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255)));
			renderer.setPointStyle(PointStyle.CIRCLE);
			renderer.setFillPoints(true);
			mDataset.addSeries(dataset);
			mRenderer.addSeriesRenderer(renderer);
		}
		
		if(dataset != null) {
			dataset.add(date, y);
			Log.d("VEHICLES", "ADD");
			if(dataset.getItemCount() > maxItems)
				dataset.remove(0);
		}
	}
	
	/**
	 * Add a collection of points to the graph
	 * @param keys
	 * @param values
	 * @param date
	 */
	public void addNewPoints(String[] keys, Integer[] values, Date date) {
		for(int i = 0; i < keys.length; i++) {
			addNewPoint(keys[i], date, values[i]);
		}
	}
}
