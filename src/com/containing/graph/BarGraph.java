package com.containing.graph;
import android.content.Context;
import android.graphics.Color;
import android.view.View;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

public class BarGraph {

	public View getView(Context context) {
		String[] x = {"Foo","Bar","Baz"};
		int[] y = {10,20,30};
		int[] colors = new int[] {Color.BLUE, Color.RED};
		
		CategorySeries series = new CategorySeries("Foobar");
		CategorySeries series2 = new CategorySeries("Foobar2");
		for(int i = 0; i < y.length; i++) {
			series.add("test" + i,y[i]);
			series2.add("test "+ i,y[i] / 2);
		}
		
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		dataset.addSeries(series.toXYSeries());
		dataset.addSeries(series2.toXYSeries());
		
		XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
		XYSeriesRenderer renderer = new XYSeriesRenderer();
		XYSeriesRenderer renderer2 = new XYSeriesRenderer();
		mRenderer.addSeriesRenderer(renderer);
		mRenderer.addSeriesRenderer(renderer2);
		mRenderer.setYTitle("Vehicles");
		
		renderer.setDisplayChartValues(true);
		renderer.setChartValuesSpacing(0.5f);
		renderer.setColor(Color.RED);
		
		renderer2.setDisplayChartValues(true);
		renderer2.setChartValuesSpacing(0.5f);		
		renderer2.setColor(Color.GREEN);
	
		View view = ChartFactory.getBarChartView(context, dataset, mRenderer, Type.DEFAULT);
		return view;
	}
}
