package es.situm.gettingstarted.userinsideevent;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import es.situm.gettingstarted.R;
import es.situm.sdk.SitumSdk;
import es.situm.sdk.error.Error;
import es.situm.sdk.model.cartography.Building;
import es.situm.sdk.model.cartography.BuildingInfo;
import es.situm.sdk.utils.Handler;

public class ListBuildingsActivity extends AppCompatActivity implements ListBuildingsAdapter.ListBuildingsAdapterOnClickHandler{

    private static final String TAG = es.situm.gettingstarted.userinsideevent.ListBuildingsActivity.class.getSimpleName();

    private RecyclerView mRVBuildingList;
    private TextView mErrorMessage;
    private ProgressBar mProgressBar;

    private ListBuildingsAdapter adapter;

    List<Building> buildingList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_list);

        mRVBuildingList = (RecyclerView) findViewById(R.id.rv_building_list);
        mErrorMessage = (TextView) findViewById(R.id.tv_building_error_message);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_loading_buildings);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRVBuildingList.setLayoutManager(linearLayoutManager);
        mRVBuildingList.setHasFixedSize(true);

        adapter = new ListBuildingsAdapter(this);
        mRVBuildingList.setAdapter(adapter);


        loadBuildings();

    }


    private void showBuildingDataView(){
        mErrorMessage.setVisibility(View.INVISIBLE);
        mRVBuildingList.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage(){
        mRVBuildingList.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.VISIBLE);
    }

    private void loadBuildings(){

        SitumSdk.communicationManager().fetchBuildings(new Handler<Collection<Building>>() {

            @Override
            public void onSuccess(Collection<Building> buildings) {
                Log.d(TAG, "onSuccess: buildings fetched");
                for (Building b : buildings) Log.d(TAG, "Building: " + b.getName());
                buildingList.clear();
                buildingList.addAll(buildings);
                mProgressBar.setVisibility(View.INVISIBLE);
                showBuildingDataView();
                adapter.setBuildingData(buildingList);
            }

            @Override
            public void onFailure(Error error) {
                showErrorMessage();
                Log.e(TAG, "onFailure: fetching buildings: " + error);
            }
        });
    }

    private void startUserInsideEventActivity(BuildingInfo buildingInfo){
        Intent intent = new Intent(getApplicationContext(), UserInsideEventActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, buildingInfo);
        startActivity(intent);
    }
    private void getBuildingInfo(Building building){
        SitumSdk.communicationManager().fetchBuildingInfo(building.getIdentifier(), new Handler<BuildingInfo>() {
            @Override
            public void onSuccess(BuildingInfo buildingInfo) {
                mProgressBar.setVisibility(View.INVISIBLE);
                startUserInsideEventActivity(buildingInfo);
            }

            @Override
            public void onFailure(Error error) {
                showErrorMessage();
                Log.e(TAG, "onFailure: fetching buildings info: " + error);
            }
        });
    }
    @Override
    public void onClick(Building building) {
        mProgressBar.setVisibility(View.VISIBLE);
        getBuildingInfo(building);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.action_refresh){
            mProgressBar.setVisibility(View.VISIBLE);
            buildingList.clear();
            adapter.setBuildingData(new ArrayList<Building>());
            loadBuildings();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}