package es.situm.gettingstarted.animateposition;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import es.situm.gettingstarted.R;

import java.util.ArrayList;
import java.util.List;

import es.situm.sdk.model.cartography.Building;

/**
 * Created by alejandro.trigo on 31/01/18.
 */

public class SelectBuildingAdapter extends RecyclerView.Adapter<SelectBuildingAdapter.SelectBuildingViewHolder> {

    private SelectBuildingOnClickHandler mClickHandler;
    private List<Building> mBuildingList = new ArrayList<>();

    interface SelectBuildingOnClickHandler {
        void onClick(String eventClick);
    }

    public SelectBuildingAdapter(SelectBuildingAdapter.SelectBuildingOnClickHandler handler){
        mClickHandler = handler;
    }

    public class SelectBuildingViewHolder extends RecyclerView.ViewHolder implements RecyclerView.OnClickListener{

        private TextView mBuildingData;

        public SelectBuildingViewHolder(View view){
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
    public SelectBuildingViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.building_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new SelectBuildingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SelectBuildingViewHolder holder, int position) {
        String infoForThisEvent = mBuildingList.get(position).getName();
        holder.mBuildingData.setText(infoForThisEvent);
    }

    @Override
    public int getItemCount() {
        if (mBuildingList == null) return 0;
        else return mBuildingList.size();
    }

    public void setBuildingData(List<Building> buildings){
        mBuildingList.clear();
        if(!buildings.isEmpty() || buildings != null){
            mBuildingList.addAll(buildings);
        }
        notifyDataSetChanged();
    }

}

