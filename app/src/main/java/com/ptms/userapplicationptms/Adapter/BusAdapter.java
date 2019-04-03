package com.ptms.userapplicationptms.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ptms.userapplicationptms.Model.SingleBusClass;
import com.ptms.userapplicationptms.R;

import java.util.ArrayList;

public class BusAdapter extends RecyclerView.Adapter<BusAdapter.BusViewHolder> {

    public class BusViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {

        TextView routeSrc,routeDest,departTime,busid,routeid;

        BusViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            routeSrc =  itemView.findViewById(R.id.routeSrc);
            routeDest =  itemView.findViewById(R.id.routeDest);
            departTime =  itemView.findViewById(R.id.departTime);
            busid = itemView.findViewById(R.id.busId);
            routeid = itemView.findViewById(R.id.routeId);
        }

        @Override
        public void onClick(View v) {
            if (mOnEntryClickListener != null) {
                mOnEntryClickListener.onEntryClick(v, getLayoutPosition(),busid.getText()
                        .toString(),routeid.getText().toString());
            }
        }
    }

    private ArrayList<SingleBusClass> mCustomObjects;

    public BusAdapter(ArrayList<SingleBusClass> arrayList) {
        mCustomObjects = arrayList;
    }

    @Override
    public int getItemCount() {
        return mCustomObjects.size();
    }

    @Override
    public BusViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_bus_layout, parent,
                false);
        return new BusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BusViewHolder holder, int position) {
        SingleBusClass object = mCustomObjects.get(position);

        String rSrc = object.getRouteSource();
        String rDest= object.getRouteDestination();
        String time= object.getDepartTime();
        String bus_id = object.getBus_id();
        String route_id = object.getRoute_id();

        holder.routeSrc.setText(rSrc);
        holder.routeDest.setText(rDest);
        holder.departTime.setText(time);
        holder.busid.setText(bus_id);
        holder.routeid.setText(route_id);


    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    private OnEntryClickListener mOnEntryClickListener;

    public interface OnEntryClickListener {
        void onEntryClick(View view, int position,String busid,String routeid);
    }

    public void setOnEntryClickListener(OnEntryClickListener onEntryClickListener) {
        mOnEntryClickListener = onEntryClickListener;
    }

}

