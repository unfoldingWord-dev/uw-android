package tasks;

import org.json.JSONException;

import java.nio.charset.CharacterCodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import model.DaoDBHelper;
import model.daoModels.BibleChapter;
import model.daoModels.BibleChapterDao;
import model.daoModels.Book;
import model.parsers.BibleChapterParser;
import model.parsers.USFMParser;
import services.UWUpdaterService;

/**
 * Created by Fechner on 6/17/15.
 */
public class UpdateBibleChaptersRunnable implements Runnable{

    private static final String TAG = "UpdateBblChaptsRunnable";

    private byte[] usfm;
    private UWUpdaterService updater;
    private Book parent;

    public UpdateBibleChaptersRunnable(byte[] usfm, UWUpdaterService updater, Book parent) {
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
            createModels(parsedUsfm);
        }
        catch (CharacterCodingException e){
            e.printStackTrace();
        }
    }


    private void createModels(Map<String, String> models){

        List<BibleChapter> chapters = new ArrayList<BibleChapter>();
        int i = 0;
        for(Map.Entry<String, String> entry : models.entrySet()){

            try {
                chapters.add(BibleChapterParser.parseBibleChapter(parent, entry.getKey(), entry.getValue()));

            }
            catch (JSONException e){
                e.printStackTrace();
            }
            i++;
        }
        updateModels(chapters);
        updater.runnableFinished();
    }

    private void updateModels(List<BibleChapter> chapters){

        BibleChapterDao dao = DaoDBHelper.getDaoSession(updater.getApplicationContext()).getBibleChapterDao();
        dao.queryBuilder()
                .where(BibleChapterDao.Properties.BookId.eq(parent.getId()))
                .buildDelete().executeDeleteWithoutDetachingEntities();
        dao.insertOrReplaceInTx(chapters);
    }
}
