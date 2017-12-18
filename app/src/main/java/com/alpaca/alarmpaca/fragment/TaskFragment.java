package com.alpaca.alarmpaca.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.ahamed.multiviewadapter.DataListManager;
import com.ahamed.multiviewadapter.SelectableAdapter;
import com.ahamed.multiviewadapter.util.ItemDecorator;
import com.ahamed.multiviewadapter.util.SimpleDividerDecoration;
import com.alpaca.alarmpaca.R;
import com.alpaca.alarmpaca.activity.MainActivity;
import com.alpaca.alarmpaca.adapter.BlankBinder;
import com.alpaca.alarmpaca.adapter.TaskBinder;
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
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.tasks.TasksScopes;
import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.Tasks;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmResults;


@SuppressWarnings("unused")
public class TaskFragment extends Fragment {

    private ActionMode actionMode;
    private SelectableAdapter adapter;
    private DataListManager<RealmTasks> selectableItemDataListManager;

    private RecyclerView recyclerView;

    private GoogleAccountCredential mCredential;
    private Realm realmUI;

    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_TASK_DETAIL = 1101;

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
        String accountName = getActivity()
                .getSharedPreferences(SHARED_PREF_ACCOUNT, Context.MODE_PRIVATE)
                .getString(PREF_ACCOUNT_NAME, null);

        mCredential = GoogleAccountCredential.usingOAuth2(
                getContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        realmUI = RealmUtil.getRealmInstance();

        if (accountName != null) {
            mCredential.setSelectedAccountName(accountName);
            TaskManager taskManager = new TaskManager(mCredential);
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

        DataListManager<BlankBinder.BlankItem> blankItemDataListManager = new DataListManager<>(adapter);
        adapter.addDataManager(blankItemDataListManager);
        adapter.registerBinder(new BlankBinder());

        blankItemDataListManager.set(BlankBinder.BlankItem.getOneBlankItemList());

        adapter.setSelectionMode(SelectableAdapter.SELECTION_MODE_MULTIPLE);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        recyclerView.addItemDecoration(adapter.getItemDecorationManager());
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);

        RealmResults<RealmTasks> results = realmUI.where(RealmTasks.class).findAllSorted("position");
        selectableItemDataListManager.set(results);
    }

    private void setDataToAdapter() {
        Log.wtf("TaskFragment", "setDataToAdapter");
        selectableItemDataListManager.clear();
//        selectableItemDataListManager = new DataListManager<>(adapter);
//        adapter.addDataManager(0, selectableItemDataListManager);
        RealmResults<RealmTasks> results = realmUI.where(RealmTasks.class).findAllSorted("position");
        selectableItemDataListManager.set(results);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_TASK_DETAIL:
                if (resultCode == Activity.RESULT_OK) {
                    RealmTasks realmTasks = data.getParcelableExtra("task");
                    TaskManager taskManager = new TaskManager(mCredential);
//                    taskManager.insertDataToApi(RealmTasks.NEW_TASK_ID);
                    taskManager.insertDataToApi(realmTasks);
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == Activity.RESULT_OK) {
                    Log.wtf("TaskFragment", "onActivityResult : REQUEST_AUTHORIZATION");
                    TaskManager taskManager = new TaskManager(mCredential);
                    taskManager.saveDataToRealm();
                }
                break;
        }
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        realmUI.close();
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
            mode.getMenuInflater().inflate(R.menu.menu_action_mode_task, menu);
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
                case R.id.action_complete:
                    Log.wtf("TaskActionMode", "Complete menu clicked");
                    List<RealmTasks> selectedTask = selectableItemDataListManager.getSelectedItems();
                    for (RealmTasks task : selectedTask
                            ) {
//                        Log.wtf("TaskActionMode", task.getTitle());
                        Realm realm = RealmUtil.getRealmInstance();
                        realm.beginTransaction();
                        task.setStatus("completed");
                        realm.commitTransaction();
                        realm.close();

                        TaskManager taskManager = new TaskManager(mCredential);
                        taskManager.updateDataToApi(task.getId());
                    }
                    toggleActionMode();
                    break;
            }
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
    private class TaskManager extends AsyncTask<String, String, String> {

        private com.google.api.services.tasks.Tasks mService = null;
        private Exception mLastError = null;
        private RealmTasks realmTaskToInsert;

        private static final String ACTION_SAVE_DATA = "action_save_data";
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
                    String id;
                    Realm realmThread = RealmUtil.getRealmInstance();
                    RealmTasks realmTask;
                    RealmResults<RealmTasks> realmResults;
                    switch (params[0]) {
                        case ACTION_SAVE_DATA:
//                            Log.wtf("TaskManager", "doInBackground : ACTION_SAVE_DATA");

                            //Loading Tasks From Google Api
                            Tasks result = mService.tasks().list("@default").execute();
                            List<Task> tasks = result.getItems();

                            realmResults = realmThread.where(RealmTasks.class).findAll();
                            realmThread.executeTransaction(realm -> realmResults.deleteAllFromRealm());

                            for (Task task : tasks) {
                                saveTaskModelToRealmTask(task);
                            }
                            realmThread.close();
                            return ACTION_SAVE_DATA;

                        case ACTION_UPDATE_DATA:
                            Log.wtf("TaskManager", "doInBackground : ACTION_UPDATE_DATA");
                            id = params[1];
                            // First retrieve the task to update.
                            Task taskToUpdate = mService.tasks().get("@default", id).execute();

                            realmTask = realmThread.where(RealmTasks.class)
                                    .equalTo("id", id)
                                    .findFirst();

                            taskToUpdate.setTitle(realmTask.getTitle());
                            if (realmTask.getNotes() != null) {
                                taskToUpdate.setNotes(realmTask.getNotes());
                            }
                            if (Objects.equals(realmTask.getStatus(), "needsAction")) {
                                taskToUpdate.setCompleted(null);
                                taskToUpdate.setStatus("needsAction");
                            } else {
                                taskToUpdate.setStatus("completed");
                            }

                            Task updatedTask = mService.tasks().update("@default", taskToUpdate.getId(), taskToUpdate).execute();
                            Log.wtf("TaskManager", "doInBackground : updatedTask " + updatedTask.toPrettyString());
                            realmThread.close();
                            return ACTION_UPDATE_DATA;

                        case ACTION_INSERT_DATA:
                            // Retrieve data to insert.
                            realmTask = realmTaskToInsert;
                            // Insert data to temporary task
                            Task taskToInsert = new Task();
                            taskToInsert.setTitle(realmTask.getTitle());
                            if (realmTask.getNotes() != null) {
                                taskToInsert.setNotes(realmTask.getNotes());
                            }
                            taskToInsert.setStatus("needsAction");
                            if (realmTask.getDue() != null) {
                                taskToInsert.setDue(new DateTime(realmTask.getDue().getTime()));
                            }

                            Task insertedTask = mService.tasks().insert("@default", taskToInsert).execute();

                            realmTask = saveTaskModelToRealmTask(insertedTask);

//                            publishProgress(ACTION_INSERT_DATA, realmTask.getId());
                            Log.wtf("TaskManager", "doInBackground : insertedTask " + insertedTask.toPrettyString());
                            Log.wtf("TaskManager", "doInBackground : realmTask " + realmTask.getId());
                            realmThread.close();
                            return ACTION_INSERT_DATA;

                        case ACTION_CLEAR_DATA:

                            //Clear from Realm

                            publishProgress(ACTION_CLEAR_DATA);

                            //Clear from Task Api
                            mService.tasks().clear("@default").execute();
                            realmThread.close();
                            return ACTION_CLEAR_DATA;

                        default:
                            break;
                    }
                }
                return null;
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        private RealmTasks saveTaskModelToRealmTask(Task task) {
            Realm realm = RealmUtil.getRealmInstance();
            RealmTasks realmTasks = new RealmTasks();
            realmTasks.setId(task.getId());
            realmTasks.setTitle(task.getTitle());
            realmTasks.setNotes(task.getNotes() == null ? "" : task.getNotes());
            realmTasks.setStatus(task.getStatus());
            realmTasks.setPosition(task.getPosition());
            if (task.getDue() != null) {
                Date due = new Date(task.getDue().getValue());
                realmTasks.setDue(due);
            }

            realm.executeTransaction(realm1 -> realm.copyToRealmOrUpdate(realmTasks));
            realm.close();
            return realmTasks;
        }

        void saveDataToRealm() {
            execute(ACTION_SAVE_DATA);
        }

        void updateDataToApi(String id) {
            executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, ACTION_UPDATE_DATA, id);
        }

        void insertDataToApi(RealmTasks realmTasks) {
            realmTaskToInsert = realmTasks;
            execute(ACTION_INSERT_DATA);
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
                RealmResults<RealmTasks> realmResults;
                switch (s) {
                    case ACTION_SAVE_DATA:
                        setDataToAdapter();
                        break;
                    case ACTION_UPDATE_DATA:
                        break;
                    case ACTION_INSERT_DATA:
                        RealmResults<RealmTasks> results = realmUI.where(RealmTasks.class).findAllSorted("position");
                        selectableItemDataListManager.set(results);
                        recyclerView.scrollToPosition(0);
                        break;
                    case ACTION_CLEAR_DATA:
                        break;
                }
            }
        }

        @SuppressLint("RestrictedApi")
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            if (values.length != 0) {
                RealmResults<RealmTasks> realmResults;
                RealmTasks realmTasks;
                switch (values[0]) {
                    case ACTION_INSERT_DATA:
//                        realmTasks = realmUI.where(RealmTasks.class).equalTo("id", values[1]).findFirst();
//                        selectableItemDataListManager.add(realmTasks);
//                        recyclerView.scrollToPosition(0);
                        break;
                    case ACTION_CLEAR_DATA:
                        realmResults = realmUI.where(RealmTasks.class)
                                .equalTo("status", "completed")
                                .findAll();

                        for (RealmTasks realmTask : realmResults
                                ) {
                            selectableItemDataListManager.remove(realmTask);
                        }
                        realmUI.executeTransaction(realm1 -> realmResults.deleteAllFromRealm());
                        break;
                }
            }
        }

        @Override
        protected void onCancelled() {
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
                    Toast.makeText(getContext(), "The following error occurred:\n"
                            + mLastError.getMessage(), Toast.LENGTH_SHORT).show();
                    mLastError.printStackTrace();
                }
            } else {
                Toast.makeText(getContext(), "Request cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
