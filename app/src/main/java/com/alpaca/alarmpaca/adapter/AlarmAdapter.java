package com.alpaca.alarmpaca.adapter;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.alpaca.alarmpaca.R;
import com.alpaca.alarmpaca.model.Alarm;
import com.alpaca.alarmpaca.util.AlarmMgrUtil;
import com.alpaca.alarmpaca.util.RealmUtil;
import com.alpaca.alarmpaca.view.CircleCheckBox;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;

public class AlarmAdapter extends RealmRecyclerViewAdapter<Alarm, AlarmAdapter.MyViewHolder> {

    private boolean isOnDeleting;

    public AlarmAdapter(OrderedRealmCollection<Alarm> data) {
        super(data, true);
        this.isOnDeleting = false;
        setHasStableIds(true);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_alarm, parent, false);
        Log.wtf("AlarmAdapter", "onCreateViewHolder");

        return new MyViewHolder(parent.getContext(), itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Log.wtf("AlarmAdapter", "onBindViewHolder : " + position);
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

    public void setIsOnDeleting(boolean isOnDeleting) {
        this.isOnDeleting = isOnDeleting;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout checkBoxLayout;
        LinearLayout timeLayout;
        CircleCheckBox checkBox;

        TextView timeTv;
        TextView periodTv;
        TextView dateTv;
        Switch toggleBtn;

        Context context;
        View itemView;
        int alarmId;

        MyViewHolder(Context context, View itemView) {
            super(itemView);

            this.context = context;
            this.itemView = itemView;

            timeLayout = itemView.findViewById(R.id.timeLayout);
            checkBoxLayout = itemView.findViewById(R.id.checkBoxLayout);
            checkBox = itemView.findViewById(R.id.checkBox);

            timeTv = itemView.findViewById(R.id.timeTv);
            periodTv = itemView.findViewById(R.id.periodTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            toggleBtn = itemView.findViewById(R.id.toggleBtn);

        }

        void bind(Alarm alarm) {

            this.alarmId = alarm.getId();

            toggleBtn.setOnCheckedChangeListener(null);

            timeTv.setText(alarm.getTime());
            periodTv.setText(alarm.getPeriod());
//            dateTv.setText(data.getDate());
            toggleBtn.setChecked(alarm.isActivated());

            if (isOnDeleting) {

                int pixelFrom46dp = (int) (46 * Resources.getSystem().getDisplayMetrics().density);


                Animation timeLayoutAnim = new TranslateAnimation(
                        Animation.ABSOLUTE, -pixelFrom46dp,
                        Animation.RELATIVE_TO_SELF, 0.0f,
                        Animation.ABSOLUTE, 0.0f,
                        Animation.ABSOLUTE, 0.0f
                );
                timeLayoutAnim.setInterpolator(new DecelerateInterpolator());
                timeLayoutAnim.setDuration(400);

                Animation dateTvAnim = new TranslateAnimation(
                        Animation.ABSOLUTE, 0.0f,
                        Animation.ABSOLUTE, toggleBtn.getRight() - dateTv.getRight(),
                        Animation.ABSOLUTE, 0.0f,
                        Animation.ABSOLUTE, 0.0f
                );
//                dateTvAnim.setInterpolator(new DecelerateInterpolator());
                dateTvAnim.setDuration(400);

                Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
                fadeIn.setInterpolator(new DecelerateInterpolator());
                fadeIn.setDuration(400);

                Animation fadeOut = new AlphaAnimation(1.0f, 0.0f);
                fadeOut.setInterpolator(new DecelerateInterpolator());
                fadeOut.setDuration(400);
                fadeOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        dateTv.setVisibility(View.VISIBLE);
                        dateTv.startAnimation(fadeIn);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });

                Animation fadeInRightAnim = AnimationUtils.loadAnimation(context, R.anim.right_from_left);
                Animation fadeOutRightAnim = AnimationUtils.loadAnimation(context, R.anim.fade_out_right_from_left);
                fadeOutRightAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        toggleBtn.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });


                checkBoxLayout.setVisibility(View.VISIBLE);

                timeLayout.startAnimation(timeLayoutAnim);
//                dateTv.animate()
//                        .translationX(toggleBtn.getRight() - dateTv.getRight())
//                        .setInterpolator(new DecelerateInterpolator())
//                        .setDuration(400);
                dateTv.startAnimation(dateTvAnim);
//                dateTv.startAnimation(fadeOut);

                checkBoxLayout.startAnimation(fadeInRightAnim);
                toggleBtn.startAnimation(fadeOutRightAnim);

            }

            toggleBtn.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                Realm realm = RealmUtil.getRealmInstance();

                if (alarm != null) {
                    realm.executeTransactionAsync(realm1 -> {
                        Alarm alarm1 = realm1.where(Alarm.class).equalTo("id", alarmId).findFirst();
                        alarm1.setActivated(isChecked);
                        Log.wtf("Realm", "Update Success alarmId : " + alarm1.getId() + ", isActivated : " + isChecked);
                    });

                    if (isChecked) {
                        AlarmMgrUtil.setAlarm(context, alarm);
                    } else {
                        AlarmMgrUtil.cancelAlarm(context, alarm);
                    }
                }

                realm.close();
            });

        }
    }
}
