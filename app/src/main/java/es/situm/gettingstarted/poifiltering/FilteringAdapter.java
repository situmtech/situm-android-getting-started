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
import es.situm.sdk.model.cartography.Poi;

/**
 *
 * Created by alejandro.trigo on 12/01/18.
 */

public class FilteringAdapter extends RecyclerView.Adapter<FilteringAdapter.FilteringViewHolder> {

    private List<Poi> mPoiList = new ArrayList<>();


    public class FilteringViewHolder extends RecyclerView.ViewHolder{

        private TextView tvSearchData;

        public FilteringViewHolder(View view) {
            super(view);
            tvSearchData =(TextView) view.findViewById(R.id.tv_search_data);
        }
    }

    @Override
    public FilteringViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.search_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new FilteringAdapter.FilteringViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FilteringViewHolder holder, int position) {
        String infoForThisEvent = mPoiList.get(position).getName();
        holder.tvSearchData.setText(infoForThisEvent);
    }

    @Override
    public int getItemCount() {
        if (mPoiList == null) return 0;
        return mPoiList.size();
    }

    public void setSearchData(List<Poi> poiList){
        mPoiList.clear();
        if(!mPoiList.isEmpty() || mPoiList != null){
            mPoiList.addAll(poiList);
        }
        notifyDataSetChanged();
    }

}
