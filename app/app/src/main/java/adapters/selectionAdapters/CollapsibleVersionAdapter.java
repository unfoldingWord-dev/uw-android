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
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.unfoldingword.mobile.R;

import java.util.ArrayList;

import fragments.VersionSelectionFragment;
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
import utils.URLUtils;
import utils.UWPreferenceManager;
import view.AnimatedExpandableListView;


public class CollapsibleVersionAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {

    private final static String TAG = "CollapseVersionAdapter";
    static final String STORIES_SLUG = "obs";
    private Fragment parentFragment;
    Project currentProject = null;

    public CollapsibleVersionAdapter(Fragment fragment, Project currentProject) {
        this.parentFragment = fragment;
        this.currentProject = currentProject;

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
            holder.languageNameTextView = (TextView) convertView.findViewById(R.id.languageNameTextView);
            holder.languageTypeImageView = (ImageView) convertView.findViewById(R.id.languageTypeImageView);
            holder.visibleFrameLayout = (FrameLayout) convertView.findViewById(R.id.visibleLayout);
            holder.checkingEntityTextView = (TextView) convertView.findViewById(R.id.checkingEntitytextView);
            holder.checkingLevelImage = (ImageView) convertView.findViewById(R.id.checking_level_image);
            holder.versionTextView = (TextView) convertView.findViewById(R.id.versionTextView);
            holder.publishDateTextView = (TextView) convertView.findViewById(R.id.publishDateTextView);
            holder.checkingEntityConstantTextView = (TextView) convertView.findViewById(R.id.checking_entity_constant_text_view);
            holder.checkingLevelConstantTextView = (TextView) convertView.findViewById(R.id.checkingLevelConstanttextView);
            holder.versionConstantTextView = (TextView) convertView.findViewById(R.id.versionConstanttextView);
            holder.publishDateConstantTextView = (TextView) convertView.findViewById(R.id.publishDateConstanttextView);
            holder.verificationTextView = (TextView) convertView.findViewById(R.id.verification_text_view);
            holder.verificationTitle = (TextView) convertView.findViewById(R.id.verification_title);
            holder.clickableLayout = (LinearLayout) convertView.findViewById(R.id.clickableRow);
            holder.infoFrame = (FrameLayout) convertView.findViewById(R.id.info_image_frame);
            holder.status = (Button) convertView.findViewById(R.id.status);
            holder.checkingLevelExplanationTextView = (TextView) convertView.findViewById(R.id.checking_level_explanation_text);
            holder.versionNameTextView = (TextView) convertView.findViewById(R.id.version_name_text);

            holder.downloadButton = (ImageView) convertView.findViewById(R.id.download_status_image);
            holder.downloadFrame = (FrameLayout) convertView.findViewById(R.id.download_status_frame);
            holder.downloadProgressBar = (ProgressBar) convertView.findViewById(R.id.download_progress_bar);
            holder.deleteButton = (Button) convertView.findViewById(R.id.delete_button);

            final ViewHolderForGroup finalHolder = holder;
            holder.infoFrame.setOnClickListener(getInfoClickListener(finalHolder, version));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolderForGroup) convertView.getTag();
        }

        // version-dependent settings

        boolean isSelected = ((version.getId() == UWPreferenceManager.getSelectedBibleVersion(getContext()))
                || (version.getId() == UWPreferenceManager.getSelectedStoryVersion(getContext())));

        int state = isSelected? 2 : 1;
        holder.downloadProgressBar.setVisibility(View.INVISIBLE);
        holder.downloadButton.setVisibility(View.VISIBLE);

        state = setRowState(holder, version, state);

        holder.downloadFrame.setOnClickListener(getDownloadOnClickListener(version, holder));
        holder.deleteButton.setOnClickListener(getDeleteOnClickListener(version, holder));
        setColorChange(holder, getColorForState(state));
        holder.languageTypeImageView.setImageResource(getCheckingLevelImage(Integer.parseInt(version.getStatusCheckingLevel())));

        holder.languageNameTextView.setText(version.getName());
        holder.checkingEntityTextView.setText(version.getStatusCheckingEntity());
        holder.checkingLevelImage.setImageResource(getCheckingLevelImage(Integer.parseInt(version.getStatusCheckingLevel())));
        holder.versionTextView.setText(version.getStatusVersion());
        holder.publishDateTextView.setText(version.getStatusPublishDate());
        holder.verificationTextView.setText(getVerificationText(version));
        holder.checkingLevelExplanationTextView.setText(getCheckingLevelText(Integer.parseInt(version.getStatusCheckingLevel())));
        holder.versionNameTextView.setText(version.getName());

        int verificationStatus = 1;//version.getVerificationStatus(getContext());
        holder.status.setBackgroundResource(getColorForStatus(verificationStatus));
        holder.status.setText(getButtonTextForStatus(verificationStatus));

        return convertView;
    }

    private int getCheckingLevelText(int level){

        switch (level){
            case 2:{
                return R.string.level_two;
            }
            case 3:{
                return R.string.level_three;
            }
            default:{
                return R.string.level_one;
            }
        }
    }

    private int getCheckingLevelImage(int level){
        switch (level){
            case 2:{
                return R.drawable.level_two_dark;
            }
            case 3:{
                return R.drawable.level_three_dark;
            }
            default:{
                return R.drawable.level_one_dark;
            }
        }
    }

    private String getVerificationText(Version version){

        String text = "testing stuff";
//        int status = version.getVerificationStatus(getContext());
//        switch (status){
//            case 0:{
//                text = getContext().getResources().getString(R.string.verified_title_start);
//                break;
//            }
//            case 1:{
//                text = getContext().getResources().getString(R.string.expired_title_start) + "\n" + version.verificationText;
//                break;
//            }
//            case 3:{
//                text = getContext().getResources().getString(R.string.failed_title_start) + "\n" + version.verificationText;
//                break;
//            }
//            default:{
//                text = getContext().getResources().getString(R.string.error_title_start) + "\n" + version.verificationText;
//            }
//        }
//
//        ArrayList<String> organizations = version.getSigningOrganizations(getContext());

//        if(status == 0){
//            for(String org : organizations){
//                text += " " + org + ",";
//            }
//            text = text.substring(0, text.length() - 1);
//        }

        return text;
    }

    private int setRowState(ViewHolderForGroup holder, Version version, int selectionState){

        switch (DownloadState.createState(version.getSaveState())){

            case DOWNLOAD_STATE_DOWNLOADED:{
                holder.verificationTextView.setVisibility(View.VISIBLE);
                holder.verificationTitle.setVisibility(View.VISIBLE);
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
                holder.verificationTextView.setVisibility(View.GONE);
                holder.verificationTitle.setVisibility(View.GONE);
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
                holder.verificationTextView.setVisibility(View.GONE);
                holder.verificationTitle.setVisibility(View.GONE);
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
            downloadIntent.putExtra(UWVersionDownloader.VERSION_PARAM, version);
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

                                if(UWPreferenceManager.getSelectedBibleVersion(getContext()) == version.getId()){
                                    UWPreferenceManager.setSelectedBibleVersion(getContext(), -1);
                                    UWPreferenceManager.setSelectedBibleChapter(getContext(), -1);
                                }
                                if(UWPreferenceManager.getSelectedStoryVersion(getContext()) == version.getId()){
                                    UWPreferenceManager.setSelectedStoryVersion(getContext(), -1);
                                    UWPreferenceManager.setSelectedStoryChapter(getContext(), -1);
                                }
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
                if (version instanceof Version) {
                    boolean isStoryChapter = version.getLanguage().getProject().getSlug().equalsIgnoreCase(STORIES_SLUG);
                    refreshChapterSelection(version, isStoryChapter);
                    if(isStoryChapter){
                        UWPreferenceManager.setSelectedStoryVersion(getContext(), version.getId());
                    }
                    else {
                        UWPreferenceManager.setSelectedBibleVersion(getContext(), version.getId());
                    }
                }
            ((VersionSelectionFragment) parentFragment).rowSelected();
            }
        };
    }


    private void refreshChapterSelection(Version version, boolean isStoryChapter){

        if(isStoryChapter){

            long chapterId = UWPreferenceManager.getSelectedStoryChapter(getContext());
            if(chapterId < 0){

                StoriesChapter newChapter = version.getBooks().get(0).getStoryChapters().get(1);
                UWPreferenceManager.setSelectedStoryChapter(getContext(), newChapter.getId());
            }
            else {
                StoriesChapter chapter = StoriesChapter.getModelForId(chapterId, DaoDBHelper.getDaoSession(getContext()));
                if(chapter == null){
                    StoriesChapter newChapter = version.getBooks().get(0).getStoryChapters().get(1);
                    UWPreferenceManager.setSelectedStoryChapter(getContext(), newChapter.getId());
                    return;
                }
                Book newBook = chapter.getBook();

                StoriesChapter newChapter = (newBook == null)? version.getBooks().get(0).getStoryChapters().get(1) :
                        newBook.getStoryChapters().get(Integer.parseInt(chapter.getNumber()));

                UWPreferenceManager.setSelectedStoryChapter(getContext(), newChapter.getId());
            }
        }
        else{
            long chapterId = UWPreferenceManager.getSelectedBibleChapter(getContext());
            if(chapterId < 0){
                BibleChapter newChapter = version.getBooks().get(0).getBibleChapters().get(1);
                UWPreferenceManager.setSelectedBibleChapter(getContext(), newChapter.getId());
            }
            else {
                BibleChapter chapter = BibleChapter.getModelForId(chapterId, DaoDBHelper.getDaoSession(getContext()));
                if(chapter == null){
                    BibleChapter newChapter = version.getBooks().get(0).getBibleChapters().get(1);
                    UWPreferenceManager.setSelectedBibleChapter(getContext(), newChapter.getId());
                    return;
                }
                Book newBook = chapter.getBook();

                BibleChapter newChapter = (newBook == null)? version.getBooks().get(0).getBibleChapters().get(1) :
                        newBook.getBibleChapters().get(Integer.parseInt(chapter.getNumber().trim()));

                if(newChapter == null){
                    newChapter = version.getBooks().get(0).getBibleChapters().get(1);
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
                if (finalHolder.visibleFrameLayout.getVisibility() == View.GONE) {

                    CustomSlideAnimationRelativeLayout animationRelativeLayout = new CustomSlideAnimationRelativeLayout(finalHolder.visibleFrameLayout, 300, CustomSlideAnimationRelativeLayout.EXPAND);
                    finalHolder.visibleFrameLayout.startAnimation(animationRelativeLayout);
                } else {
                    CustomSlideAnimationRelativeLayout animationRelativeLayout = new CustomSlideAnimationRelativeLayout(finalHolder.visibleFrameLayout, 300, CustomSlideAnimationRelativeLayout.COLLAPSE);
                    finalHolder.visibleFrameLayout.startAnimation(animationRelativeLayout);
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

    private String getButtonTextForStatus(int currentStatus){

        switch (currentStatus){
            case 0:
                return getContext().getResources().getString(R.string.verified_button_char);
            case 1:
                return getContext().getResources().getString(R.string.expired_button_char);
            default:
                return getContext().getResources().getString(R.string.x_button_char);
        }
    }

    private int getColorForStatus(int currentStatus){

        switch (currentStatus){
            case 0:
                return R.drawable.green_checkmark;
            case 1:
                return R.drawable.yellow_exclamation_point;
            default:
                return R.drawable.red_x_button;
        }
    }

    protected int getColorForState(int state){

        switch (state){
            case 1:
                return getContext().getResources().getColor(R.color.black_light);
            case 2:
                return getContext().getResources().getColor(R.color.cyan);
            default:
                return getContext().getResources().getColor(R.color.lightgrey);
        }
    }


    private static class ViewHolderForGroup {

        TextView languageNameTextView;
        ImageView languageTypeImageView;
        FrameLayout visibleFrameLayout;
        TextView checkingEntityTextView;
        ImageView checkingLevelImage;
        TextView versionTextView;
        TextView publishDateTextView;
        TextView checkingEntityConstantTextView;
        TextView checkingLevelConstantTextView;
        TextView versionConstantTextView;
        TextView publishDateConstantTextView;
        TextView verificationTextView;
        TextView verificationTitle;
        LinearLayout clickableLayout;
        FrameLayout infoFrame;
        Button status;
        TextView versionNameTextView;

        ImageView downloadButton;
        FrameLayout downloadFrame;
        ProgressBar downloadProgressBar;
        Button deleteButton;
        TextView checkingLevelExplanationTextView;
    }
}

