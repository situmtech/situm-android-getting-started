package es.situm.gettingstarted.poifiltering;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import es.situm.gettingstarted.R;
import es.situm.sdk.SitumSdk;
import es.situm.sdk.error.Error;
import es.situm.sdk.model.cartography.Building;
import es.situm.sdk.model.cartography.Poi;
import es.situm.sdk.utils.Handler;

/**
 *
 * Created by alejandro.trigo on 12/01/18.
 */

public class FilteringActivity extends AppCompatActivity {

    private static final String TAG = FilteringActivity.class.getSimpleName();
    private List<Poi> poiList = new ArrayList<>();
    private String buildingId;
    private Building building;

    private EditText etKeyValue;
    private EditText etValueValue;
    private Button btnSearch;
    private RecyclerView rvSearchResults;
    private ProgressBar pbSearchLoading;
    private TextView tvSearchError;

    private String keyText;
    private String valueText;

    private FilteringAdapter filteringAdapter;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtering);

        Intent intent = getIntent();

        if (intent != null)
            if (intent.hasExtra(Intent.EXTRA_TEXT))
                buildingId = intent.getStringExtra(Intent.EXTRA_TEXT);


        etKeyValue = (EditText) findViewById(R.id.et_key_value);
        etValueValue = (EditText) findViewById(R.id.et_value_value);
        btnSearch = (Button) findViewById(R.id.btn_search);
        rvSearchResults = (RecyclerView) findViewById(R.id.rv_filter_search);
        pbSearchLoading = (ProgressBar) findViewById(R.id.pb_loading_filter);
        tvSearchError = (TextView) findViewById(R.id.tv_error_search);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        rvSearchResults.setLayoutManager(linearLayoutManager);
        rvSearchResults.setHasFixedSize(true);

        filteringAdapter = new FilteringAdapter();

        rvSearchResults.setAdapter(filteringAdapter);

        getPois();

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyText = etKeyValue.getText().toString();
                valueText = etValueValue.getText().toString();
                filter(keyText, valueText);
            }
        });

    }

    private void getPois(){
        /* NOS QUEDAMOS AQUI PENSANDO EN COMO FILTRAR LA INFO */

        SitumSdk.communicationManager().fetchBuildings(new Handler<Collection<Building>>() {

            @Override
            public void onSuccess(Collection<Building> buildings) {
                Log.d(TAG, "onSuccess: buildings fetched");
                for (Building bui : buildings) {
                    if (bui.getIdentifier().equals(buildingId)) {
                        building = bui;
                    }
                }

                SitumSdk.communicationManager().fetchIndoorPOIsFromBuilding(building.getIdentifier(), new Handler<Collection<Poi>>(){

                    @Override
                    public void onSuccess(Collection<Poi> pois) {
                        for(Poi poi : pois){
                            Log.i(TAG, "onSuccess: poi: " + poi);
                        }
                        poiList.clear();
                        poiList.addAll(pois);
                        showPoiDataView();
                        filteringAdapter.setSearchData(poiList);
                        pbSearchLoading.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onFailure(Error error) {
                        Log.e(TAG, "onFailure: fetching pois: " + error);
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

    private void showErrorMessage(){
        rvSearchResults.setVisibility(View.INVISIBLE);
        tvSearchError.setVisibility(View.VISIBLE);
    }

    private void showPoiDataView(){
        tvSearchError.setVisibility(View.INVISIBLE);
        rvSearchResults.setVisibility(View.VISIBLE);
    }

    public void filter (String key, String value){

        List<Poi> poiListTemp = new ArrayList<>();
        Map<String, String> kVList;


        for(Poi p : poiList){
            kVList = p.getCustomFields();
            Log.d(TAG, kVList.toString());
            if(!kVList.isEmpty()) {
                if(!kVList.containsKey(key)){;
                    continue;
                }
                if(!kVList.containsValue(value)){
                    continue;
                }
                if (kVList.get(key).equals(value)) {
                    poiListTemp.add(p);
                }

            }
            else{
                continue;
            }
        }
        if(!poiListTemp.isEmpty()){
            filteringAdapter.setSearchData(poiListTemp);
        }else{
            showErrorMessage();
        }

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
            pbSearchLoading.setVisibility(View.VISIBLE);
            poiList.clear();
            filteringAdapter.setSearchData(new ArrayList<Poi>());
            getPois();
            etValueValue.setText("");
            etKeyValue.setText("");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
