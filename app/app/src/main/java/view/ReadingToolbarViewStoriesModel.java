/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package view;

import android.content.Context;

import eventbusmodels.StoriesPagingEvent;
import model.daoModels.StoryPage;

/**
 * Created by Fechner on 10/7/15.
 */
public class ReadingToolbarViewStoriesModel implements ReadingToolbarViewData {

    private String mainVersionText;
    private String secondaryVersionText;
    private String titleText;

    public ReadingToolbarViewStoriesModel(StoryPage currentPage, StoryPage secondaryPage) {
        setup(currentPage, secondaryPage);
    }

    public ReadingToolbarViewStoriesModel(Context context) {
        setup(context);
    }

    private void setup(Context context){

        StoriesPagingEvent event = StoriesPagingEvent.getStickyEvent(context);
        setup(event.mainStoryPage, event.secondaryStoryPage);
    }

    private void setup(StoryPage currentPage, StoryPage secondaryPage){
        mainVersionText = (currentPage != null)? currentPage.getStoriesChapter().getBook().getVersion().getTitle() : "";
        secondaryVersionText = (secondaryPage != null)? secondaryPage.getStoriesChapter().getBook().getVersion().getTitle() : "";
        titleText = (currentPage != null)? currentPage.getStoriesChapter().getTitle() : "";
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
