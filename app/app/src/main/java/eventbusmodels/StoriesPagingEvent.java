package eventbusmodels;

import android.content.Context;

import de.greenrobot.event.EventBus;
import model.daoModels.StoryPage;
import utils.UWPreferenceDataAccessor;

/**
 * Created by Fechner on 12/28/15.
 */
public class StoriesPagingEvent {

    public final StoryPage mainStoryPage;
    public final StoryPage secondaryStoryPage;

    public StoriesPagingEvent(StoryPage mainStoryPage, StoryPage secondaryStoryPage) {
        this.mainStoryPage = mainStoryPage;
        this.secondaryStoryPage = secondaryStoryPage;
    }

    public static StoriesPagingEvent getStickyEvent(Context context){
        StoriesPagingEvent event = EventBus.getDefault().getStickyEvent(StoriesPagingEvent.class);
        if(event == null){
            event = UWPreferenceDataAccessor.getSharedInstance(context).createStoriesPagingEvent();
            EventBus.getDefault().postSticky(event);
            return getStickyEvent(context);
        }
        else{
            return event;
        }
    }
}
