package com.alpaca.alarmpaca.adapter;

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

    private OnItemClickListener listener;

    public AlarmBinder(ItemDecorator itemDecorator,
                       OnItemClickListener listener) {
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

    public interface OnItemClickListener {
        void onItemClick(View view, Alarm item);

        void onItemLongClick(View view, Alarm item);
    }

    @SuppressWarnings("deprecation")
    static class ViewHolder extends BaseViewHolder<Alarm> {

        private TextView timeTv;
        private TextView periodTv;
        private TextView dateTv;
        private SwitchCompat switchCompat;
        private ImageView ivIndicator;

        ViewHolder(View itemView,
                   final AlarmBinder.OnItemClickListener listener) {
            super(itemView);

            timeTv = itemView.findViewById(R.id.timeTv);
            periodTv = itemView.findViewById(R.id.periodTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            switchCompat = itemView.findViewById(R.id.toggleBtn);
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
                } else {
                    listener.onItemClick(view, item);
                }
            });

            switchCompat.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                Realm realm = RealmUtil.getRealmInstance();

                if (isChecked != getItem().isActivated()) {
                    int id = getItem().getId();
                    realm.executeTransactionAsync(realm1 -> {
                        Alarm alarm1 = realm1.where(Alarm.class).equalTo("id", id).findFirst();
                        alarm1.setActivated(isChecked);
                        Log.wtf("Realm", "Update Success alarmId : " + id + ", isActivated : " + isChecked);
                    });

                }

                    if (isChecked) {
                        AlarmMgrUtil.setAlarm(Contextor.getContextInstance(), getItem());
                    } else {
                        AlarmMgrUtil.cancelAlarm(Contextor.getContextInstance(), getItem());
                    }


                realm.close();
            });
        }

        private void bind(Alarm item) {

            timeTv.setText(item.getTime());
            periodTv.setText(item.getPeriod());
            dateTv.setText(item.getDateString());

            if (isInActionMode()) {

                if (ivIndicator.getVisibility() == View.GONE) {
                    Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
                    fadeIn.setInterpolator(new DecelerateInterpolator());
                    fadeIn.setDuration(400);

                    switchCompat.setVisibility(View.GONE);
                    ivIndicator.setVisibility(View.VISIBLE);

                    ivIndicator.startAnimation(fadeIn);
                }

                ivIndicator.setImageResource(
                        isItemSelected() ? R.drawable.drawable_selection_indicator
                                : R.drawable.drawable_circle);

            } else {

                if (switchCompat.getVisibility() == View.GONE) {
                    Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
                    fadeIn.setInterpolator(new DecelerateInterpolator());
                    fadeIn.setDuration(400);

                    switchCompat.setVisibility(View.VISIBLE);
                    ivIndicator.setVisibility(View.GONE);

                    switchCompat.startAnimation(fadeIn);
                }

                switchCompat.setChecked(item.isActivated());

            }


        }
    }
}

