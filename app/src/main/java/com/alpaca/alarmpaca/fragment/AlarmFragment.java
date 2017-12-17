package com.alpaca.alarmpaca.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.ahamed.multiviewadapter.DataListManager;
import com.ahamed.multiviewadapter.SelectableAdapter;
import com.ahamed.multiviewadapter.util.ItemDecorator;
import com.ahamed.multiviewadapter.util.SimpleDividerDecoration;
import com.alpaca.alarmpaca.R;
import com.alpaca.alarmpaca.activity.AlarmDetailActivity;
import com.alpaca.alarmpaca.activity.MainActivity;
import com.alpaca.alarmpaca.adapter.AlarmBinder;
import com.alpaca.alarmpaca.adapter.BlankBinder;
import com.alpaca.alarmpaca.model.Alarm;
import com.alpaca.alarmpaca.util.RealmUtil;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;


@SuppressWarnings("unused")
public class AlarmFragment extends Fragment {

    private ActionMode actionMode;
    private SelectableAdapter adapter;

    private RecyclerView recyclerView;

    private DataListManager<Alarm> selectableItemDataListManager;

    public static final int REQUEST_ALARM_DETAIL = 1001;

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

        Log.wtf("AlarmFragment", "initInstances");
        recyclerView = rootView.findViewById(R.id.recyclerView);

        setUpAdapter();

        setHasOptionsMenu(true);

    }

    @SuppressLint("RestrictedApi")
    private void setUpAdapter() {
        ItemDecorator itemDecorator =
                new SimpleDividerDecoration(getContext(), SimpleDividerDecoration.VERTICAL);

        adapter = new SelectableAdapter();
        selectableItemDataListManager = new DataListManager<>(adapter);
        selectableItemDataListManager.setMultiSelectionChangedListener(
                selectedItems -> {
                    if (selectedItems.size() == 0) {
                        toggleActionMode();
                    } else if (actionMode != null) {
                        actionMode.setTitle(selectedItems.size() + " selected items");
                    }
                });

        adapter.addDataManager(selectableItemDataListManager);
        adapter.registerBinder(new AlarmBinder(itemDecorator,
                new AlarmBinder.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, Alarm item) {
                        Log.wtf("AlarmFragment", "onItem click");
                        Intent intent = new Intent(getContext(), AlarmDetailActivity.class);
                        intent.putExtra(AlarmDetailActivity.ALARM_ID_EXTRA, item.getId());
                        startActivityForResult(intent, REQUEST_ALARM_DETAIL);
                    }

                    @Override
                    public void onItemLongClick(View view, Alarm item) {
                        toggleActionMode();
                    }
                }));

        DataListManager<BlankBinder.BlankItem> blankItemDataListManager = new DataListManager<>(adapter);
        adapter.addDataManager(blankItemDataListManager);
        adapter.registerBinder(new BlankBinder());

        blankItemDataListManager.set(BlankBinder.BlankItem.getOneBlankItemList());

        adapter.setSelectionMode(SelectableAdapter.SELECTION_MODE_MULTIPLE);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        recyclerView.addItemDecoration(adapter.getItemDecorationManager());
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);

        setDataToAdapter();

    }

    private void setDataToAdapter() {
        selectableItemDataListManager.clear();
        Realm realm = RealmUtil.getRealmInstance();
        RealmResults<Alarm> results = realm.where(Alarm.class).findAllSorted("id");
        selectableItemDataListManager.set(results);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ALARM_DETAIL:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    int id = data.getIntExtra(AlarmDetailActivity.ALARM_ID_EXTRA, -1);
//                    Log.wtf("AlarmFragment", "onActivityResult : REQUEST_ALARM_DETAIL " + id);
                    Realm realm = RealmUtil.getRealmInstance();
                    realm.beginTransaction();
                    realm.where(Alarm.class).equalTo("id", id).findFirst().deleteFromRealm();
                    realm.commitTransaction();
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        Log.wtf("AlarmFragment", "onCreateOptionMenu");
        menu.clear();
        inflater.inflate(R.menu.menu_fragment_alarm, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_alarm_delete:
                Log.wtf("AlarmFragment", "Menu : Delete clicked");
                onDeleteMenuClicked();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.wtf("AlarmFragment", "onResume");
        setDataToAdapter();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.wtf("AlarmFragment", "onStop");
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

    private void onDeleteMenuClicked() {
        toggleActionMode();
        if (actionMode != null) {
            actionMode.setTitle("0 selected item");
        }
    }

    @SuppressLint("RestrictedApi")
    private void toggleActionMode() {
        if (null == actionMode) {
            MainActivity activity = (MainActivity) getActivity();
            adapter.startActionMode();
            actionMode = activity.startSupportActionMode(actionModeCallback);
        } else {
            actionMode.finish();
            actionMode = null;
            adapter.stopActionMode();
            selectableItemDataListManager.clearSelectedItems();
        }

        adapter.notifyDataSetChanged();
    }

    ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.setTitle("1 selected item");
            mode.getMenuInflater().inflate(R.menu.menu_action_mode_alarm, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        @SuppressLint("RestrictedApi")
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    Log.wtf("AlarmActionMode", "Delete menu clicked");
                    List<Alarm> selectedAlarm = selectableItemDataListManager.getSelectedItems();
                    Realm realm = RealmUtil.getRealmInstance();
                    realm.beginTransaction();
                    for (Alarm alarm : selectedAlarm
                            ) {
                        alarm.deleteFromRealm();
                    }
                    realm.commitTransaction();
                    realm.close();
                    toggleActionMode();
                    setDataToAdapter();
                    break;
            }
            return false;
        }

        @SuppressLint("RestrictedApi")
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            adapter.stopActionMode();
            selectableItemDataListManager.clearSelectedItems();
            AlarmFragment.this.actionMode = null;
        }
    };

}
