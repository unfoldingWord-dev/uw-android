package eventbusmodels;

import android.content.Context;

import de.greenrobot.event.EventBus;
import model.daoModels.BibleChapter;
import model.daoModels.StoryPage;
import utils.UWPreferenceDataAccessor;

/**
 * Created by Fechner on 12/28/15.
 */
public class BiblePagingEvent {

    public final BibleChapter mainChapter;
    public final BibleChapter secondaryChapter;

    public BiblePagingEvent(BibleChapter mainChapter, BibleChapter secondaryChapter) {
        this.mainChapter = mainChapter;
        this.secondaryChapter = secondaryChapter;
    }

    public static BiblePagingEvent getStickyEvent(Context context){

        BiblePagingEvent event = EventBus.getDefault().getStickyEvent(BiblePagingEvent.class);
        if(event == null){
            event = UWPreferenceDataAccessor.getSharedInstance(context).createBiblePagingEvent();
            EventBus.getDefault().postSticky(event);
            return getStickyEvent(context);
        }
        else{
            return event;
        }

    }
}
