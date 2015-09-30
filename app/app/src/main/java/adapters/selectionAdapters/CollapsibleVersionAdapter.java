package adapters.selectionAdapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;
import android.widget.Toast;

import org.unfoldingword.mobile.R;

import model.DaoDBHelper;
import model.DownloadState;
import model.daoModels.Language;
import model.daoModels.LanguageLocale;
import model.daoModels.Project;
import model.daoModels.Version;
import services.UWMediaDownloaderService;
import services.UWUpdaterService;
import services.UWVersionDownloaderService;
import utils.NetWorkUtil;
import utils.UWPreferenceDataManager;
import view.AnimatedExpandableListView;
import view.VersionRowViewHolder;


/**
 * Created by PJ Fechner
 * Adapter for Versions
 */
public class CollapsibleVersionAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter implements VersionRowViewHolder.VersionRowViewHolderListener {

//    private final static String TAG = "CollapseVersionAdapter";

    private Fragment parentFragment;

    private Project currentProject = null;
    private VersionAdapterListener listener;
    private long selectedVersionId;

    private BroadcastReceiver receiver;

    //region setup

    public CollapsibleVersionAdapter(Fragment fragment, Project currentProject, long selectedVersionId, VersionAdapterListener listener) {
        this.parentFragment = fragment;
        this.currentProject = currentProject;
        this.selectedVersionId = selectedVersionId;
        this.listener = listener;
        setupIntentFilter();
    }

    private void setupIntentFilter(){
        receiver = createBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(UWUpdaterService.BROAD_CAST_DOWN_COMP);
        getContext().registerReceiver(receiver, filter);
    }

    private BroadcastReceiver createBroadcastReceiver() {

        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Toast.makeText(context, "Download Complete", Toast.LENGTH_SHORT).show();
                reload();
            }
        };
    }

    //endregion

    //region adapter methods

    @Override
    public Version getChild(int groupPosition, int childPosition) {
        return currentProject.getLanguages().get(groupPosition).getVersions().get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getRealChildrenCount(int groupPosition) {
        return getGroup(groupPosition).getVersions().size();
    }

    @Override
    public View getRealChildView(final int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {

        final Version version = getChild(groupPosition, childPosition);
        final VersionRowViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_version_selector, parent, false);

            holder = new VersionRowViewHolder(getContext(), version, convertView, this);
            convertView.setTag(holder);
        } else {
            holder = (VersionRowViewHolder) convertView.getTag();
        }

        // version-dependent settings
        boolean isSelected = (version.getId() == selectedVersionId);
        holder.setupViewForVersion(version, isSelected);
        return convertView;
    }

    //endregion

    //region helpers

    private Context getContext(){
        return this.parentFragment.getActivity();
    }

    /**
     * Do anything that needs to happen when the adapter will be destroyed
     */
    public void willDestroy(){
        if(receiver != null) {
            getContext().unregisterReceiver(receiver);
        }
        receiver = null;
    }

    //endregion

    //region reloading

    private void reload(){

        currentProject = Project.getProjectForId(currentProject.getId(), DaoDBHelper.getDaoSession(getContext()));
        notifyDataSetChanged();
    }

    //endregion

    //region VersionRowViewHolderListener

    @Override
    public void deletePressed(Version version) {
        deleteVersion(version);
    }

    @Override
    public void downloadWasPressed(VersionRowViewHolder holder, Version version) {

        if(version.getSaveState() == DownloadState.DOWNLOAD_STATE_DOWNLOADING.ordinal()) {
            stopDownload(version);
        }
        else{
            downloadRow(version);
        }
    }

    @Override
    public void audioButtonWasClicked(VersionRowViewHolder holder, Version version) {

        downloadMedia(holder, version, false);
    }

    @Override
    public void videoButtonWasClicked(VersionRowViewHolder holder, Version version) {

        downloadMedia(holder, version, true);
    }

    @Override
    public void versionWasSelected(Version version) {

        listener.versionWasSelected(version);
    }

    //endregion

    //region row actions

    private void downloadRow(final Version version){

        if (!NetWorkUtil.isConnected(getContext())) {
            new AlertDialog.Builder(getContext())
                .setTitle("Alert")
                .setMessage("Failed connecting to the internet.")
                .setPositiveButton("OK", null)
                .create().show();
        } else {
            setupIntentFilter();
            version.setSaveState(DownloadState.DOWNLOAD_STATE_DOWNLOADING.ordinal());
            version.update();
            Intent downloadIntent = new Intent(getContext(), UWVersionDownloaderService.class);
            downloadIntent.putExtra(UWVersionDownloaderService.VERSION_PARAM, version.getId());
            getContext().startService(downloadIntent);
            reload();
        }
    }

    private void downloadMedia(VersionRowViewHolder holder, Version version, boolean isVideo){

        if (!NetWorkUtil.isConnected(getContext())) {
            new AlertDialog.Builder(getContext())
                    .setTitle("Alert")
                    .setMessage("Failed connecting to the internet.")
                    .setPositiveButton("OK", null)
                    .create().show();
        } else {
            setupIntentFilter();
            Intent downloadIntent = new Intent(getContext(), UWMediaDownloaderService.class);
            downloadIntent.putExtra(UWMediaDownloaderService.VERSION_PARAM, version.getId());
            downloadIntent.putExtra(UWMediaDownloaderService.IS_VIDEO_PARAM, isVideo);
            getContext().startService(downloadIntent);
            if(!isVideo){
                version.willDownloadAudio();
                holder.setupForAudioDownloadState(DownloadState.DOWNLOAD_STATE_DOWNLOADING, false);
            }
        }
    }

    private void deleteVersion(final Version version){

        (new DialogFragment() {

            @NonNull
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                // Use the Builder class for convenient dialog construction
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(getResources().getString(R.string.delete_warning_text))
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                listener.isLoading(true);
                                UWPreferenceDataManager.willDeleteVersion(getContext(), version);
                                new DeleteVersionTask().execute(version);
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                reload();
                            }
                        });
                // Create the AlertDialog object and return it
                return builder.create();
            }
        }).show(parentFragment.getFragmentManager(), "confirmAlert");
    }

    @Override
    public void deleteAudioWasPressed(Version version) {

        deleteAudio(version);
    }

    private void deleteAudio(final Version version){
        (new DialogFragment() {

            @NonNull
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                // Use the Builder class for convenient dialog construction
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(getResources().getString(R.string.delete_audio_warning_text))
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                listener.isLoading(true);
                                new DeleteVersionAudioTask().execute(version);
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                reload();
                            }
                        });
                // Create the AlertDialog object and return it
                return builder.create();
            }
        }).show(parentFragment.getFragmentManager(), "confirmAlert");
    }

    private void stopDownload(final Version version){

        (new DialogFragment() {


            @NonNull
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                // Use the Builder class for convenient dialog construction
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Stop download?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                DownloadState state = DownloadState.createState(Version.getVersionForId(
                                        version.getId(), DaoDBHelper.getDaoSession(getContext())).getSaveState());
                                stopDownloadService(version);

                                if(state == DownloadState.DOWNLOAD_STATE_DOWNLOADING ||
                                        state == DownloadState.DOWNLOAD_STATE_ERROR) {new DeleteVersionTask().execute(version);
                                }
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                // Create the AlertDialog object and return it
                return builder.create();
            }
        }).show(parentFragment.getFragmentManager(), "confirmAlert");
    }

    private class DeleteVersionTask extends AsyncTask<Version, Void, Void> {

        @Override
        protected Void doInBackground(Version... params) {
            UWPreferenceDataManager.willDeleteVersion(getContext(), params[0]);
            params[0].deleteContent();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            reload();
            listener.isLoading(false);
        }
    }

    private class DeleteVersionAudioTask extends AsyncTask<Version, Void, Void> {

        @Override
        protected Void doInBackground(Version... params) {
            params[0].deleteAudio(getContext());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            listener.isLoading(false);
            reload();
        }
    }

    private void stopDownloadService(Version version){

        getContext().sendBroadcast(new Intent(UWVersionDownloaderService.STOP_DOWNLOAD_VERSION_MESSAGE).
                putExtra(UWVersionDownloaderService.VERSION_ID, Long.toString(version.getId())));
    }

    //endregion

    //region group methods

    @Override
    public Language getGroup(int groupPosition) {
        return currentProject.getLanguages().get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return currentProject.getLanguages().size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        Language language = getGroup(groupPosition);
        language.getVersions();
        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_group,
                    null);
        }

        TextView item = (TextView) convertView.findViewById(R.id.group_title);
        item.setTypeface(null, Typeface.BOLD);
        LanguageLocale languageLocale = LanguageLocale.getLocalForKey(language.getLanguageAbbreviation(), DaoDBHelper.getDaoSession(getContext()));
        item.setText((languageLocale != null)? languageLocale.getLanguageName() : "");

        return convertView;
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public boolean hasStableIds() {
        return true;
    }

    //endregion

    public interface VersionAdapterListener{
        /**
         * User chose a version
         * @param version Version that was chosen
         */
        void versionWasSelected(Version version);

        /**
         * The adapter is loading
         * @param loading true if the adapter is loading
         */
        void isLoading(boolean loading);
    }
}

