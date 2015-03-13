package adapters.selectionAdapters;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.unfoldingword.mobile.R;

import java.util.List;

import activity.bookSelection.ChapterSelectionActivity;
import activity.bookSelection.GeneralSelectionActivity;
import model.modelClasses.mainData.VersionModel;
import utils.CustomSlideAnimationRelativeLayout;

/**
 * Created by Fechner on 3/6/15.
 */
public class VersionAdapter extends GeneralAdapter {


    public VersionAdapter(Context context, List<GeneralRowInterface> models, TextView actionbarTextView, ActionBarActivity activity, String storageString) {
        super(context, R.layout.row_version_selector, models, actionbarTextView, activity, storageString);
    }

    @Override
    public View getView(final int pos, View view, ViewGroup parent) {

        ViewHolderForGroup holder = null;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.row_version_selector, parent, false);
            holder = new ViewHolderForGroup();
            holder.languageNameTextView = (TextView) view.findViewById(R.id.languageNameTextView);
            holder.languageTypeImageView = (ImageView) view.findViewById(R.id.languageTypeImageView);
            holder.clickLanguageImageView = (ImageView) view.findViewById(R.id.version_info_image);
            holder.visibleFrameLayout = (FrameLayout) view.findViewById(R.id.visibleLayout);
            holder.checkingEntityTextView = (TextView) view.findViewById(R.id.checkingEntitytextView);
            holder.checkingLevelTextView = (TextView) view.findViewById(R.id.checkingLevelTextView);
            holder.versionTextView = (TextView) view.findViewById(R.id.versionTextView);
            holder.publishDateTextView = (TextView) view.findViewById(R.id.publishDateTextView);
            holder.checkingEntiityConstantTextView = (TextView) view.findViewById(R.id.checkingEntityConstanttextView);
            holder.checkinglevelConstantTextView = (TextView) view.findViewById(R.id.checkingLevelConstanttextView);
            holder.versionConstantTextView = (TextView) view.findViewById(R.id.versionConstanttextView);
            holder.publishDateConstantTextView = (TextView) view.findViewById(R.id.publishDateConstanttextView);
            holder.clickableLayout = (LinearLayout) view.findViewById(R.id.clickableRow);
            holder.infoFrame = (FrameLayout) view.findViewById(R.id.info_image_frame);

                holder.clickableLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(SELECTED_POS, pos).commit();
//                    context.startActivity(new Intent(context, ChapterSelectionActivity.class).putExtra(LanguageChooserActivity.LANGUAGE_CODE, models.get(pos).language));

                        Object itemAtPosition = models.get(pos);
                        if (itemAtPosition instanceof GeneralRowInterface) {
                            GeneralRowInterface model = (GeneralRowInterface) itemAtPosition;

                            // put selected position  to sharedprefences
                            PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(SELECTED_POS, pos).commit();
                            context.startActivity(new Intent(context, ChapterSelectionActivity.class).putExtra(
                                    GeneralSelectionActivity.CHOSEN_ID, model.getChildIdentifier()));
                            activity.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_on_left);
                        }


//                    activity.overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
//                    activity.finish();
                    }
                });

            final ViewHolderForGroup finalHolder = holder;
            holder.clickLanguageImageView.setOnClickListener(getInfoClickListener(finalHolder));
            holder.infoFrame.setOnClickListener(getInfoClickListener(finalHolder));
            view.setTag(holder);
        } else {
            holder = (ViewHolderForGroup) view.getTag();
        }

        int selectionPosition = PreferenceManager.getDefaultSharedPreferences(context).getInt(SELECTED_POS, -1);
        setColorChange(holder, getColorForState(selectionPosition, pos));

        if (((VersionModel) models.get(pos)).status.checkingLevel.equals("1")) {
            holder.languageTypeImageView.setImageResource(R.drawable.level_one_dark);
        } else if (((VersionModel) models.get(pos)).status.checkingLevel.equals("2")) {
            holder.languageTypeImageView.setImageResource(R.drawable.level_two_dark);
        } else if (((VersionModel) models.get(pos)).status.checkingLevel.equals("3")) {
            holder.languageTypeImageView.setImageResource(R.drawable.level_three_dark);
        }

//        holder.languageTypeImageView.set
        holder.languageNameTextView.setText(models.get(pos).getTitle());
        holder.checkingEntityTextView.setText(((VersionModel) models.get(pos)).status.checkingEntity);
        holder.checkingEntityTextView.setText(((VersionModel) models.get(pos)).status.checkingEntity);
        holder.checkingLevelTextView.setText(((VersionModel) models.get(pos)).status.checkingLevel);
        holder.versionTextView.setText(((VersionModel) models.get(pos)).status.version);
        holder.publishDateTextView.setText(((VersionModel) models.get(pos)).status.publishDate);

        return view;
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

    public void setColorChange(ViewHolderForGroup holder, int color) {

        holder.languageNameTextView.setTextColor(color);
    }

    private static class ViewHolderForGroup {

        TextView languageNameTextView;
        ImageView languageTypeImageView;
        ImageView clickLanguageImageView;
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
    }


}
