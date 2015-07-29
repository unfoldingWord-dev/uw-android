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
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.unfoldingword.mobile.R;

import model.DaoDBHelper;
import model.DownloadState;
import model.daoModels.BibleChapter;
import model.daoModels.Book;
import model.daoModels.Language;
import model.daoModels.LanguageLocale;
import model.daoModels.Project;
import model.daoModels.StoriesChapter;
import model.daoModels.Version;
import services.UWVersionDownloader;
import services.VersionDownloadService;
import utils.CustomSlideAnimationRelativeLayout;
import utils.NetWorkUtil;
import utils.RowStatusHelper;
import utils.URLUtils;
import utils.UWPreferenceManager;
import view.AnimatedExpandableListView;
import view.VersionInformationViewHolder;
import view.ViewHelper;


public class CollapsibleVersionAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {

    private final static String TAG = "CollapseVersionAdapter";
    static final String STORIES_SLUG = "obs";
    private Fragment parentFragment;
    private Project currentProject = null;
    private long selectedVersionId;

    private VersionAdapterListener listener;

    public interface VersionAdapterListener{
        void versionWasSelected(Version version);
    }

    public CollapsibleVersionAdapter(Fragment fragment, Project currentProject, long selectedVersionId, VersionAdapterListener listener) {
        this.parentFragment = fragment;
        this.currentProject = currentProject;
        this.selectedVersionId = selectedVersionId;
        this.listener = listener;
        IntentFilter filter = new IntentFilter();
        filter.addAction(URLUtils.VERSION_BROADCAST_DOWN_COMP);
        filter.addAction(URLUtils.VERSION_BROADCAST_DOWN_ERROR);
        getContext().registerReceiver(receiver, filter);
    }

    private Context getContext(){
        return this.parentFragment.getActivity();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle extra = intent.getExtras();
            if (extra != null) {
                String itemId = extra.getString(VersionDownloadService.VERSION_ID);
                Log.d(TAG, itemId);
            }
            if (intent.getAction().equals(URLUtils.VERSION_BROADCAST_DOWN_COMP)) {
                Toast.makeText(context, "Download Complete", Toast.LENGTH_SHORT).show();
                reload();
            }
            else if(intent.getAction().equals(URLUtils.VERSION_BROADCAST_DOWN_STOPPED)){
                Toast.makeText(context, "Download Stopped", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(context, "Download Error", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void reload(){

        currentProject = Project.getProjectForId(currentProject.getId(), DaoDBHelper.getDaoSession(getContext()));
        notifyDataSetChanged();
    }

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
        int count = getGroup(groupPosition).getVersions().size();
        return count;
    }



    @Override
    public View getRealChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final Version version = getChild(groupPosition, childPosition);
        final ViewHolderForGroup holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_version_selector, parent, false);
            holder = new ViewHolderForGroup();
            holder.languageNameTextView = (TextView) convertView.findViewById(R.id.language_name_text_view);
            holder.languageTypeImageView = (ImageView) convertView.findViewById(R.id.language_type_image_view);
            holder.versionInfoLayout = (LinearLayout) convertView.findViewById(R.id.version_information_layout);

            holder.clickableLayout = (LinearLayout) convertView.findViewById(R.id.clickableRow);

            holder.infoFrame = (FrameLayout) convertView.findViewById(R.id.info_image_frame);
            holder.status = (Button) convertView.findViewById(R.id.status);

            holder.downloadButton = (ImageView) convertView.findViewById(R.id.download_status_image);
            holder.downloadFrame = (FrameLayout) convertView.findViewById(R.id.download_status_frame);
            holder.downloadProgressBar = (ProgressBar) convertView.findViewById(R.id.download_progress_bar);
            holder.deleteButton = (Button) convertView.findViewById(R.id.delete_button);
            holder.versionInformationHolder = new VersionInformationViewHolder(convertView);
            final ViewHolderForGroup finalHolder = holder;
            holder.infoFrame.setOnClickListener(getInfoClickListener(finalHolder, version));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolderForGroup) convertView.getTag();
        }

        // version-dependent settings

        boolean isSelected = (version.getId() == selectedVersionId);

        int state = isSelected? 2 : 1;
        holder.downloadProgressBar.setVisibility(View.INVISIBLE);
        holder.downloadButton.setVisibility(View.VISIBLE);
        holder.versionInformationHolder.setInfoForVersion(version);

        state = setRowState(holder, version, state);

        holder.downloadFrame.setOnClickListener(getDownloadOnClickListener(version, holder));
        holder.deleteButton.setOnClickListener(getDeleteOnClickListener(version, holder));
        setColorChange(holder, RowStatusHelper.getColorForState(getContext(), state));
        holder.languageTypeImageView.setImageResource(ViewHelper.getCheckingLevelImage(Integer.parseInt(version.getStatusCheckingLevel())));
        holder.languageNameTextView.setText(version.getName());

        int verificationStatus = 1;//version.getVerificationStatus(getContext());
        holder.status.setBackgroundResource(RowStatusHelper.getColorForStatus(verificationStatus));
        holder.status.setText(RowStatusHelper.getButtonTextForStatus(getContext(), verificationStatus));

        return convertView;
    }

    private int setRowState(ViewHolderForGroup holder, Version version, int selectionState){

        holder.versionInformationHolder.setRowState(version);
        switch (DownloadState.createState(version.getSaveState())){

            case DOWNLOAD_STATE_DOWNLOADED:{
                holder.status.setVisibility(View.VISIBLE);
                holder.downloadButton.setVisibility(View.GONE);
                holder.downloadProgressBar.setVisibility(View.GONE);
                holder.deleteButton.setVisibility(View.VISIBLE);
                holder.clickableLayout.setClickable(true);
                holder.downloadFrame.setClickable(false);
                holder.downloadFrame.setVisibility(View.GONE);
                holder.clickableLayout.setOnClickListener(getSelectionOnClickListener(version));

                return selectionState;
            }
            case DOWNLOAD_STATE_DOWNLOADING:{
                holder.status.setVisibility(View.GONE);
                holder.downloadButton.setVisibility(View.INVISIBLE);
                holder.downloadProgressBar.setVisibility(View.VISIBLE);
                holder.deleteButton.setVisibility(View.GONE);
                holder.downloadFrame.setVisibility(View.VISIBLE);
                holder.clickableLayout.setClickable(false);
                holder.downloadFrame.setClickable(true);
                return 3;
            }

            default:{
                holder.status.setVisibility(View.GONE);
                holder.downloadButton.setVisibility(View.VISIBLE);
                holder.downloadProgressBar.setVisibility(View.GONE);
                holder.deleteButton.setVisibility(View.GONE);
                holder.downloadFrame.setVisibility(View.VISIBLE);
                holder.clickableLayout.setClickable(false);
                holder.downloadFrame.setClickable(true);
                return 3;
            }

        }
    }

    private View.OnClickListener getDeleteOnClickListener(final Version version, final ViewHolderForGroup finalHolder) {

        return new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                deleteRowPressed(version, finalHolder);
            }
        };
    }

    private View.OnClickListener getDownloadOnClickListener(final Version version, final ViewHolderForGroup finalHolder) {

        return new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (version instanceof Version) {
                    finalHolder.clickableLayout.setClickable(false);
                    finalHolder.downloadProgressBar.setVisibility(View.VISIBLE);
                    finalHolder.downloadButton.setVisibility(View.INVISIBLE);
                    finalHolder.deleteButton.setVisibility(View.INVISIBLE);

                    if((version.getSaveState() == DownloadState.DOWNLOAD_STATE_NONE.ordinal())
                    || (version.getSaveState() == DownloadState.DOWNLOAD_STATE_ERROR.ordinal())) {
                        downloadRow(version, finalHolder);
                    }
                    else if(version.getSaveState() == DownloadState.DOWNLOAD_STATE_DOWNLOADING.ordinal()){
                        stopDownload(version, finalHolder);
                    }
                }
            }
        };
    }

    private void downloadRow(final Version version, final ViewHolderForGroup finalHolder){

        if (!NetWorkUtil.isConnected(getContext())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Alert");
            builder.setMessage("Failed connecting to the internet.");
            builder.setPositiveButton("OK", null);
            builder.create().show();
        } else {
            version.setSaveState(DownloadState.DOWNLOAD_STATE_DOWNLOADING.ordinal());
            version.update();
            Intent downloadIntent = new Intent(getContext(), UWVersionDownloader.class);
            downloadIntent.putExtra(UWVersionDownloader.VERSION_PARAM, version.getId());
            getContext().startService(downloadIntent);
            reload();
        }
    }

    private void deleteRowPressed(final Version version, final ViewHolderForGroup finalHolder){

        (new DialogFragment() {

            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                // Use the Builder class for convenient dialog construction
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(getResources().getString(R.string.delete_warning_text))
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                UWPreferenceManager.willDeleteVersion(getContext(), version);
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

    private void stopDownload(final Version version, final ViewHolderForGroup finalHolder){

        (new DialogFragment() {

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

    private void stopDownloadService(Version version){

        getContext().sendBroadcast(new Intent(VersionDownloadService.STOP_DOWNLOAD_VERSION_MESSAGE).
                putExtra(VersionDownloadService.VERSION_ID, Long.toString(version.getId())));
    }

    private class DeleteVersionTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
//            return new VersionDataSource(getContext()).deleteDownloadedBookContent((VersionModel) params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            reload();
        }
    }

    private View.OnClickListener getSelectionOnClickListener(final Version version) {

        return new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(listener != null) {
                    listener.versionWasSelected(version);
                }
            }
        };
    }


    private void refreshChapterSelection(Version version, boolean isStoryChapter){

        if(isStoryChapter){

            long chapterId = UWPreferenceManager.getSelectedStoryPage(getContext());
            if(chapterId < 0){

                StoriesChapter newChapter = version.getBooks().get(0).getStoryChapters(true).get(1);
                UWPreferenceManager.setSelectedStoryPage(getContext(), newChapter.getId());
            }
            else {
                StoriesChapter chapter = StoriesChapter.getModelForId(chapterId, DaoDBHelper.getDaoSession(getContext()));
                if(chapter == null){
                    StoriesChapter newChapter = version.getBooks().get(0).getStoryChapters(true).get(1);
                    UWPreferenceManager.setSelectedStoryPage(getContext(), newChapter.getId());
                    return;
                }
                Book newBook = chapter.getBook();

                StoriesChapter newChapter = (newBook == null)? version.getBooks().get(0).getStoryChapters(true).get(1) :
                        newBook.getStoryChapters(true).get(Integer.parseInt(chapter.getNumber()));

                UWPreferenceManager.setSelectedStoryPage(getContext(), newChapter.getId());
            }
        }
        else{
            long chapterId = UWPreferenceManager.getSelectedBibleChapter(getContext());
            if(chapterId < 0){
                BibleChapter newChapter = version.getBooks().get(0).getBibleChapters(true).get(0);
                UWPreferenceManager.setSelectedBibleChapter(getContext(), newChapter.getId());
            }
            else {
                BibleChapter chapter = BibleChapter.getModelForId(chapterId, DaoDBHelper.getDaoSession(getContext()));
                if(chapter == null){
                    BibleChapter newChapter = version.getBooks().get(0).getBibleChapters(true).get(1);
                    UWPreferenceManager.setSelectedBibleChapter(getContext(), newChapter.getId());
                    return;
                }
                Book newBook = chapter.getBook();

                BibleChapter newChapter = (newBook == null)? version.getBooks().get(0).getBibleChapters(true).get(1) :
                        newBook.getBibleChapters(true).get(Integer.parseInt(chapter.getNumber().trim()));

                if(newChapter == null){
                    newChapter = version.getBooks().get(0).getBibleChapters(true).get(1);
                }
                UWPreferenceManager.setSelectedBibleChapter(getContext(), newChapter.getId());
            }
        }

    }

    @Override
    public Language getGroup(int groupPosition) {
        Language model = currentProject.getLanguages().get(groupPosition);
        return model;
    }

    @Override
    public int getGroupCount() {
        int groupCount = currentProject.getLanguages().size();
        return groupCount;
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
        item.setText(languageLocale.getLanguageName());

        return convertView;
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void setColorChange(ViewHolderForGroup holder, int color) {
        holder.languageNameTextView.setTextColor(color);
    }


    private View.OnClickListener getInfoClickListener(final ViewHolderForGroup finalHolder, final Version version){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (finalHolder.versionInfoLayout.getVisibility() == View.GONE) {

                    CustomSlideAnimationRelativeLayout animationRelativeLayout = new CustomSlideAnimationRelativeLayout(finalHolder.versionInfoLayout, 300, CustomSlideAnimationRelativeLayout.EXPAND);
                    finalHolder.versionInfoLayout.startAnimation(animationRelativeLayout);
                } else {
                    CustomSlideAnimationRelativeLayout animationRelativeLayout = new CustomSlideAnimationRelativeLayout(finalHolder.versionInfoLayout, 300, CustomSlideAnimationRelativeLayout.COLLAPSE);
                    finalHolder.versionInfoLayout.startAnimation(animationRelativeLayout);
                }
            }
        };
    }

    public void willDestroy(){
        if(receiver != null) {
            getContext().unregisterReceiver(receiver);
        }
        receiver = null;
    }




    private static class ViewHolderForGroup {

        VersionInformationViewHolder versionInformationHolder;
        TextView languageNameTextView;
        ImageView languageTypeImageView;
        LinearLayout versionInfoLayout;
        LinearLayout clickableLayout;

        FrameLayout infoFrame;
        Button status;

        ImageView downloadButton;
        FrameLayout downloadFrame;
        ProgressBar downloadProgressBar;
        Button deleteButton;
    }
}

