package adapter;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.unfoldingword.mobile.R;

import java.util.List;

import activity.ChapterSelectionActivity;
import activity.LanguageChooserActivity;
import models.LanguageModel;
import utils.CustomSlideAnimationRelativeLayout;

/**
 * Created by Acts Media Inc on 4/12/14.
 */
public class LanguageListAdapter extends ArrayAdapter<LanguageModel> {
    private static final String SELECTED_POS = "SELECTED_POS";
    private final List<LanguageModel> models;
    private final ActionBarActivity activity;
    private TextView actionbarTextview;
    private Context context;

    public LanguageListAdapter(Context context, List<LanguageModel> models, TextView actionbarTextView, ActionBarActivity activity) {
        super(context, R.layout.row_language_chooser_group, models);
        this.context = context;
        this.models = models;
        this.actionbarTextview = actionbarTextView;
        this.activity = activity;
    }

    @Override
    public View getView(final int pos, View view, ViewGroup parent) {
        ViewHolderForGroup holder = null;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.row_language_chooser_group, parent, false);
            holder = new ViewHolderForGroup();
            holder.languageNameTextView = (TextView) view.findViewById(R.id.languageNameTextView);
            holder.languageTypeImageView = (ImageView) view.findViewById(R.id.languageTypeImageView);
            holder.clickLanguageImageView = (ImageView) view.findViewById(R.id.goFrameClickImageView);
            holder.visibleFrameLayout = (FrameLayout) view.findViewById(R.id.visibleLayout);
            holder.checkingEntityTextView = (TextView) view.findViewById(R.id.checkingEntitytextView);
            holder.checkingLevelTextView = (TextView) view.findViewById(R.id.checkingLevelTextView);
            holder.versionTextView = (TextView) view.findViewById(R.id.versionTextView);
            holder.publishDateTextView = (TextView) view.findViewById(R.id.publishDateTextView);
            holder.checkingEntiityConstantTextView = (TextView) view.findViewById(R.id.checkingEntityConstanttextView);
            holder.checkinglevelConstantTextView = (TextView) view.findViewById(R.id.checkingLevelConstanttextView);
            holder.versionConstantTextView = (TextView) view.findViewById(R.id.versionConstanttextView);
            holder.publishDateConstantTextView = (TextView) view.findViewById(R.id.publishDateConstanttextView);
            holder.clickeblbleLayout = (LinearLayout) view.findViewById(R.id.cleckebleLayout);

            holder.clickeblbleLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(SELECTED_POS, pos).commit();
                    context.startActivity(new Intent(context, ChapterSelectionActivity.class).putExtra(LanguageChooserActivity.LANGUAGE_CODE, models.get(pos).language));
                    activity.overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
                    activity.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_on_left);
//                    activity.finish();
                }
            });

            final ViewHolderForGroup finalHolder = holder;
            holder.clickLanguageImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (finalHolder.visibleFrameLayout.getVisibility() == View.GONE) {

                        CustomSlideAnimationRelativeLayout animationRelativeLayout = new CustomSlideAnimationRelativeLayout(finalHolder.visibleFrameLayout, 500, CustomSlideAnimationRelativeLayout.EXPAND);
                        finalHolder.visibleFrameLayout.startAnimation(animationRelativeLayout);
//                context.startActivity(new Intent(context, ChapterSelectionActivity.class).putExtra(LanguageChooserActivity.LANGUAGE_CODE, list.get(pos).language));
                    } else {
                        CustomSlideAnimationRelativeLayout animationRelativeLayout = new CustomSlideAnimationRelativeLayout(finalHolder.visibleFrameLayout, 500, CustomSlideAnimationRelativeLayout.COLLAPSE);
                        finalHolder.visibleFrameLayout.startAnimation(animationRelativeLayout);

                    }

                }
            });
            view.setTag(holder);
        } else {
            holder = (ViewHolderForGroup) view.getTag();
        }
        int selected = PreferenceManager.getDefaultSharedPreferences(context).getInt(SELECTED_POS, -1);
        if (selected != -1) {
            if (pos == selected)
                setColorChange(holder, context.getResources().getColor(R.color.cyan));
            else
                setColorChange(holder, context.getResources().getColor(R.color.black_light));

        } else {
            if (pos == 0)
                setColorChange(holder, context.getResources().getColor(R.color.cyan));
            else
                setColorChange(holder, context.getResources().getColor(R.color.black_light));
        }
        if (models.get(pos).checkingLevel.equals("1")) {
            holder.languageTypeImageView.setImageResource(R.drawable.level_one_dark);
        } else if (models.get(pos).checkingLevel.equals("2")) {
            holder.languageTypeImageView.setImageResource(R.drawable.level_two_dark);
        } else if (models.get(pos).checkingLevel.equals("3")) {
            holder.languageTypeImageView.setImageResource(R.drawable.level_three_dark);
        }
        holder.languageNameTextView.setText(models.get(pos).languageName + " [" + models.get(pos).language + "]");
        holder.checkingEntityTextView.setText(models.get(pos).checkingEntity);
        holder.checkingEntityTextView.setText(models.get(pos).checkingEntity);
        holder.checkingLevelTextView.setText(models.get(pos).checkingLevel);
        holder.versionTextView.setText(models.get(pos).version);
        holder.publishDateTextView.setText(models.get(pos).publishDate);


        return view;
    }

    public void setColorChange(ViewHolderForGroup holder, int color) {
        holder.languageNameTextView.setTextColor(color);
        holder.checkingEntityTextView.setTextColor(color);
        holder.checkingEntityTextView.setTextColor(color);
        holder.checkingLevelTextView.setTextColor(color);
        holder.versionTextView.setTextColor(color);
        holder.publishDateTextView.setTextColor(color);

        //
        holder.checkingEntiityConstantTextView.setTextColor(color);
        holder.checkinglevelConstantTextView.setTextColor(color);
        holder.versionConstantTextView.setTextColor(color);
        holder.publishDateConstantTextView.setTextColor(color);
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
        LinearLayout clickeblbleLayout;
    }
}
