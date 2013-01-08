package com.containing.graph;

import java.util.Date;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Color;

/**
 * Linegraph representing incoming and outgoing containers
 * 
 * @author Christiaan
 *
 */
public class ContainersIncomingOutgoingGraph implements IGraph {

	public enum LINE { INCOMING, OUTGOING };
	private int maxItems = 10;
	
	private GraphicalView view;
	private TimeSeries incoming = new TimeSeries("Incoming");
	private TimeSeries outgoing = new TimeSeries("Outgoing");
	private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	
	private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
	private XYSeriesRenderer rendererIncoming = new XYSeriesRenderer();
	private XYSeriesRenderer rendererOutgoing = new XYSeriesRenderer();
	
	/**
	 * Constructor
	 */
	public ContainersIncomingOutgoingGraph() {
		mDataset.addSeries(incoming);
		mDataset.addSeries(outgoing);
		
		rendererIncoming.setColor(Color.RED);
		rendererIncoming.setPointStyle(PointStyle.CIRCLE);
		rendererIncoming.setFillPoints(true);

		rendererOutgoing.setColor(Color.GREEN);
		rendererOutgoing.setPointStyle(PointStyle.CIRCLE);
		rendererOutgoing.setFillPoints(true);
		
		mRenderer.setXTitle("Time");
		mRenderer.setYTitle("Count");
		mRenderer.addSeriesRenderer(rendererIncoming);
		mRenderer.addSeriesRenderer(rendererOutgoing);
		mRenderer.setZoomEnabled(false);
		mRenderer.setZoomButtonsVisible(true);
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
	 * @param date
	 * @param y
	 * @param line
	 */
	public void addNewPoint(Date date, int y, LINE line) {
		TimeSeries dataset = null;
		switch(line) {
			case INCOMING:
				dataset = incoming;
				break;
			case OUTGOING:
				dataset = outgoing;
				break;
		}
		
		if(dataset != null) {
			dataset.add(date, y);
			if(dataset.getItemCount() > maxItems)
				dataset.remove(0);
		}
	}
}
