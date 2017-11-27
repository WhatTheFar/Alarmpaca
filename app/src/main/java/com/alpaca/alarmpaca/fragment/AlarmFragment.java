package com.alpaca.alarmpaca.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alpaca.alarmpaca.R;
import com.alpaca.alarmpaca.adapter.AlarmAdapter;
import com.alpaca.alarmpaca.model.Alarm;
import com.alpaca.alarmpaca.util.Contextor;
import com.alpaca.alarmpaca.util.RealmUtil;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;


@SuppressWarnings("unused")
public class AlarmFragment extends Fragment {

    public AlarmFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static AlarmFragment newInstance() {
        AlarmFragment fragment = new AlarmFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_alarm, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here

        RealmConfiguration realmConfig = (new RealmConfiguration.Builder())
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm realm = Realm.getInstance(realmConfig);
        RealmResults<Alarm> results = realm.where(Alarm.class).findAllSorted("id");

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setAdapter(new AlarmAdapter(results));
        recyclerView.setLayoutManager(new LinearLayoutManager(
                Contextor.getInstance().getContext(),
                LinearLayoutManager.VERTICAL,
                false));

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /*
     * Save Instance State Here
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Instance State here
    }

    /*
     * Restore Instance State Here
     */
    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance State here
    }

}
