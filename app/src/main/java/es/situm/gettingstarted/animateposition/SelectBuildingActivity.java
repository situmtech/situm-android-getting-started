package es.situm.gettingstarted.animateposition;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import es.situm.gettingstarted.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import es.situm.sdk.SitumSdk;
import es.situm.sdk.error.Error;
import es.situm.sdk.model.cartography.Building;
import es.situm.sdk.utils.Handler;

/**
 *
 * Created by alejandro.trigo on 31/01/18.
 */

public class SelectBuildingActivity extends AppCompatActivity implements SelectBuildingAdapter.SelectBuildingOnClickHandler {

    private RecyclerView mRVBuildingList;
    private TextView mErrorMessage;
    private ProgressBar mProgressBar;

    private SelectBuildingAdapter adapter;

    List<Building> buildingList = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_list);

        mRVBuildingList = (RecyclerView) findViewById(R.id.rv_building_list);
        mErrorMessage = (TextView) findViewById(R.id.tv_building_error_message);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_loading_buildings);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRVBuildingList.setLayoutManager(linearLayoutManager);
        mRVBuildingList.setHasFixedSize(true);

        adapter = new SelectBuildingAdapter(this);
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
                if (buildings.isEmpty()){
                    mErrorMessage.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.INVISIBLE);
                }else {

                    buildingList.clear();
                    buildingList.addAll(buildings);
                    mProgressBar.setVisibility(View.INVISIBLE);
                    showBuildingDataView();
                    adapter.setBuildingData(buildingList);
                }
            }

            @Override
            public void onFailure(Error error) {
                showErrorMessage();
            }
        });
    }

    @Override
    public void onClick(String eventClick) {
        Intent intent = new Intent(this, AnimatePositionActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT ,eventClick);
        startActivity(intent);
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
