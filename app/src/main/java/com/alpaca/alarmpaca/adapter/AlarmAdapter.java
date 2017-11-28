package com.alpaca.alarmpaca.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.alpaca.alarmpaca.R;
import com.alpaca.alarmpaca.model.Alarm;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by Far on 11/26/2017 AD.
 */

public class AlarmAdapter extends RealmRecyclerViewAdapter<Alarm, AlarmAdapter.MyViewHolder> {

    public AlarmAdapter(OrderedRealmCollection<Alarm> data) {
        super(data, true);
        setHasStableIds(true);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_alarm, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public long getItemId(int position) {
        //noinspection ConstantConditions
        return getItem(position).getId();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView timeTv;
        TextView periodTv;
        TextView dateTv;
        Switch toggleBtn;

        MyViewHolder(View itemView) {
            super(itemView);
            timeTv = itemView.findViewById(R.id.timeTv);
            periodTv = itemView.findViewById(R.id.periodTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            toggleBtn = itemView.findViewById(R.id.toggleBtn);
        }

        void bind(Alarm data) {

            timeTv.setText(data.getTime());
            periodTv.setText(data.getPeriod());
//            dateTv.setText(data.getDate());
            toggleBtn.setChecked(data.isActivated());
        }
    }
}
