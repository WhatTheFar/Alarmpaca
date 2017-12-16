package com.alpaca.alarmpaca.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.ahamed.multiviewadapter.DataListManager;
import com.ahamed.multiviewadapter.SelectableAdapter;
import com.ahamed.multiviewadapter.util.ItemDecorator;
import com.ahamed.multiviewadapter.util.SimpleDividerDecoration;
import com.alpaca.alarmpaca.R;
import com.alpaca.alarmpaca.activity.MainActivity;
import com.alpaca.alarmpaca.activity.TaskActivity;
import com.alpaca.alarmpaca.adapter.AlarmBinder;
import com.alpaca.alarmpaca.model.Alarm;
import com.alpaca.alarmpaca.model.RealmTasks;
import com.alpaca.alarmpaca.util.RealmUtil;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.Tasks;

import java.io.IOException;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;


@SuppressWarnings("unused")
public class TaskFragment extends Fragment {

    private ActionMode actionMode;
    private SelectableAdapter adapter;
    private DataListManager<RealmTasks> selectableItemDataListManager;

    private RecyclerView recyclerView;

    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;


    public TaskFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static TaskFragment newInstance() {
        TaskFragment fragment = new TaskFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        recyclerView = rootView.findViewById(R.id.recyclerView);

        setUpAdapter();

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
                (view, item) -> {
                    toggleActionMode();
                    return true;
                }));

        adapter.setSelectionMode(SelectableAdapter.SELECTION_MODE_MULTIPLE);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        recyclerView.addItemDecoration(adapter.getItemDecorationManager());
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);

        Realm realm = RealmUtil.getRealmInstance();
        RealmResults<RealmTasks> results = realm.where(RealmTasks.class).findAllSorted("id");

        selectableItemDataListManager.set(results);
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

    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                getActivity(),
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.setTitle("1 selected item");
            mode.getMenuInflater().inflate(R.menu.menu_action_mode, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }

        @SuppressLint("RestrictedApi")
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            adapter.stopActionMode();
            selectableItemDataListManager.clearSelectedItems();
            TaskFragment.this.actionMode = null;
        }
    };


    @SuppressLint("StaticFieldLeak")
    private class TaskManager extends AsyncTask<Integer, Void, Void> {

        private com.google.api.services.tasks.Tasks mService = null;
        private Exception mLastError = null;
        private String action;

        private static final int ACTION_SAVE_DATA = 1001;
        private static final int ACTION_DELETE_DATA = 1002;


        TaskManager(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.tasks.Tasks.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Alarmpaca")
                    .build();
        }

        @Override
        protected Void doInBackground(Integer... params) {
            try {
                if (params.length != 0) {
                    switch (params[0]) {
                        case ACTION_SAVE_DATA:
                            Tasks result = mService.tasks().list("@default").execute();
                            List<Task> tasks = result.getItems();

                            //TODO save to realm
                            for (Task task : tasks
                                    ) {

                            }


                    }
                }
                return null;
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        public void saveDataToRealm() {
            execute(ACTION_SAVE_DATA);
        }

        public void deleteDataFromApi() {
            execute(ACTION_DELETE_DATA);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled() {
//            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            TaskFragment.REQUEST_AUTHORIZATION);
                } else {
//                    mOutputText.setText("The following error occurred:\n"
//                            + mLastError.getMessage());
                    mLastError.printStackTrace();
                }
            } else {
//                mOutputText.setText("Request cancelled.");
            }
        }
    }

}
