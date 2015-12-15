/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package runnables;

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
import model.parsers.MediaType;
import tasks.ModelCreator;
import services.UWUpdaterService;

/**
 * Created by PJ Fechner on 6/17/15.
 * Runnable for updating OBS Pages
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

        List<StoryPage> pages = new ArrayList<>();

        for(int i = 0; i < models.length(); i++){

            try {
                pages.add(updateModel(models.getJSONObject(i)));
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }
        updatePages(pages);
        parent.getBook().getVersion().update();
        updater.runnableFinished(parent.getBook().getVersion(), MediaType.MEDIA_TYPE_TEXT);
    }

    private StoryPage updateModel(final JSONObject jsonObject){

        UWDatabaseModel model = new ModelCreator(new StoryPage(), parent, null).run(jsonObject);

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
