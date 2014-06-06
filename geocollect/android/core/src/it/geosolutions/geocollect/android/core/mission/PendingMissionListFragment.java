/*
 * GeoSolutions - MapstoreMobile - GeoSpatial Framework on Android based devices
 * Copyright (C) 2014  GeoSolutions (www.geo-solutions.it)
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
package it.geosolutions.geocollect.android.core.mission;

import it.geosolutions.android.map.wfs.geojson.feature.Feature;
import it.geosolutions.geocollect.android.core.R;
import it.geosolutions.geocollect.android.core.mission.utils.MissionUtils;
import it.geosolutions.geocollect.model.config.MissionTemplate;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;


/**
 * A list fragment representing a list of Pending Missions. This fragment also
 * supports tablet devices by allowing list items to be given an 'activated'
 * state upon selection. This helps indicate which item is currently being
 * viewed in a {@link PendingMissionDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class PendingMissionListFragment 
	extends SherlockListFragment 
	implements  LoaderCallbacks<List<Feature>>,	OnScrollListener{

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	private static final int LOADER_INDEX = 0;

	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private Callbacks listSelectionCallbacks = sDummyCallbacks;

	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;
	
	/**
	 * The loader to load WFS data
	 */
	private Loader loader;
	
	/**
	 * The adapter for the Feature
	 */
	private FeatureAdapter adapter;
	
	/**
	 * Callback for the Loader
	 */
	private LoaderCallbacks<List<Feature>> mCallbacks;

	
	/**
	 * Boolean value that allow to start loading only once at time
	 */
	private boolean isLoading;
	/**
	 * The template that provides the form and the 
	 */
	private MissionTemplate missionTemplate;
	
	/**
	 * page number for remote queries
	 */
	private int page=0;
	/**
	 * page size for remote queries
	 */
	private int pagesize=150;
	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelected(Object object);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(Object object) {
		}
	};

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public PendingMissionListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 setRetainInstance(true);
		// setup the listView
		missionTemplate =  MissionUtils.getDefaultTemplate(getSherlockActivity());
	    adapter = new FeatureAdapter(getSherlockActivity(),
	            R.layout.mission_resource_row,missionTemplate);
	    setListAdapter(adapter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {

	    startDataLoading(missionTemplate, LOADER_INDEX);
	    return inflater.inflate(R.layout.mission_resource_list, container, false); 
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
	        Log.v("GEOSTORE_LOADER", "task terminated");
	        
	    }
	    adapter.notifyDataSetChanged();
	    isLoading=false;
	}
	
	/**
	 * Sets the view to show that no data are available
	 */
	private void setNoData() {
	    ((TextView) getView().findViewById(R.id.empty_text))
	            .setText(R.string.geostore_extracting_no_result);
	    getView().findViewById(R.id.progress_bar).setVisibility(TextView.GONE);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// Restore the previously serialized activated item position.
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState
					.getInt(STATE_ACTIVATED_POSITION));
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		listSelectionCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		listSelectionCallbacks = sDummyCallbacks;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);

		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		listSelectionCallbacks.onItemSelected(getListAdapter().getItem(position));
	}

	@Override
	public void onSaveInstanceState(Bundle outState) { 
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	private void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}

	/* (non-Javadoc)
	 * @see com.actionbarsherlock.view.ActionMode.Callback#onCreateActionMode(com.actionbarsherlock.view.ActionMode, com.actionbarsherlock.view.Menu)
	 */
	

	/* (non-Javadoc)
	 * @see android.widget.AbsListView.OnScrollListener#onScrollStateChanged(android.widget.AbsListView, int)
	 */
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see android.widget.AbsListView.OnScrollListener#onScroll(android.widget.AbsListView, int, int, int)
	 */
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		//check if applicable
		if (adapter == null){
	        return ;
		}
	    if (adapter.getCount() == 0){
	        return ;
	    }
	    if(loader==null){
	    	return;
	    }
	    
	    //if the last item is visible and can load more resources
	    //load more resources
	    int l = visibleItemCount + firstVisibleItem;
	    if (l >= totalItemCount && !isLoading ) {
	        // It is time to add new data. We call the listener
	        //this.addFooterView(footer);
	        isLoading = true;
	        loadMore();
	    }
		
	}
	/* (non-Javadoc)
	 * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onCreateLoader(int, android.os.Bundle)
	 */
	@Override
	public Loader<List<Feature>> onCreateLoader(int arg0, Bundle arg1) {
		getSherlockActivity().setSupportProgressBarIndeterminateVisibility(true);
		getSherlockActivity().getSupportActionBar();
		 loader = MissionUtils.createMissionLoader(missionTemplate,getSherlockActivity(),page,pagesize);
	     return loader; 
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onLoadFinished(android.support.v4.content.Loader, java.lang.Object)
	 */
	@Override
	public void onLoadFinished(Loader<List<Feature>> loader, List<Feature> results) {
		if(results == null){
			   Toast.makeText(getSherlockActivity(), R.string.error_connectivity_problem, Toast.LENGTH_SHORT).show();
			   setNoData();
		   }else{
			   //add loaded resources to the listView
				for(Feature a : results ){
					adapter.add(a);
				}
				if (adapter.isEmpty()) {
			        setNoData();
			    }else{
			    }
		   }
		  
		   stopLoadingGUI();
		
	}
	

	/* (non-Javadoc)
	 * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onLoaderReset(android.support.v4.content.Loader)
	 */
	@Override
	public void onLoaderReset(Loader<List<Feature>> arg0) {
		adapter.clear();
		
	}
	/**
	 * Loads more data from the Loader
	 */
	protected void loadMore() {
		
		if(loader!=null){
			
				page++;
				getLoaderManager().restartLoader(LOADER_INDEX, null, this);
			
			
		}
	}
	/**
	 * Create the data loader and bind the loader to the
	 * parent callbacks
	 * @param URL (not used for now)
	 * @param loaderIndex a unique id for query loader
	 */
	private void startDataLoading(MissionTemplate t, int loaderIndex) {

	    // initialize Load Manager
	    mCallbacks = this;
	    //reset page
	    LoaderManager lm = getSherlockActivity().getSupportLoaderManager();
	    adapter.clear();
	    page=0;
	    lm.initLoader(loaderIndex, null, this); 
	}
}