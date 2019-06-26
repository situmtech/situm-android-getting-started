package es.situm.gettingstarted.buildingevents;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import es.situm.gettingstarted.R;
import es.situm.sdk.model.cartography.Building;

/**
 *
 * Created by alejandro.trigo on 12/01/18.
 */

public class BuildingListAdapter extends RecyclerView.Adapter<BuildingListAdapter.BuildingListViewHolder> {

    private List<Building> mBuildingList = new ArrayList<>();
    private BuildingListAdapterOnClickHandler mClickHandler;


    interface BuildingListAdapterOnClickHandler{
        void onClick(String eventClick);
    }

    public BuildingListAdapter(BuildingListAdapterOnClickHandler handler){
        mClickHandler = handler;
    }

    public class BuildingListViewHolder extends RecyclerView.ViewHolder implements RecyclerView.OnClickListener{

        private TextView mBuildingData;

        public BuildingListViewHolder(View view) {
            super(view);
            mBuildingData = (TextView) view.findViewById(R.id.tv_building_data);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            String buildingId = mBuildingList.get(adapterPosition).getIdentifier();
            mClickHandler.onClick(buildingId);
        }
    }

    @Override
    public BuildingListViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.building_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new BuildingListAdapter.BuildingListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BuildingListViewHolder holder, int position) {
        String infoForThisEvent = mBuildingList.get(position).getName();
        holder.mBuildingData.setText(infoForThisEvent);
    }

    @Override
    public int getItemCount() {
        if (mBuildingList == null) return 0;
        return mBuildingList.size();
    }

    public void setBuildingData(List<Building> buildingList){
        mBuildingList = new ArrayList<>(buildingList);
        notifyDataSetChanged();
    }

}
