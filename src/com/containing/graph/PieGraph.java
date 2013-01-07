package com.containing.graph;
import android.content.Context;
import android.graphics.Color;
import android.view.View;

import org.achartengine.ChartFactory;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

public class PieGraph {

	public View getView(Context context) {
		String[] x = {"Foo","Bar","Baz"};
		int[] y = {10,20,30};
		int[] colors = new int[] {Color.BLUE, Color.RED, Color.GREEN};
		
		CategorySeries series = new CategorySeries("Foobar");
		for(int i = 0; i < x.length; i++) {
			series.add(x[i],y[i]);
		}
		
		DefaultRenderer renderer = new DefaultRenderer();
		for(int color : colors) {
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(color);
			renderer.addSeriesRenderer(r);
		}
	
		View view = ChartFactory.getPieChartView(context, series, renderer);
		
		return view;
	}
}
