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

        TextView routeSrc,routeDest,departTime;

        BusViewHolder(View itemView) {
            super(itemView);
            routeSrc = (TextView) itemView.findViewById(R.id.routeSrc);
            routeDest = (TextView) itemView.findViewById(R.id.routeDest);
            departTime = (TextView) itemView.findViewById(R.id.departTime);

        }

        @Override
        public void onClick(View v) {
            if (mOnEntryClickListener != null) {
                mOnEntryClickListener.onEntryClick(v, getLayoutPosition());
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

        // My example assumes CustomClass objects have getFirstText() and getSecondText() methods
        String rSrc = object.getRouteSource();
        String rDest= object.getRouteDestination();
        String time= object.getDepartTime();

        holder.routeSrc.setText(rSrc);
        holder.routeDest.setText(rDest);
        holder.departTime.setText(time);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
    private OnEntryClickListener mOnEntryClickListener;

    public interface OnEntryClickListener {
        void onEntryClick(View view, int position);
    }

    public void setOnEntryClickListener(OnEntryClickListener onEntryClickListener) {
        mOnEntryClickListener = onEntryClickListener;
    }

}

