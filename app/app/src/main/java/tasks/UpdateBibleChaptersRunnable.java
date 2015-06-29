package tasks;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.CharacterCodingException;
import java.util.Map;

import model.UWDatabaseModel;
import model.daoModels.BibleChapter;
import model.daoModels.Book;
import model.daoModels.DaoSession;
import model.daoModels.StoryPage;
import model.parsers.BibleChapterParser;
import model.parsers.USFMParser;
import services.UWUpdater;

/**
 * Created by Fechner on 6/17/15.
 */
public class UpdateBibleChaptersRunnable implements Runnable{

    private static final String TAG = "UpdateVersionsRunnable";

    private byte[] usfm;
    private UWUpdater updater;
    private Book parent;

    public UpdateBibleChaptersRunnable(byte[] usfm, UWUpdater updater, Book parent) {
        this.usfm = usfm;
        this.updater = updater;
        this.parent = parent;
    }

    @Override
    public void run() {

        parse(usfm);

    }
    private void parse(byte[] textBytes){

        try {
            Map<String, String> parsedUsfm = new USFMParser().getChaptersFromUsfm(textBytes);
        }
        catch (CharacterCodingException e){
            e.printStackTrace();
        }
    }

    private void createModels(Map<String, String> models){

        for(Map.Entry<String, String> entry : models.entrySet()){

            try {
                BibleChapter chapter = BibleChapterParser.parseBibleChapter(parent, entry.getKey(), entry.getValue());


            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    private void updateModel(BibleChapter chapter){

        new BibleChapterSaveOrUpdateTask(updater.getApplicationContext(), new ModelSaveOrUpdateTask.ModelCreationTaskListener(){
            @Override
            public void modelWasUpdated(UWDatabaseModel shouldContinueUpdate) {

            }
        }
        ).execute(chapter);
    }

    private class BibleChapterSaveOrUpdateTask extends ModelSaveOrUpdateTask{

        public BibleChapterSaveOrUpdateTask(Context context, ModelCreationTaskListener listener) {
            super(context, listener);
        }

        @Override
        protected UWDatabaseModel getExistingModel(String slug, DaoSession session) {
            return BibleChapter.getModelForSlug(slug, session);
        }
    }
}
