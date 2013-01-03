package com.containing.graph;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import org.achartengine.ChartFactory;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

public class LineGraph {

	public Intent getIntent(Context context) {
		int[] x = {1,2,3,4,5,6,7,8,9,10};
		int[] y = {10,20,7,54,276,42,86,48,12,64};
		
		TimeSeries series = new TimeSeries("line1");
		for(int i = 0; i < x.length; i++) {
			series.add(x[i],y[i]);
		}
		
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		dataset.addSeries(series);
		
		XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
		XYSeriesRenderer renderer = new XYSeriesRenderer();
		mRenderer.addSeriesRenderer(renderer);
	
		Intent intent = ChartFactory.getLineChartIntent(context, dataset, mRenderer, "Line graph");
		
		return intent;
	}

	public View getView(Context context) {
		int[] x = {1,2,3,4,5,6,7,8,9,10};
		int[] y = {10,20,7,54,276,42,86,48,12,64};
		
		TimeSeries series = new TimeSeries("line1");
		for(int i = 0; i < x.length; i++) {
			series.add(x[i],y[i]);
		}
		
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		dataset.addSeries(series);
		
		XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
		XYSeriesRenderer renderer = new XYSeriesRenderer();
		mRenderer.addSeriesRenderer(renderer);
	
		View view = ChartFactory.getLineChartView(context, dataset, mRenderer);
		
		return view;
	}
}
