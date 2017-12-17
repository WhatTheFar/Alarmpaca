package com.alpaca.alarmpaca.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
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
import com.alpaca.alarmpaca.activity.LoginActivity;
import com.alpaca.alarmpaca.activity.MainActivity;
import com.alpaca.alarmpaca.activity.TaskActivity;
import com.alpaca.alarmpaca.adapter.AlarmBinder;
import com.alpaca.alarmpaca.adapter.TaskBinder;
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
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.tasks.TasksScopes;
import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.Tasks;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;


@SuppressWarnings("unused")
public class TaskFragment extends Fragment {

    private ActionMode actionMode;
    private SelectableAdapter adapter;
    private DataListManager<RealmTasks> selectableItemDataListManager;

    private RecyclerView recyclerView;

    private GoogleAccountCredential mCredential;
    private TaskManager taskManager;

    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;

    private static final String SHARED_PREF_ACCOUNT = "accountSharedPreference";
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String ARGS_ACCOUNT_NAME = "args_accountName";
    private static final String[] SCOPES = {TasksScopes.TASKS};


    public TaskFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static TaskFragment newInstance() {
        TaskFragment fragment = new TaskFragment();
        Bundle args = new Bundle();
//        args.putString(ARGS_ACCOUNT_NAME, accountName);
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
//        accountName = getArguments().getString(ARGS_ACCOUNT_NAME);
        String accountName = getActivity()
                .getSharedPreferences(SHARED_PREF_ACCOUNT, Context.MODE_PRIVATE)
                .getString(PREF_ACCOUNT_NAME, null);

        mCredential = GoogleAccountCredential.usingOAuth2(
                getContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        if (accountName != null) {
            mCredential.setSelectedAccountName(accountName);
            taskManager = new TaskManager(mCredential);
            taskManager.saveDataToRealm();

        }
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
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
//        adapter.registerBinder(new TaskBinder(itemDecorator,
//                (view, item) -> {
//                    toggleActionMode();
//                    return true;
//                }));

        adapter.registerBinder(new TaskBinder(itemDecorator,
                new TaskBinder.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, RealmTasks item) {

                    }

                    @Override
                    public void onItemLongClick(View view, RealmTasks item) {
                        toggleActionMode();
                    }

                    @Override
                    public void onItemCheckedChange(View view, RealmTasks item, boolean isChecked) {
                        String status = isChecked ? "completed" : "needsAction";
                        if (!Objects.equals(item.getStatus(), status)) {
                            Realm realm = RealmUtil.getRealmInstance();
                            realm.executeTransaction(realm1 -> {
                                RealmTasks task = realm1.where(RealmTasks.class).equalTo("id", item.getId()).findFirst();
                                task.setStatus(status);
                                Log.wtf("Realm", "Update Success id : " + task.getId() + ", status : " + task.getStatus());
                            });
                            realm.close();

                            TaskManager taskManager = new TaskManager(mCredential);
                            taskManager.updateDataToApi(item.getId());
                        }
                    }
                }));

        adapter.setSelectionMode(SelectableAdapter.SELECTION_MODE_MULTIPLE);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        recyclerView.addItemDecoration(adapter.getItemDecorationManager());
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);

        setDataToAdapter();
    }

    private void setDataToAdapter() {
        Log.wtf("TaskFragment", "setDataToAdapter");
        Realm realm = RealmUtil.getRealmInstance();
        RealmResults<RealmTasks> results = realm.where(RealmTasks.class).findAllSorted("position");
        selectableItemDataListManager.set(results);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_fragment_task, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_task_clear:
                Log.wtf("TaskFragment", "Menu : Clear Task clicked");
                TaskManager taskManager = new TaskManager(mCredential);
                taskManager.clearCompleteTaskFromDataAndApi();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.wtf("TaskFragment", "onResume");
//        setDataToAdapter();
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
    private class TaskManager extends AsyncTask<String, Void, String> {

        private com.google.api.services.tasks.Tasks mService = null;
        private Exception mLastError = null;

        private static final String ACTION_SAVE_DATA = "action_save_data";
        private static final String ACTION_DELETE_DATA = "action_delete_data";
        private static final String ACTION_UPDATE_DATA = "action_update_data";
        private static final String ACTION_INSERT_DATA = "action_insert_data";
        private static final String ACTION_CLEAR_DATA = "action_clear_data";

        TaskManager(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.tasks.Tasks.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Alarmpaca")
                    .build();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                if (params.length != 0) {
                    Realm realm = RealmUtil.getRealmInstance();
                    String id;
                    RealmTasks realmTask;
                    switch (params[0]) {
                        case ACTION_SAVE_DATA:
                            Log.wtf("TaskManager", "doInBackground : ACTION_SAVE_DATA");

                            //Loading Tasks From Google Api
                            Tasks result = mService.tasks().list("@default").execute();
                            List<Task> tasks = result.getItems();

                            for (Task task : tasks) {

                                RealmTasks realmTasks = new RealmTasks();
                                realmTasks.setId(task.getId());
                                realmTasks.setTitle(task.getTitle());
                                realmTasks.setNotes(task.getNotes() == null ? "" : task.getNotes());
                                realmTasks.setStatus(task.getStatus());
                                realmTasks.setPosition(task.getPosition());

                                Log.d("TaskManager", "Title : " + task.getTitle());

                                if (task.getDue() != null) {
                                    Date due = new Date(task.getDue().getValue());
                                    realmTasks.setDue(due);
                                    Log.d("TaskManager", "Date : " + DateFormat.getDateInstance().format(due));
                                }

                                realm.executeTransaction(realm1 -> realm1.copyToRealmOrUpdate(realmTasks));
                            }
                            realm.close();
                            return ACTION_SAVE_DATA;

                        case ACTION_DELETE_DATA:
                            id = params[1];
                            mService.tasks().delete("@default", id).execute();
                            realm.close();
                            return ACTION_DELETE_DATA;

                        case ACTION_UPDATE_DATA:
                            Log.wtf("TaskManager", "doInBackground : ACTION_UPDATE_DATA");
                            id = params[1];
                            // First retrieve the task to update.
                            Task taskToUpdate = mService.tasks().get("@default", id).execute();

                            realmTask = realm.where(RealmTasks.class)
                                    .equalTo("id", id)
                                    .findFirst();

//                            taskToUpdate.setId(realmTask.getId());
//                            taskToUpdate.setTitle(realmTask.getTitle());
//                            if (realmTask.getNotes() != null) {
//                                taskToUpdate.setNotes(realmTask.getNotes());
//                            }
//                            taskToUpdate.setTitle("hello");
                            if (Objects.equals(realmTask.getStatus(), "needsAction")) {
                                taskToUpdate.setCompleted(null);
                                taskToUpdate.setStatus("needsAction");
                            } else {
                                taskToUpdate.setStatus("completed");
                            }
                            Log.wtf("TaskManager", "doInBackground : " + taskToUpdate.getId());
                            Log.wtf("TaskManager", "doInBackground : " + id);
                            Log.wtf("TaskManager", "doInBackground : " + taskToUpdate.toPrettyString());

                            Task updatedTask = mService.tasks().update("@default", taskToUpdate.getId(), taskToUpdate).execute();
                            realm.close();
                            Log.wtf("TaskManager", "doInBackground : " + updatedTask.toPrettyString());
                            return ACTION_UPDATE_DATA;

                        case ACTION_INSERT_DATA:
                            id = params[1];
                            // Retrieve data to insert.
                            realmTask = realm.where(RealmTasks.class)
                                    .equalTo("id", id)
                                    .findFirst();
                            // Insert data to temporary task
                            Task taskToInsert = new Task();
                            taskToInsert.setTitle(realmTask.getTitle());
                            if (realmTask.getNotes() != null) {
                                taskToInsert.setNotes(realmTask.getNotes());
                            }
                            taskToInsert.setStatus(realmTask.getStatus());
                            taskToInsert.setDeleted(realmTask.isDeleted());

                            realm.beginTransaction();
                            realmTask.deleteFromRealm();
                            realm.commitTransaction();

                            mService.tasks().insert("@default", taskToInsert).execute();
                            realm.close();
                            return ACTION_INSERT_DATA;

                        case ACTION_CLEAR_DATA:
                            //Clear from Task Api
                            mService.tasks().clear("@default").execute();

                            //Clear from Realm
                            RealmResults<RealmTasks> realmResults = realm.where(RealmTasks.class)
                                    .equalTo("status", "completed")
                                    .findAll();

                            realm.beginTransaction();
                            realmResults.deleteAllFromRealm();
                            realm.commitTransaction();
                            realm.close();
                            return ACTION_CLEAR_DATA;

                        default:
                            break;
                    }
                    realm.close();
                }
                return null;
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        void saveDataToRealm() {
            Log.wtf("TaskManager", "saveDataToRealm");
            if (taskManager.getStatus() != Status.RUNNING) {
                execute(ACTION_SAVE_DATA);
            }
        }

        void deleteDataFromApi(String id) {
            execute(ACTION_DELETE_DATA, id);
        }

        void updateDataToApi(String id) {
            execute(ACTION_UPDATE_DATA, id);
        }

        void clearCompleteTaskFromDataAndApi() {
            execute(ACTION_CLEAR_DATA);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                switch (s) {
                    case ACTION_SAVE_DATA:
                        setDataToAdapter();
                        break;
                    case ACTION_UPDATE_DATA:
                        Log.wtf("TaskManager", "onPostExecute : ACTION_UPDATE_DATA");
                }
            }
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
