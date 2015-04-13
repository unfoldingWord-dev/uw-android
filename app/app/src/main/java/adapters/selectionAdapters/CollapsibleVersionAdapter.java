package adapters.selectionAdapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
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
import model.datasource.BibleChapterDataSource;
import model.datasource.LanguageLocaleDataSource;
import model.datasource.ProjectDataSource;
import model.datasource.StoriesChapterDataSource;
import model.datasource.VersionDataSource;
import model.modelClasses.mainData.BibleChapterModel;
import model.modelClasses.mainData.BookModel;
import model.modelClasses.mainData.LanguageLocaleModel;
import model.modelClasses.mainData.LanguageModel;
import model.modelClasses.mainData.ProjectModel;
import model.modelClasses.mainData.StoriesChapterModel;
import model.modelClasses.mainData.VersionModel;
import services.VersionDownloadService;
import utils.CustomSlideAnimationRelativeLayout;
import utils.NetWorkUtil;
import utils.URLUtils;
import utils.UWPreferenceManager;


public class CollapsibleVersionAdapter extends BaseExpandableListAdapter {

    private final static String TAG = "CollapseVersionAdapter";
    static final String STORIES_SLUG = "obs";
    private Fragment parentFragment;
    ProjectModel currentProject = null;

    public CollapsibleVersionAdapter(Fragment fragment, ProjectModel currentProject) {
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
                Toast.makeText(context, "Download complete", Toast.LENGTH_SHORT).show();
                reload();
            } else {
                Toast.makeText(context, "Download error", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void reload(){

        currentProject = new ProjectDataSource(getContext()).getModel(Long.toString(currentProject.uid));
        notifyDataSetChanged();
    }

    @Override
    public VersionModel getChild(int groupPosition, int childPosition) {
        return currentProject.getChildModels(getContext()).get(groupPosition).getChildModels(getContext()).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final VersionModel version = getChild(groupPosition, childPosition);
        ViewHolderForGroup holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_version_selector, parent, false);
            holder = new ViewHolderForGroup();
            holder.languageNameTextView = (TextView) convertView.findViewById(R.id.languageNameTextView);
            holder.languageTypeImageView = (ImageView) convertView.findViewById(R.id.languageTypeImageView);
            holder.visibleFrameLayout = (FrameLayout) convertView.findViewById(R.id.visibleLayout);
            holder.checkingEntityTextView = (TextView) convertView.findViewById(R.id.checkingEntitytextView);
            holder.checkingLevelTextView = (TextView) convertView.findViewById(R.id.checkingLevelTextView);
            holder.versionTextView = (TextView) convertView.findViewById(R.id.versionTextView);
            holder.publishDateTextView = (TextView) convertView.findViewById(R.id.publishDateTextView);
            holder.checkingEntityConstantTextView = (TextView) convertView.findViewById(R.id.checking_entity_constant_text_view);
            holder.checkingLevelConstantTextView = (TextView) convertView.findViewById(R.id.checkingLevelConstanttextView);
            holder.versionConstantTextView = (TextView) convertView.findViewById(R.id.versionConstanttextView);
            holder.publishDateConstantTextView = (TextView) convertView.findViewById(R.id.publishDateConstanttextView);
            holder.verificationTextView = (TextView) convertView.findViewById(R.id.verification_text_view);
            holder.clickableLayout = (LinearLayout) convertView.findViewById(R.id.clickableRow);
            holder.infoFrame = (FrameLayout) convertView.findViewById(R.id.info_image_frame);
            holder.status = (Button) convertView.findViewById(R.id.status);

            holder.downloadImageView = (ImageView) convertView.findViewById(R.id.download_status_image);
            holder.downloadFrame = (FrameLayout) convertView.findViewById(R.id.download_status_frame);
            holder.downloadProgressBar = (ProgressBar) convertView.findViewById(R.id.download_progress_bar);

            final ViewHolderForGroup finalHolder = holder;
            holder.infoFrame.setOnClickListener(getInfoClickListener(finalHolder, version));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolderForGroup) convertView.getTag();
        }

        // version-dependent settings

        boolean isSelected = ((version.uid == Long.parseLong(UWPreferenceManager.getSelectedBibleVersion(getContext())))
                || (version.uid == Long.parseLong(UWPreferenceManager.getSelectedStoryVersion(getContext()))));

        int state = isSelected? 2 : 1;
        holder.downloadProgressBar.setVisibility(View.INVISIBLE);
        holder.downloadImageView.setVisibility(View.VISIBLE);

        state = setRowState(holder, version, state);

        holder.downloadFrame.setOnClickListener(getDownloadOnClickListener(version, holder));
        setColorChange(holder, getColorForState(state));
        holder.languageTypeImageView.setImageResource(getCheckingLevelImage(Integer.parseInt(version.status.checkingLevel)));

        holder.languageNameTextView.setText(version.getTitle());
        holder.checkingEntityTextView.setText(version.status.checkingEntity);
        holder.checkingEntityTextView.setText(version.status.checkingEntity);
        holder.checkingLevelTextView.setText(version.status.checkingLevel);
        holder.versionTextView.setText(version.status.version);
        holder.publishDateTextView.setText(version.status.publishDate);

        int verificationStatus = version.getVerificationStatus(getContext());
        holder.status.setBackgroundResource(getColorForStatus(verificationStatus));
        holder.status.setText(getButtonTextForStatus(verificationStatus));

        return convertView;
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

    private String getVerificationText(VersionModel version){

        String text;
        int status = version.getVerificationStatus(getContext());
        switch (status){
            case 0:{
                text = getContext().getResources().getString(R.string.verified_title_start);
                break;
            }
            case 1:{
                text = getContext().getResources().getString(R.string.expired_title_start);
                break;
            }
            case 3:{
                text = getContext().getResources().getString(R.string.failed_title_start);
                break;
            }
            default:{
                text = getContext().getResources().getString(R.string.error_title_start);
            }
        }

        ArrayList<String> organizations = version.getSigningOrganizations(getContext());

        if(status == 0){
            for(String org : organizations){
                text += " " + org + ",";
            }
            text = text.substring(0, text.length() - 1);
        }

        return text;
    }

    private int setRowState(ViewHolderForGroup holder, VersionModel version, int selectionState){

        switch (version.downloadState){

            case DOWNLOAD_STATE_DOWNLOADED:{
                holder.status.setVisibility(View.VISIBLE);
                holder.downloadImageView.setVisibility(View.VISIBLE);
                holder.downloadProgressBar.setVisibility(View.INVISIBLE);
                holder.clickableLayout.setClickable(true);
                holder.downloadFrame.setClickable(true);
                holder.downloadImageView.setImageResource(R.drawable.x_button);
                holder.clickableLayout.setOnClickListener(getSelectionOnClickListener(version));
                return selectionState;
            }
            case DOWNLOAD_STATE_DOWNLOADING:{
                holder.status.setVisibility(View.INVISIBLE);
                holder.downloadImageView.setVisibility(View.INVISIBLE);
                holder.downloadProgressBar.setVisibility(View.VISIBLE);
                holder.clickableLayout.setClickable(false);
                holder.downloadFrame.setClickable(true);
                return 3;
            }

            default:{
                holder.status.setVisibility(View.INVISIBLE);
                holder.downloadImageView.setVisibility(View.VISIBLE);
                holder.downloadProgressBar.setVisibility(View.INVISIBLE);
                holder.clickableLayout.setClickable(false);
                holder.downloadFrame.setClickable(true);
                holder.downloadImageView.setImageResource(R.drawable.download_button);
                return 3;
            }

        }
    }

    private View.OnClickListener getDownloadOnClickListener(final VersionModel version, final ViewHolderForGroup finalHolder) {

        return new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (version instanceof VersionModel) {
                    finalHolder.clickableLayout.setClickable(false);
                    finalHolder.downloadProgressBar.setVisibility(View.VISIBLE);
                    finalHolder.downloadImageView.setVisibility(View.INVISIBLE);

                    if((version.downloadState == VersionModel.DOWNLOAD_STATE.DOWNLOAD_STATE_NONE)
                    || (version.downloadState == VersionModel.DOWNLOAD_STATE.DOWNLOAD_STATE_ERROR)) {

                        if (!NetWorkUtil.isConnected(getContext())) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Alert");
                            builder.setMessage("Failed connecting to the internet.");
                            builder.setPositiveButton("OK", null);
                            builder.create().show();
                        } else {
                            // to handle new data from network
                            version.downloadState = VersionModel.DOWNLOAD_STATE.DOWNLOAD_STATE_DOWNLOADING;
                            version.save(getContext());
                            Intent downloadIntent = new Intent(getContext(), VersionDownloadService.class);
                            downloadIntent.putExtra(VersionDownloadService.VERSION_ID, Long.toString(version.uid));
                            getContext().startService(downloadIntent);
                        }
                    }
                    else if(version.downloadState == VersionModel.DOWNLOAD_STATE.DOWNLOAD_STATE_DOWNLOADING){
                        getContext().stopService(new Intent(getContext(), VersionDownloadService.class));
                        new DeleteVersionTask().execute(version);
                    }
                    else{
                        if(Long.parseLong(UWPreferenceManager.getSelectedBibleVersion(getContext())) == version.uid){
                            UWPreferenceManager.setSelectedBibleVersion(getContext(), -1);
                            UWPreferenceManager.setSelectedBibleChapter(getContext(), -1);
                        }
                        if(Long.parseLong(UWPreferenceManager.getSelectedStoryVersion(getContext())) == version.uid){
                            UWPreferenceManager.setSelectedStoryVersion(getContext(), -1);
                            UWPreferenceManager.setSelectedStoryChapter(getContext(), -1);
                        }
                        new DeleteVersionTask().execute(version);
                    }
                }
            }
        };
    }

    private class DeleteVersionTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            return new VersionDataSource(getContext()).deleteDownloadedBookContent((VersionModel) params[0]);
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            reload();
        }
    }

    private View.OnClickListener getSelectionOnClickListener(final VersionModel version) {

        return new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (version instanceof VersionModel) {
                    boolean isStoryChapter = version.getParent(getContext()).getParent(getContext()).slug.equalsIgnoreCase(STORIES_SLUG);
                    refreshChapterSelection(version, isStoryChapter);
                    if(isStoryChapter){
                        UWPreferenceManager.setSelectedStoryVersion(getContext(), version.uid);
                    }
                    else {
                        UWPreferenceManager.setSelectedBibleVersion(getContext(), version.uid);
                    }
                }
            ((VersionSelectionFragment) parentFragment).rowSelected();
            }
        };
    }


    private void refreshChapterSelection(VersionModel version, boolean isStoryChapter){

        if(isStoryChapter){

            String chapterId = UWPreferenceManager.getSelectedStoryChapter(getContext());
            if(Long.parseLong(chapterId) < 0){

                StoriesChapterModel newChapter = version.getChildModels(getContext()).get(0).getStoryChapter(getContext(), 1);
                UWPreferenceManager.setSelectedStoryChapter(getContext(), newChapter.uid);
            }
            else {
                StoriesChapterModel chapter = new StoriesChapterDataSource(getContext()).getModel(chapterId);
                if(chapter == null){
                    StoriesChapterModel newChapter = version.getChildModels(getContext()).get(0).getStoryChapter(getContext(), 1);
                    UWPreferenceManager.setSelectedStoryChapter(getContext(), newChapter.uid);
                    return;
                }
                BookModel newBook = version.findBookForJsonSlug(getContext(), chapter.getParent(getContext()).slug.substring(0, 3));

                StoriesChapterModel newChapter = (newBook == null)? version.getChildModels(getContext()).get(0).getStoryChapter(getContext(), 1) :
                        newBook.getStoryChapter(getContext(), Integer.parseInt(chapter.number));

                UWPreferenceManager.setSelectedStoryChapter(getContext(), newChapter.uid);
            }
        }
        else{
            String chapterId = UWPreferenceManager.getSelectedBibleChapter(getContext());
            if(Long.parseLong(chapterId) < 0){
                BibleChapterModel newChapter = version.getChildModels(getContext()).get(0).getBibleChapter(getContext(), 1);
                UWPreferenceManager.setSelectedBibleChapter(getContext(), newChapter.uid);
            }
            else {
                BibleChapterModel chapter = new BibleChapterDataSource(getContext()).getModel(chapterId);
                if(chapter == null){
                    BibleChapterModel newChapter = version.getChildModels(getContext()).get(0).getBibleChapter(getContext(), 1);
                    UWPreferenceManager.setSelectedBibleChapter(getContext(), newChapter.uid);
                    return;
                }
                BookModel newBook = version.findBookForJsonSlug(getContext(), chapter.getParent(getContext()).slug.substring(0, 3));

                BibleChapterModel newChapter = (newBook == null)? version.getChildModels(getContext()).get(0).getBibleChapter(getContext(), 1) :
                        newBook.getBibleChapter(getContext(), Integer.parseInt(chapter.number.trim()));

                UWPreferenceManager.setSelectedBibleChapter(getContext(), newChapter.uid);
            }
        }

    }

    @Override
    public int getChildrenCount(int groupPosition) {
        int count = getGroup(groupPosition).getChildModels(getContext()).size();
        return count;
    }

    @Override
    public LanguageModel getGroup(int groupPosition) {
        LanguageModel model = currentProject.getChildModels(getContext()).get(groupPosition);
        return model;
    }

    @Override
    public int getGroupCount() {
        int groupCount = currentProject.getChildModels(getContext()).size();
        return groupCount;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        LanguageModel language = getGroup(groupPosition);
        language.getChildModels(getContext());
        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_group,
                    null);
        }

        TextView item = (TextView) convertView.findViewById(R.id.group_title);
        item.setTypeface(null, Typeface.BOLD);
        LanguageLocaleModel languageLocale = new LanguageLocaleDataSource(getContext()).getModelForSlug(language.languageAbbreviation);
        item.setText(languageLocale.languageName);

        return convertView;
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


    private View.OnClickListener getInfoClickListener(final ViewHolderForGroup finalHolder, final VersionModel version){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (finalHolder.visibleFrameLayout.getVisibility() == View.GONE) {
                    finalHolder.verificationTextView.setText(getVerificationText(version));

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
            case 3:
                return getContext().getResources().getString(R.string.failed_button_char);
            default:
                return getContext().getResources().getString(R.string.unknown_button_char);
        }
    }

    private int getColorForStatus(int currentStatus){

        switch (currentStatus){
            case 0:
                return R.drawable.green_checkmark;
            case 1:
                return R.drawable.yellow_exclamation_point;
            case 3:
                return R.drawable.red_x_button;
            default:
                return R.drawable.orange_question_mark;
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
        TextView checkingLevelTextView;
        TextView versionTextView;
        TextView publishDateTextView;
        TextView checkingEntityConstantTextView;
        TextView checkingLevelConstantTextView;
        TextView versionConstantTextView;
        TextView publishDateConstantTextView;
        TextView verificationTextView;
        LinearLayout clickableLayout;
        FrameLayout infoFrame;
        Button status;

        ImageView downloadImageView;
        FrameLayout downloadFrame;
        ProgressBar downloadProgressBar;
    }
}

