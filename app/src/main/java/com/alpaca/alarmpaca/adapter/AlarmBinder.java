package com.alpaca.alarmpaca.adapter;

import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahamed.multiviewadapter.BaseViewHolder;
import com.ahamed.multiviewadapter.ItemBinder;
import com.ahamed.multiviewadapter.util.ItemDecorator;
import com.alpaca.alarmpaca.R;
import com.alpaca.alarmpaca.model.Alarm;
import com.alpaca.alarmpaca.util.AlarmMgrUtil;
import com.alpaca.alarmpaca.util.Contextor;
import com.alpaca.alarmpaca.util.RealmUtil;

import io.realm.Realm;


public class AlarmBinder extends ItemBinder<Alarm, AlarmBinder.ViewHolder> {

    private BaseViewHolder.OnItemLongClickListener<Alarm> listener;

    public AlarmBinder(ItemDecorator itemDecorator,
                       BaseViewHolder.OnItemLongClickListener<Alarm> listener) {
        super(itemDecorator);
        this.listener = listener;
    }

    @Override
    public ViewHolder create(LayoutInflater layoutInflater, ViewGroup parent) {
        return new ViewHolder(layoutInflater.inflate(R.layout.item_alarm, parent, false),
                listener);
    }

    @Override
    public void bind(ViewHolder holder, Alarm item) {
//        Log.wtf("AlarmBinder", "ViewHolder : Bind");
        holder.bind(item);
    }

    @Override
    public boolean canBindData(Object item) {
        return item instanceof Alarm;
    }

    @Override
    public int getSpanSize(int maxSpanCount) {
        return maxSpanCount;
    }

    static class ViewHolder extends BaseViewHolder<Alarm> {

        private TextView timeTv;
        private TextView periodTv;
        private TextView dateTv;
        private SwitchCompat toggleBtn;
        private ImageView ivIndicator;

        //        private Context context;
        private int alarmId;

        ViewHolder(View itemView,
                   final BaseViewHolder.OnItemLongClickListener<Alarm> listener) {
            super(itemView);

//            this.context = itemView.getContext();

            timeTv = itemView.findViewById(R.id.timeTv);
            periodTv = itemView.findViewById(R.id.periodTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            toggleBtn = itemView.findViewById(R.id.toggleBtn);
            ivIndicator = itemView.findViewById(R.id.iv_selection_indicator);

            setItemLongClickListener((view, item) -> {
                toggleItemSelection();
                if (!isInActionMode()) {
                    listener.onItemLongClick(view, item);
                }
                return true;
            });

            setItemClickListener((view, item) -> {
                if (isInActionMode()) {
                    toggleItemSelection();
                }
            });

            toggleBtn.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                Realm realm = RealmUtil.getRealmInstance();


                if (alarmId != 0) {
                    realm.executeTransactionAsync(realm1 -> {
                        Alarm alarm1 = realm1.where(Alarm.class).equalTo("id", alarmId).findFirst();
                        alarm1.setActivated(isChecked);
                        Log.wtf("Realm", "Update Success alarmId : " + alarm1.getId() + ", isActivated : " + isChecked);
                    });

//                    if (isChecked) {
//                        AlarmMgrUtil.setAlarm(Contextor.getContextInstance(), alarmId);
//                    } else {
//                        AlarmMgrUtil.cancelAlarm(Contextor.getContextInstance(), alarmId);
//                    }
                }

                realm.close();
            });
        }

        private void bind(Alarm item) {

            this.alarmId = item.getId();

            timeTv.setText(item.getTime());
            periodTv.setText(item.getPeriod());


            if (isInActionMode()) {

                if (ivIndicator.getVisibility() == View.GONE) {
                    Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
                    fadeIn.setInterpolator(new DecelerateInterpolator());
                    fadeIn.setDuration(400);

                    toggleBtn.setVisibility(View.GONE);
                    ivIndicator.setVisibility(View.VISIBLE);

                    ivIndicator.startAnimation(fadeIn);
                }

                ivIndicator.setImageResource(
                        isItemSelected() ? R.drawable.drawable_selection_indicator
                                : R.drawable.drawable_circle);

            } else {

                if (toggleBtn.getVisibility() == View.GONE) {
                    Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
                    fadeIn.setInterpolator(new DecelerateInterpolator());
                    fadeIn.setDuration(400);

                    toggleBtn.setVisibility(View.VISIBLE);
                    ivIndicator.setVisibility(View.GONE);

                    toggleBtn.startAnimation(fadeIn);
                }

                toggleBtn.setChecked(item.isActivated());

            }


        }
    }
}

