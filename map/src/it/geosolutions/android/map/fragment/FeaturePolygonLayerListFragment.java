/* GeoSolutions map - Digital field mapping on Android based devices
 * Copyright (C) 2013  GeoSolutions (www.geo-solutions.it)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package it.geosolutions.android.map.fragment;

import it.geosolutions.android.map.R;
import it.geosolutions.android.map.activities.GetFeatureInfoAttributeActivity;
import it.geosolutions.android.map.adapters.FeaturePolygonLayerLayerAdapter;
import it.geosolutions.android.map.database.SpatialDataSourceManager;
import it.geosolutions.android.map.loaders.FeaturePolygonLoader;
import it.geosolutions.android.map.model.FeaturePolygonQuery;
import it.geosolutions.android.map.model.FeaturePolygonQueryResult;
import it.geosolutions.android.map.model.FeaturePolygonTaskQuery;
import it.geosolutions.android.map.utils.FeatureInfoUtils;

import java.util.ArrayList;
import java.util.List;
import jsqlite.Exception;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockListFragment;
import eu.geopaparazzi.spatialite.database.spatial.core.SpatialVectorTable;

/**
 * Show a list of the layers from a feature Polygon query This fragment is
 * optimized to get only the available features doing a query on the visible
 * layers to check if at least one is present.
 * @author Jacopo Pianigiani (jacopo.pianigiani85@gmail.com)
 */
public class FeaturePolygonLayerListFragment extends SherlockListFragment
        implements LoaderCallbacks<List<FeaturePolygonQueryResult>> {
	
private FeaturePolygonLayerLayerAdapter adapter;
private static final int LOADER_INDEX =0;

FeaturePolygonTaskQuery[] queryQueue;

// The callbacks through which we will interact with the LoaderManager.

private LoaderManager.LoaderCallbacks<List<FeaturePolygonQueryResult>> mCallbacks;

/**
 * Called once on creation
 */
@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // view operations

    setRetainInstance(true);

    // get parameters to create the task query
    // TODO use arguments instead
    Bundle extras = getActivity().getIntent().getExtras();
    FeaturePolygonQuery query = (FeaturePolygonQuery) extras.getParcelable("query");
    ArrayList<String> layers = extras.getStringArrayList("layers");
    // create a unique loader index
    // TODO use a better system to get the proper loader
    // TODO check if needed,maybe the activity has only one loader
    

    // create task query to execute on spatialite db
   queryQueue = FeatureInfoUtils.createTaskQueryQueue(layers, query, null, 1);
    // Initialize loader and callbacks for the parent activity

    // setup the listView
    adapter = new FeaturePolygonLayerLayerAdapter(getSherlockActivity(),
            R.layout.feature_info_layer_list_row);
    setListAdapter(adapter); //Crasha qui!
}

@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
    startDataLoading(queryQueue, LOADER_INDEX);

    return inflater.inflate(R.layout.feature_info_layer_list, container, false);
}


/**
 * Set the loading bar and loading text
 */
private void startLoadingGUI() {
    if(getSherlockActivity()!=null){
    // start progress bars
        getSherlockActivity().setSupportProgressBarIndeterminateVisibility(true);
        getSherlockActivity().setSupportProgressBarVisibility(true);
    }
    // set suggestion text
    ((TextView) getView().findViewById(R.id.empty_text))
            .setText(R.string.feature_info_extracting_information);
}

/**
 * hide loading bar and set loading task
 */
private void stopLoadingGUI() {
    if (getSherlockActivity() != null) {
        getSherlockActivity()
                        .setSupportProgressBarIndeterminateVisibility(false);
        getSherlockActivity()
                        .setSupportProgressBarVisibility(false);
        
    }
    adapter.notifyDataSetChanged();
}

private void setNoData() {
    ((TextView) getView().findViewById(R.id.empty_text))
            .setText(R.string.feature_info_extracting_no_result);
}
/**
 * Create the data loader and bind the loader to the
 * parent callbacks
 * @param queryQueue2 array of <FeatureCircleTaskQuery> to pass to the loader
 * @param loaderIndex a unique id for query loader
 */
private void startDataLoading(FeaturePolygonTaskQuery[] queryQueue2, int loaderIndex) {
    // create task query

    // initialize Load Manager
    mCallbacks = this;
    LoaderManager lm = getSherlockActivity().getSupportLoaderManager();
    // NOTE: use the start variable as index in the loadermanager
    // if you use more than one
    adapter.clear();
    lm.initLoader(loaderIndex, null, this); // uses start to get the
}

/*
 * (non-Javadoc)
 * @see android.support.v4.app.ListFragment#onViewCreated(android.view.View,
 * android.os.Bundle)
 */
@Override
public void onViewCreated(View view, Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    super.onViewCreated(view, savedInstanceState);
    //init progress bar and loading text
    startLoadingGUI();
    //set the click listener for the items
    getListView().setOnItemClickListener(new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            Intent i = new Intent(view.getContext(),
                    GetFeatureInfoAttributeActivity.class);
            i.putExtras(getActivity().getIntent().getExtras());
            i.removeExtra("layers");
            // add a list with only one layer
            ArrayList<String> subList = new ArrayList<String>();
            FeaturePolygonQueryResult item = (FeaturePolygonQueryResult) parent
                    .getAdapter().getItem(position);
            subList.add(item.getLayerName());
            i.putExtra("layers", subList);
            i.putExtra("start", 0);
            i.putExtra("limit", 1);
            //don't allow picking the position 
            String action = getActivity().getIntent().getAction();
            i.setAction(action);
            getActivity().startActivityForResult(i,
                    GetFeatureInfoAttributeActivity.GET_ITEM);
        }
    });
}

/**
 * create an array of <FeatureCircleTaskQuery> from the <FeatureCircleQuery>
 * and the list of layers. This array can be passed to the loader to perform
 * a query 
 * @param layers a list of <String> for whom to generate the query
 * @param query the base query (bounding box etc..) 
 * @return an array of <FeatureCircleTaskQuery> to pass to a loader
 */
private FeaturePolygonTaskQuery[] createTaskQueryQueue(ArrayList<String> layers,
        FeaturePolygonQuery query) {
    final SpatialDataSourceManager sdbManager = SpatialDataSourceManager
            .getInstance();
    int querySize = layers.size();
    FeaturePolygonTaskQuery[] queryQueue = new FeaturePolygonTaskQuery[querySize];
    int index = 0;
    for (String layer : layers) {
        SpatialVectorTable table;
        try {
            table = sdbManager.getVectorTableByName(layer);
        } catch (Exception e1) {
            Log.e("FEATUREPolygon", "unable to get table:" + layer);
            continue;
        }
        FeaturePolygonTaskQuery taskquery = new FeaturePolygonTaskQuery(query);
        taskquery.setTable(table);
        taskquery.setHandler(sdbManager.getSpatialDataSourceHandler(table));
        // query.setStart(0);
        taskquery.setLimit(1);

        queryQueue[index] = taskquery;
        index++;
    }
    return queryQueue;
}

/**
 * Create the loader
 */
@Override
public Loader<List<FeaturePolygonQueryResult>> onCreateLoader(int id, Bundle args) {

    return new FeaturePolygonLoader(getSherlockActivity(), queryQueue);
}

@Override
public void onLoadFinished(Loader<List<FeaturePolygonQueryResult>> loader,
        List<FeaturePolygonQueryResult> results) {
    //for each result, an entry is added to the list if
    // it contains a result
    for (FeaturePolygonQueryResult result : results) {
        if (result.getFeatures().size() > 0) {
            adapter.add(result);
        }
    }
    if (adapter.isEmpty()) {
        setNoData();
    }
    stopLoadingGUI();

}

@Override
public void onLoaderReset(Loader<List<FeaturePolygonQueryResult>> arg0) {
    adapter.clear();
}

/*
 * (non-Javadoc)
 * @see android.support.v4.app.Fragment#onDestroy()
 */
@Override
public void onDestroy() {
    // TODO try to kill the load process
	    super.onDestroy();
	}
}