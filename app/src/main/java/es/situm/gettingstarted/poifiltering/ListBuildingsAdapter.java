package es.situm.gettingstarted.poifiltering;

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

public class ListBuildingsAdapter extends RecyclerView.Adapter<ListBuildingsAdapter.ListBuildingsViewHolder>{

    private List<Building> mBuildingList = new ArrayList<>();
    private ListBuildingsAdapter.ListBuildingsAdapterOnClickHandler mClickHandler;


    interface ListBuildingsAdapterOnClickHandler{
        void onClick(String eventClick);
    }

    public ListBuildingsAdapter(ListBuildingsAdapter.ListBuildingsAdapterOnClickHandler handler){
        mClickHandler = handler;
    }

    public class ListBuildingsViewHolder extends RecyclerView.ViewHolder implements RecyclerView.OnClickListener{

        private TextView mBuildingData;

        public ListBuildingsViewHolder(View view) {
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
    public ListBuildingsAdapter.ListBuildingsViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.building_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new ListBuildingsAdapter.ListBuildingsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListBuildingsAdapter.ListBuildingsViewHolder holder, int position) {
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

