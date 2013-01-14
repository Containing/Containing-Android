package com.containing.graph;

import java.util.List;
import java.util.Random;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.content.Context;
import android.graphics.Color;

/**
 * Pie graph that displays how the containers are divided across areas
 * 
 * TODO: Keep track of areas and their series index as well as renderer index
 * so we can simply adjust the values of the existing series instead of
 * regenerating it and creating new renderers. 
 * 
 * @author Christiaan
 *
 */
public class StorageAreaGraph implements IGraph {

	private GraphicalView view;
	private DefaultRenderer renderer = new DefaultRenderer();
	private CategorySeries series = new CategorySeries("Storage");
	
	/**
	 * Constructor
	 */
	public StorageAreaGraph() {
		renderer.setChartTitle("Container division across areas");
		renderer.setShowLegend(false);
	}
	
	/**
	 * Return chart view
	 * @param context
	 * @return GraphicalView
	 */
	@Override
	public GraphicalView getView(Context context) {
		view = ChartFactory.getPieChartView(context, series, renderer);
		return view;
	}

	/**
	 * Add's an area to the series and generates a SimpleSeriesRenderer for it
	 * @param name
	 * @param value
	 * @param rnd
	 */
	private void addArea(String name, int value, Random rnd) {
		series.add(name, value);
		SimpleSeriesRenderer r = new SimpleSeriesRenderer();
		r.setColor(Color.rgb(rnd.nextInt(254), rnd.nextInt(254), rnd.nextInt(254)));
		renderer.addSeriesRenderer(r);
	}
	
	/**
	 * Add new areas
	 * @param name
	 * @param value
	 * @throws Exception when name and value arrays don't match in length
	 */
	public void addAreas(String[] name, Integer[] value) throws Exception {
		if(name.length != value.length)
			throw new Exception("name[] and value[] don't match in length");
		
		series.clear();
		if(renderer.getSeriesRendererCount() > 0)
			for(SimpleSeriesRenderer r : renderer.getSeriesRenderers())
				renderer.removeSeriesRenderer(r);

		Random rnd = new Random(2);
		for(int i = 0; i < name.length; i++) {
			addArea(name[i], value[i], rnd);
		}
	}
}
