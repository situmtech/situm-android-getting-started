package es.situm.gettingstarted.common.floorselector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import es.situm.gettingstarted.R;
import es.situm.sdk.model.cartography.Floor;

public class FloorAdapter extends RecyclerView.Adapter<FloorAdapter.ViewHolder> {

    private final OnItemClickListener onItemClickListener;
    private final Context context;
    private Floor selected;
    private Floor positioningFloor;
    private List<Floor> floors;

    FloorAdapter(List<Floor> floors, OnItemClickListener onItemClickListener, Context context) {
        setFloors(floors);
        this.onItemClickListener = onItemClickListener;
        this.context = context;
    }

    /**
     * Set the floors of the building
     *
     * @param floors List<Floor>
     */
    void setFloors(List<Floor> floors) {
        this.floors = floors;
        notifyItemRangeChanged(0, getItemCount());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.situm_item_level, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Floor floor = floors.get(position);
        holder.bind(floor);
    }

    @Override
    public int getItemCount() {
        return floors.size();
    }

    @Nullable
    public Floor getSelected() {
        return selected;
    }

    /**
     * Select the floor provided
     *
     * @param floor Floor
     */
    void select(Floor floor) {
        int indexPrevSelected = floors.indexOf(selected);
        int indexNewSelected = floors.indexOf(floor);
        selected = floor;
        notifyItemChanged(indexPrevSelected);
        notifyItemChanged(indexNewSelected);
    }

    /**
     * Updates the positioning floor
     *
     * @param newPositioningFloor Floor
     */
    void positioningFloorChangedTo(Floor newPositioningFloor, boolean userPickedFloor) {
        int indexPrevPositioned = floors.indexOf(positioningFloor);
        int indexNewPosition = floors.indexOf(newPositioningFloor);
        if(userPickedFloor){
            select(newPositioningFloor);
        }
        positioningFloor = newPositioningFloor;
        notifyItemChanged(indexPrevPositioned);
        notifyItemChanged(indexNewPosition);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvLevelNumber;

        ViewHolder(View itemView) {
            super(itemView);
            tvLevelNumber = itemView.findViewById(R.id.txt_level_number);
        }

        void bind(Floor floor) {
            //By default the item is not SELECTED or POSITIONING
            tvLevelNumber.setText(FloorUtils.getNameOrLevel(floor));
            itemView.setBackgroundResource(R.drawable.situm_item_level_background);
            tvLevelNumber.setTextAppearance(context, R.style.situm_normalText);

            if(positioningFloor != null && positioningFloor.equals(floor)){ // POSITIONING
                itemView.setBackgroundResource(R.drawable.situm_item_level_background_positioning);
                tvLevelNumber.setTextAppearance(context, R.style.situm_boldText);
            }else if (selected != null && selected.equals(floor)){ // SELECTED
                itemView.setBackgroundResource(R.drawable.situm_item_level_background_selected);
            }

            itemView.setOnClickListener(v -> onItemClickListener.onItemClick(floor));
        }
    }

    interface OnItemClickListener {
        void onItemClick(Floor floor);
    }
}