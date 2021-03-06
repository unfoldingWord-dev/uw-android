/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package runnables;

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
import model.parsers.MediaType;
import model.parsers.USFMParser;
import services.UWUpdaterService;

/**
 * Created by PJ Fechner on 6/17/15.
 *  Runnable which updates a BibleChapter
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
            String optionalSingleChapterBookName = USFMParser.getSingleChapterBookName(USFMParser.getStringFromBytes(textBytes));
            createModels(parsedUsfm, optionalSingleChapterBookName);
        }
        catch (CharacterCodingException e){
            e.printStackTrace();
        }
    }


    private void createModels(Map<String, String> models, String singleChapterBookName){

        List<BibleChapter> chapters = new ArrayList<BibleChapter>();
        int i = 0;
        for(Map.Entry<String, String> entry : models.entrySet()){

            try {
                chapters.add(BibleChapterParser.parseBibleChapter(parent, entry.getKey(), entry.getValue(), singleChapterBookName));
            }
            catch (JSONException e){
                e.printStackTrace();
            }
            i++;
        }
        updateModels(chapters);
        parent.getVersion().update();
        updater.runnableFinished(parent.getVersion(), MediaType.MEDIA_TYPE_TEXT);
    }

    private List<BibleChapter> updateModels(List<BibleChapter> chapters){

        BibleChapterDao dao = DaoDBHelper.getDaoSession(updater.getApplicationContext()).getBibleChapterDao();
        dao.queryBuilder()
                .where(BibleChapterDao.Properties.BookId.eq(parent.getId()))
                .buildDelete().executeDeleteWithoutDetachingEntities();
        dao.insertOrReplaceInTx(chapters);
        return chapters;
    }
}
