package es.situm.gettingstarted.common.selectbuilding;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import es.situm.gettingstarted.R;
import es.situm.sdk.model.cartography.Building;

public class SelectBuildingAdapter
        extends RecyclerView.Adapter<SelectBuildingAdapter.SelectBuildingViewHolder> {

    private OnBuildingSelectedCallback callback;
    private List<Building> buildings = new ArrayList<>();

    interface OnBuildingSelectedCallback {
        void onBuildingSelected(Building building);
    }

    SelectBuildingAdapter(OnBuildingSelectedCallback callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    public SelectBuildingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.item_building_name, viewGroup, false);
        return new SelectBuildingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectBuildingViewHolder holder, int position) {
        Building building = buildings.get(position);
        holder.bind(building);

        holder.itemView.setOnClickListener(v -> callback.onBuildingSelected(building));
    }

    @Override
    public int getItemCount() {
        return buildings.size();
    }

    public void setBuildings(Collection<Building> buildings) {
        this.buildings.clear();
        this.buildings.addAll(buildings);
        notifyDataSetChanged();
    }

    static class SelectBuildingViewHolder extends RecyclerView.ViewHolder {

        private TextView buildingName;

        SelectBuildingViewHolder(View view) {
            super(view);
            buildingName = view.findViewById(R.id.tv_building_data);
        }

        void bind(Building building) {
            buildingName.setText(building.getName());
        }
    }

}

