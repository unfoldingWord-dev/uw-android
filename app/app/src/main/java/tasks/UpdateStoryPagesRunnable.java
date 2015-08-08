package tasks;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import model.DaoDBHelper;
import model.UWDatabaseModel;
import model.daoModels.StoriesChapter;
import model.daoModels.StoryPage;
import model.daoModels.StoryPageDao;
import services.UWUpdaterService;

/**
 * Created by Fechner on 6/17/15.
 */
public class UpdateStoryPagesRunnable implements Runnable{

    private static final String TAG = "UpdateStoryPgsRunnable";

    private JSONArray jsonModels;
    private UWUpdaterService updater;
    private StoriesChapter parent;

    public UpdateStoryPagesRunnable(JSONArray jsonModels, UWUpdaterService updater, StoriesChapter parent) {
        this.jsonModels = jsonModels;
        this.updater = updater;
        this.parent = parent;
    }

    @Override
    public void run() {

        parseModels(jsonModels);

    }
    private void parseModels(JSONArray models){

        List<StoryPage> pages = new ArrayList<StoryPage>();

        for(int i = 0; i < models.length(); i++){

            try {
                pages.add(updateModel(models.getJSONObject(i)));
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }
        updatePages(pages);
        updater.runnableFinished();
    }

    private StoryPage updateModel(final JSONObject jsonObject){

        UWDatabaseModel model = new ModelCreation(new StoryPage(), parent).start(jsonObject);

        if(model instanceof StoryPage) {
            return (StoryPage) model;
        }
        else{
            return null;
        }
    }

    private void updatePages(List<StoryPage> pages){
        StoryPageDao dao = DaoDBHelper.getDaoSession(updater.getApplicationContext()).getStoryPageDao();
        dao.queryBuilder()
                .where(StoryPageDao.Properties.StoryChapterId.eq(parent.getId()))
                .buildDelete().executeDeleteWithoutDetachingEntities();
        dao.insertOrReplaceInTx(pages);
    }
}
