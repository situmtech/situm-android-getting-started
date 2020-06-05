package es.situm.gettingstarted.buildingevents;

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

import es.situm.gettingstarted.R;
import es.situm.sdk.SitumSdk;
import es.situm.sdk.error.Error;
import es.situm.sdk.model.cartography.Building;
import es.situm.sdk.utils.Handler;
import es.situm.sdk.v1.SitumEvent;

/**
 * Created by alejandro.trigo on 12/01/18.
 *
 */

public class GetBuildingEvents extends AppCompatActivity implements EventAdapter.EventAdapterOnClickHandler{

    private static final String TAG = GetBuildingEvents.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private TextView mErrorMessage;
    private ProgressBar mProgressBar;

    private String buildingId;

    private Building building;
    ArrayList<SitumEvent> events = new ArrayList<>();

    private EventAdapter eventAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_events);

        Intent intent = getIntent();

        if(intent != null) {
            if (intent.hasExtra(Intent.EXTRA_TEXT)) {
                buildingId = intent.getStringExtra(Intent.EXTRA_TEXT);
            }
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_event_list) ;
        mErrorMessage = (TextView) findViewById(R.id.tv_error_message);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        eventAdapter = new EventAdapter(this);

        mRecyclerView.setAdapter(eventAdapter);

        getEvents();

    }


    @Override
    public void onClick(String eventData){

    }

    private void showEventDataView(){
        mErrorMessage.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage(){
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.VISIBLE);
    }

    private void getEvents() {

        SitumSdk.communicationManager().fetchBuildings(new Handler<Collection<Building>>() {

            @Override
            public void onSuccess(Collection<Building> buildings) {
                Log.d(TAG, "onSuccess: buildings fetched");
                for (Building bui : buildings) {
                    if (bui.getIdentifier().equals(buildingId)) {
                        building = bui;
                    }
                }

                SitumSdk.communicationManager().fetchEventsFromBuilding(building.getIdentifier(), new Handler<Collection<SitumEvent>>(){

                    @Override
                    public void onSuccess(Collection<SitumEvent> situmEvents) {
                        events.clear();
                        events.addAll(situmEvents);
                        showEventDataView();
                        eventAdapter.setEventData(events);
                        mProgressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onFailure(Error error) {
                        Log.e(TAG, "onFailure: fetching events: " + error);
                        showErrorMessage();
                    }
                });

            }

            @Override
            public void onFailure(Error error) {
                Log.e(TAG, "onFailure: fetching buildings: " + error);
                showErrorMessage();
            }
        });

    }



    // MENU OPTIONS

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
            events.clear();
            eventAdapter.setEventData(new ArrayList<SitumEvent>());
            getEvents();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // END MENU OPTIONS
}
