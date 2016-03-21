/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package view;

import android.content.Context;

import eventbusmodels.BiblePagingEvent;
import model.daoModels.BibleChapter;

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

        BiblePagingEvent event = BiblePagingEvent.getStickyEvent(context);
        setup(event.mainChapter, event.secondaryChapter);
    }

    private void setup(BibleChapter currentChapter, BibleChapter secondaryChapter){

        mainVersionText = (currentChapter != null)? currentChapter.getBook().getVersion().getTitle() : "";
        secondaryVersionText = (currentChapter != null)? currentChapter.getBook().getVersion().getTitle() : "";
        titleText = (currentChapter != null)? currentChapter.getTitle() : "";
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
