package adapters.selectionAdapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.unfoldingword.mobile.R;

import java.util.Locale;

import model.datasource.BibleChapterDataSource;
import model.datasource.ProjectDataSource;
import model.datasource.StoriesChapterDataSource;
import model.datasource.VersionDataSource;
import model.modelClasses.mainData.BibleChapterModel;
import model.modelClasses.mainData.BookModel;
import model.modelClasses.mainData.LanguageModel;
import model.modelClasses.mainData.ProjectModel;
import model.modelClasses.mainData.StoriesChapterModel;
import model.modelClasses.mainData.VersionModel;
import services.VersionDownloadService;
import utils.CustomSlideAnimationRelativeLayout;
import utils.NetWorkUtil;
import utils.URLUtils;
import utils.UWPreferenceManager;

/**
 * Created by Fechner on 3/23/15.
 */
public class CollapsibleVersionAdapter extends BaseExpandableListAdapter {

    private final static String TAG = "CollapseVersionAdapter";
    protected final static String SELECTED_POS = "VERSION_POSITION";

    static final String STORIES_SLUG = "obs";
    private Activity context;
    ProjectModel currentProject = null;

    public CollapsibleVersionAdapter(Activity context, ProjectModel currentProject) {
        this.context = context;
        this.currentProject = currentProject;

        IntentFilter filter = new IntentFilter();
        filter.addAction(URLUtils.VERSION_BROADCAST_DOWN_COMP);
        filter.addAction(URLUtils.VERSION_BROADCAST_DOWN_ERROR);
        context.registerReceiver(receiver, filter);
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

        currentProject = new ProjectDataSource(context).getModel(Long.toString(currentProject.uid));
        notifyDataSetChanged();
    }

    public VersionModel getChild(int groupPosition, int childPosition) {
        return currentProject.getChildModels(context).get(groupPosition).getChildModels(context).get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final VersionModel version = getChild(groupPosition, childPosition);
        ViewHolderForGroup holder = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_version_selector, parent, false);
            holder = new ViewHolderForGroup();
            holder.languageNameTextView = (TextView) convertView.findViewById(R.id.languageNameTextView);
            holder.languageTypeImageView = (ImageView) convertView.findViewById(R.id.languageTypeImageView);
            holder.visibleFrameLayout = (FrameLayout) convertView.findViewById(R.id.visibleLayout);
            holder.checkingEntityTextView = (TextView) convertView.findViewById(R.id.checkingEntitytextView);
            holder.checkingLevelTextView = (TextView) convertView.findViewById(R.id.checkingLevelTextView);
            holder.versionTextView = (TextView) convertView.findViewById(R.id.versionTextView);
            holder.publishDateTextView = (TextView) convertView.findViewById(R.id.publishDateTextView);
            holder.checkingEntiityConstantTextView = (TextView) convertView.findViewById(R.id.checkingEntityConstanttextView);
            holder.checkinglevelConstantTextView = (TextView) convertView.findViewById(R.id.checkingLevelConstanttextView);
            holder.versionConstantTextView = (TextView) convertView.findViewById(R.id.versionConstanttextView);
            holder.publishDateConstantTextView = (TextView) convertView.findViewById(R.id.publishDateConstanttextView);
            holder.clickableLayout = (LinearLayout) convertView.findViewById(R.id.clickableRow);
            holder.infoFrame = (FrameLayout) convertView.findViewById(R.id.info_image_frame);
            holder.status = (ImageView) convertView.findViewById(R.id.status);

            holder.downloadImageView = (ImageView) convertView.findViewById(R.id.download_status_image);
            holder.downloadFrame = (FrameLayout) convertView.findViewById(R.id.download_status_frame);
            holder.downloadProgressBar = (ProgressBar) convertView.findViewById(R.id.download_progress_bar);

            final ViewHolderForGroup finalHolder = holder;
            holder.infoFrame.setOnClickListener(getInfoClickListener(finalHolder));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolderForGroup) convertView.getTag();
        }

        boolean isSelected = ((version.uid == Long.parseLong(UWPreferenceManager.getSelectedBibleVersion(context)))
                || (version.uid == Long.parseLong(UWPreferenceManager.getSelectedStoryVersion(context))));

        int state = isSelected? 2 : 1;
        holder.downloadProgressBar.setVisibility(View.INVISIBLE);
        holder.downloadImageView.setVisibility(View.VISIBLE);

        state = setRowState(holder, version, state);

        holder.downloadFrame.setOnClickListener(getDownloadOnClickListener(version, holder));
        setColorChange(holder, getColorForState(state));

        if (version.status.checkingLevel.equals("1")) {
            holder.languageTypeImageView.setImageResource(R.drawable.level_one_dark);
        } else if (version.status.checkingLevel.equals("2")) {
            holder.languageTypeImageView.setImageResource(R.drawable.level_two_dark);
        } else if (version.status.checkingLevel.equals("3")) {
            holder.languageTypeImageView.setImageResource(R.drawable.level_three_dark);
        }


//        holder.languageTypeImageView.set
        holder.languageNameTextView.setText(version.getTitle());
        holder.checkingEntityTextView.setText(version.status.checkingEntity);
        holder.checkingEntityTextView.setText(version.status.checkingEntity);
        holder.checkingLevelTextView.setText(version.status.checkingLevel);
        holder.versionTextView.setText(version.status.version);
        holder.publishDateTextView.setText(version.status.publishDate);


        Drawable statusImage = context.getResources().getDrawable(getColorForStatus(version.getVerificationStatus(context)));
        Bitmap statusBitmap = ((BitmapDrawable)statusImage).getBitmap();
        holder.status.setImageBitmap(statusBitmap);

        return convertView;
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
                    finalHolder.downloadImageView.setVisibility(view.INVISIBLE);

                    if((version.downloadState == VersionModel.DOWNLOAD_STATE.DOWNLOAD_STATE_NONE)
                    || (version.downloadState == VersionModel.DOWNLOAD_STATE.DOWNLOAD_STATE_ERROR)) {

                        if (!NetWorkUtil.isConnected(context)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Alert");
                            builder.setMessage("Failed connecting to the internet.");
                            builder.setPositiveButton("OK", null);
                            builder.create().show();
                        } else {
                            // to handle new data from network
                            version.downloadState = VersionModel.DOWNLOAD_STATE.DOWNLOAD_STATE_DOWNLOADING;
                            version.save(context);
                            Intent downloadIntent = new Intent(context, VersionDownloadService.class);
                            downloadIntent.putExtra(VersionDownloadService.VERSION_ID, Long.toString(version.uid));
                            context.startService(downloadIntent);
                        }
                    }
                    else if(version.downloadState == VersionModel.DOWNLOAD_STATE.DOWNLOAD_STATE_DOWNLOADING){
                        context.stopService(new Intent(context, VersionDownloadService.class));
                        new DeleteVersionTask().execute(version);
                    }
                    else{
                        if(Long.parseLong(UWPreferenceManager.getSelectedBibleVersion(context)) == version.uid){
                            UWPreferenceManager.setSelectedBibleVersion(context, -1);
                            UWPreferenceManager.setSelectedBibleChapter(context, -1);
                        }
                        if(Long.parseLong(UWPreferenceManager.getSelectedStoryVersion(context)) == version.uid){
                            UWPreferenceManager.setSelectedStoryVersion(context, -1);
                            UWPreferenceManager.setSelectedStoryChapter(context, -1);
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
            return new VersionDataSource(context).deleteDownloadedBookContent((VersionModel) params[0]);
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
                    boolean isStoryChapter = version.getParent(context).getParent(context).slug.equalsIgnoreCase(STORIES_SLUG);
                    refreshChapterSelection(version, isStoryChapter);
                    if(isStoryChapter){
                        UWPreferenceManager.setSelectedStoryVersion(context, version.uid);
                    }
                    else {
                        UWPreferenceManager.setSelectedBibleVersion(context, version.uid);
                    }
                }
                context.finish();
                context.overridePendingTransition(R.anim.enter_center, R.anim.exit_on_bottom);
            }
        };
    }


    private void refreshChapterSelection(VersionModel version, boolean isStoryChapter){

        if(isStoryChapter){

            String chapterId = UWPreferenceManager.getSelectedStoryChapter(context);
            if(Long.parseLong(chapterId) < 0){

                StoriesChapterModel newChapter = version.getChildModels(context).get(0).getStoryChapter(context, 1);
                UWPreferenceManager.setSelectedStoryChapter(context, newChapter.uid);
            }
            else {
                StoriesChapterModel chapter = new StoriesChapterDataSource(context).getModel(chapterId);
                if(chapter == null){
                    StoriesChapterModel newChapter = version.getChildModels(context).get(0).getStoryChapter(context, 1);
                    UWPreferenceManager.setSelectedStoryChapter(context, newChapter.uid);
                    return;
                }
                BookModel newBook = version.findBookForJsonSlug(context, chapter.getParent(context).slug.substring(0, 3));

                StoriesChapterModel newChapter = (newBook == null)? version.getChildModels(context).get(0).getStoryChapter(context, 1) :
                        newBook.getStoryChapter(context, Integer.parseInt(chapter.number));

                UWPreferenceManager.setSelectedStoryChapter(context, newChapter.uid);
            }
        }
        else{
            String chapterId = UWPreferenceManager.getSelectedBibleChapter(context);
            if(Long.parseLong(chapterId) < 0){
                BibleChapterModel newChapter = version.getChildModels(context).get(0).getBibleChapter(context, 1);
                UWPreferenceManager.setSelectedBibleChapter(context, newChapter.uid);
            }
            else {
                BibleChapterModel chapter = new BibleChapterDataSource(context).getModel(chapterId);
                if(chapter == null){
                    BibleChapterModel newChapter = version.getChildModels(context).get(0).getBibleChapter(context, 1);
                    UWPreferenceManager.setSelectedBibleChapter(context, newChapter.uid);
                    return;
                }
                BookModel newBook = version.findBookForJsonSlug(context, chapter.getParent(context).slug.substring(0, 3));

                BibleChapterModel newChapter = (newBook == null)? version.getChildModels(context).get(0).getBibleChapter(context, 1) :
                        newBook.getBibleChapter(context, Integer.parseInt(chapter.number.trim()));

                UWPreferenceManager.setSelectedBibleChapter(context, newChapter.uid);
            }
        }

    }

    public int getChildrenCount(int groupPosition) {
        return getGroup(groupPosition).getChildModels(context).size();
    }

    public LanguageModel getGroup(int groupPosition) {
        return currentProject.getChildModels(context).get(groupPosition);
    }

    public int getGroupCount() {
        return currentProject.getChildModels(context).size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        LanguageModel language = getGroup(groupPosition);
        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_group,
                    null);
        }

        TextView item = (TextView) convertView.findViewById(R.id.group_title);
        item.setTypeface(null, Typeface.BOLD);
        Locale languageLocal = new Locale(language.getTitle());
        item.setText(languageLocal.getDisplayLanguage());

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

    private int getColorForStatus(int currentStatus){

        switch (currentStatus){
            case 0:
                return R.drawable.status_verified_btn_radio_on_holo_light;
            case 1:
                return R.drawable.status_expired_btn_radio_on_holo_light;
            case 3:
                return R.drawable.status_failed_btn_radio_on_holo_light;
            default:
                return R.drawable.status_error_btn_radio_on_holo_light;
        }
    }

    protected int getColorForState(int state){

        switch (state){
            case 1:
                return context.getResources().getColor(R.color.black_light);
            case 2:
                return context.getResources().getColor(R.color.cyan);
            default:
                return context.getResources().getColor(R.color.lightgrey);
        }
    }


    private View.OnClickListener getInfoClickListener(final ViewHolderForGroup finalHolder){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (finalHolder.visibleFrameLayout.getVisibility() == View.GONE) {

                    CustomSlideAnimationRelativeLayout animationRelativeLayout = new CustomSlideAnimationRelativeLayout(finalHolder.visibleFrameLayout, 300, CustomSlideAnimationRelativeLayout.EXPAND);
                    finalHolder.visibleFrameLayout.startAnimation(animationRelativeLayout);
//                context.startActivity(new Intent(context, ChapterSelectionActivity.class).putExtra(LanguageChooserActivity.LANGUAGE_CODE, list.get(pos).language));
                } else {
                    CustomSlideAnimationRelativeLayout animationRelativeLayout = new CustomSlideAnimationRelativeLayout(finalHolder.visibleFrameLayout, 300, CustomSlideAnimationRelativeLayout.COLLAPSE);
                    finalHolder.visibleFrameLayout.startAnimation(animationRelativeLayout);
                }
            }
        };
    }


    public void willDestroy(){
        if(receiver != null) {
            context.unregisterReceiver(receiver);
        }
        receiver = null;
    }


    private static class ViewHolderForGroup {

        TextView languageNameTextView;
        ImageView languageTypeImageView;
        FrameLayout visibleFrameLayout;
        TextView checkingEntityTextView;
        TextView checkingLevelTextView;
        TextView versionTextView;
        TextView publishDateTextView;
        TextView checkingEntiityConstantTextView;
        TextView checkinglevelConstantTextView;
        TextView versionConstantTextView;
        TextView publishDateConstantTextView;
        LinearLayout clickableLayout;
        FrameLayout infoFrame;
        ImageView status;

        ImageView downloadImageView;
        FrameLayout downloadFrame;
        ProgressBar downloadProgressBar;
    }
}

