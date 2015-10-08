package view;

import android.content.Context;

import model.daoModels.BibleChapter;
import model.daoModels.StoryPage;
import utils.UWPreferenceDataAccessor;

/**
 * Created by Fechner on 10/7/15.
 */
public class ReadingToolbarViewBibleModel implements ReadingToolbarViewData {

    private String mainVersionText;
    private String secondaryVersionText;
    private String titleText;

    public ReadingToolbarViewBibleModel(BibleChapter currentChapter, BibleChapter secondaryChapter) {
        setup(currentChapter, secondaryChapter);
    }

    public ReadingToolbarViewBibleModel(Context context) {
        setup(context);
    }

    private void setup(Context context){

        BibleChapter currentChapter = UWPreferenceDataAccessor.getCurrentBibleChapter(context, false);
        BibleChapter secondaryChapter = UWPreferenceDataAccessor.getCurrentBibleChapter(context, true);
        setup(currentChapter, secondaryChapter);
    }

    private void setup(BibleChapter currentChapter, BibleChapter secondaryChapter){

        mainVersionText = (currentChapter != null)? currentChapter.getBook().getVersion().getTitle() : "";
        secondaryVersionText = (currentChapter != null)? currentChapter.getBook().getVersion().getTitle() : "";
        titleText = (secondaryChapter != null)? secondaryChapter.getTitle() : "";
    }

    @Override
    public String getMainVersionText() {
        return mainVersionText;
    }

    @Override
    public String getSecondaryVersionText() {
        return secondaryVersionText;
    }

    @Override
    public String getTitleText() {
        return titleText;
    }
}
