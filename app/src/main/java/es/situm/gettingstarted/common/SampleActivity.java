package es.situm.gettingstarted.common;

import androidx.appcompat.app.AppCompatActivity;

import es.situm.sdk.model.cartography.Building;

public abstract class SampleActivity extends AppCompatActivity {

    public static final String EXTRA_BUILDING = "EXTRA_BUILDING";

    protected Building getBuildingFromIntent() {
        return (Building) getIntent().getParcelableExtra(EXTRA_BUILDING);
    }

}
