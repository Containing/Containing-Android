package com.containing.graph;

import android.content.Context;
import org.achartengine.GraphicalView;

/**
 * Graph interface
 * @author Christiaan
 *
 */
public interface IGraph {
	
	/**
	 * Returns generated graph as a View
	 * @param context
	 * @return GraphicalView
	 */
	public GraphicalView getView(Context context);
}
