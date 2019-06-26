package es.situm.gettingstarted.common.selectbuilding;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.util.Collection;

import es.situm.gettingstarted.R;
import es.situm.gettingstarted.common.SampleActivity;
import es.situm.sdk.SitumSdk;
import es.situm.sdk.error.Error;
import es.situm.sdk.model.cartography.Building;
import es.situm.sdk.utils.Handler;

public class SelectBuildingActivity extends AppCompatActivity implements SelectBuildingAdapter.OnBuildingSelectedCallback {

    private static final String TAG = SelectBuildingActivity.class.getSimpleName();
    private static final String EXTRA_NEXT_ACTIVITY = "EXTRA_NEXT_ACTIVITY";

    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private SelectBuildingAdapter adapter;
    private Class nextActivity;

    public static Intent createIntent(Context context, Class nextActivity) {
        Intent intent = new Intent(context, SelectBuildingActivity.class);
        intent.putExtra(EXTRA_NEXT_ACTIVITY, nextActivity);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_building);

        nextActivity = (Class) getIntent().getSerializableExtra(EXTRA_NEXT_ACTIVITY);

        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.loading);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new SelectBuildingAdapter(this);
        recyclerView.setAdapter(adapter);

        loadBuildings();
    }

    private void loadBuildings() {
        SitumSdk.communicationManager().fetchBuildings(new Handler<Collection<Building>>() {

            @Override
            public void onSuccess(Collection<Building> buildings) {
                progressBar.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                adapter.setBuildings(buildings);
            }

            @Override
            public void onFailure(Error error) {
                Log.e(TAG, "onFailure: " + error);
                showErrorMessage(error.getMessage());
            }
        });
    }

    private void showErrorMessage(String message) {
        Snackbar.make(findViewById(R.id.container), message, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry, v -> loadBuildings())
                .show();
    }

    @Override
    public void onBuildingSelected(Building building) {
        Intent intent = new Intent(this, nextActivity);
        intent.putExtra(SampleActivity.EXTRA_BUILDING, building);
        startActivity(intent);
    }

}
