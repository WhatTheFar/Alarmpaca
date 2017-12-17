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
import com.alpaca.alarmpaca.model.RealmTasks;
import com.alpaca.alarmpaca.util.RealmUtil;
import com.alpaca.alarmpaca.view.CircleCheckBox;

import java.text.DateFormat;
import java.util.Objects;

import io.realm.Realm;


public class TaskBinder extends ItemBinder<RealmTasks, TaskBinder.ViewHolder> {

    private OnItemClickListener listener;

    public TaskBinder(ItemDecorator itemDecorator,
                      OnItemClickListener listener) {
        super(itemDecorator);
        this.listener = listener;
    }

    @Override
    public ViewHolder create(LayoutInflater layoutInflater, ViewGroup parent) {
        return new ViewHolder(layoutInflater.inflate(R.layout.item_task, parent, false),
                listener);
    }

    @Override
    public void bind(ViewHolder holder, RealmTasks item) {
        Log.wtf("TaskBinder", "ViewHolder : Bind");
        holder.bind(item);
    }

    @Override
    public boolean canBindData(Object item) {
        return item instanceof RealmTasks;
    }

    @Override
    public int getSpanSize(int maxSpanCount) {
        return maxSpanCount;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, RealmTasks item);
        void onItemLongClick(View view, RealmTasks item);
        void onItemCheckedChange(View view, RealmTasks item, boolean isChecked);
    }

    static class ViewHolder extends BaseViewHolder<RealmTasks> {

        private TextView titleTv;
        private TextView detailTv;
        private TextView dueDateTv;
        private CircleCheckBox checkBox;
//        private String id;

        ViewHolder(View itemView,
                   final TaskBinder.OnItemClickListener listener) {
            super(itemView);

            titleTv = itemView.findViewById(R.id.taskTitle);
            detailTv = itemView.findViewById(R.id.shorten_taskDetail);
            dueDateTv = itemView.findViewById(R.id.task_date);
            checkBox = itemView.findViewById(R.id.checkbox_taskTitle);

            //ivIndicator = itemView.findViewById(R.id.iv_selection_indicator);

            setItemLongClickListener((view, item) -> {
                 toggleItemSelection();
                if (!isInActionMode()) {
                    listener.onItemLongClick(view, item);
                } else {
                    listener.onItemClick(view, item);
                }
                return true;
            });

            setItemClickListener((view, item) -> {
                if (isInActionMode()) {
                    toggleItemSelection();
                }
            });

            checkBox.setOnCheckedChangeListener((compoundButton, isChecked) -> {

                listener.onItemCheckedChange(compoundButton, getItem(), isChecked);

//                Realm realm = RealmUtil.getRealmInstance();

//                if (id != null && isChecked) {
//                    realm.executeTransactionAsync(realm1 -> {
//                        RealmTasks task = realm1.where(RealmTasks.class).equalTo("id", id).findFirst();
//                        task.setStatus("completed");
//                        Log.wtf("Realm", "Update Success id : " + task.getId() + ", status : " + task.getStatus());
//                    });
//                } else {
//                    realm.executeTransaction(realm1 -> {
//                        RealmTasks task = realm1.where(RealmTasks.class).equalTo("id", id).findFirst();
//                        task.setStatus("needsAction");
//                        Log.wtf("Realm", "Update Success id : " + task.getId() + ", status : " + task.getStatus());
//                    });
//                }

//                realm.close();

            });
        }

        private void bind(RealmTasks item) {

//            this.id = item.getId();

            titleTv.setText(item.getTitle());
            detailTv.setText(item.getNotes() == null ? "" : item.getNotes());
            dueDateTv.setText(item.getDue() == null ? "" : DateFormat.getDateInstance().format(item.getDue()));

            Log.wtf("TaskBinder", "item : " + item.getTitle() + " " + item.getStatus());
            if (Objects.equals(item.getStatus(), "completed")) {
                checkBox.setChecked(true);
            }

        }
    }
}

