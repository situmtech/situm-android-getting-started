package es.situm.gettingstarted.buildingevents;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import java.util.ArrayList;

import es.situm.gettingstarted.R;
import es.situm.sdk.v1.SitumEvent;

/**
 *
 * Created by alejandro.trigo on 12/01/18.
 */

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventAdapterViewHolder> {

    private ArrayList<SitumEvent> mEventData = new ArrayList<>();
    private EventAdapterOnClickHandler mClickHandler;

    // EVENTADAPTERONCLICKHANDLER INTERFACE
    interface EventAdapterOnClickHandler {
        void onClick(String eventClick);
    }
    // END EVENTADAPTERONCLICKHANDLER INTERFACE


    public EventAdapter(EventAdapterOnClickHandler handler) { mClickHandler = handler; }


    // EVENTADAPTERVIEWHOLDER CLASS
    public class EventAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final TextView mEventTextView;
        private final WebView mWebView;

        private EventAdapterViewHolder(View view){
            super(view);
            mEventTextView = (TextView) view.findViewById(R.id.tv_event_data);
            mWebView = (WebView) view.findViewById(R.id.wv_event_data);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            if (mWebView.getVisibility() == (View.VISIBLE)){
                mWebView.setVisibility(View.GONE);
            }else{
                mWebView.setVisibility(View.VISIBLE);
            }

            mWebView.loadDataWithBaseURL(null, mEventData.get(adapterPosition).getHtml(), "text/html", "utf-8", null);

            mClickHandler.onClick("");

        }
    }
    //END EVENTADAPTERVIEWHOLDER CLASS



    @Override
    public EventAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        Context context = viewGroup.getContext();
        int layourIdForListItem = R.layout.event_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);


        View view = inflater.inflate(layourIdForListItem, viewGroup, false);
        return new EventAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EventAdapterViewHolder holder, int position) {
        String infoForThisEvent = mEventData.get(position).getName();
        holder.mEventTextView.setText(infoForThisEvent);
    }

    @Override
    public int getItemCount() {
        if(mEventData == null) return 0;
        return mEventData.size();
    }

    public void setEventData(ArrayList<SitumEvent> events ){
        mEventData.clear();
        if (!events.isEmpty() || events != null) {
            mEventData.addAll(events);
        }

        notifyDataSetChanged();

    }



}
