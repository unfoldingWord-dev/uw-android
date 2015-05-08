package model.database;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import model.datasource.AMDatabase.AMDatabaseDataSourceAbstract;
import model.datasource.BibleChapterDataSource;
import model.datasource.BookDataSource;
import model.datasource.LanguageDataSource;
import model.datasource.LanguageLocaleDataSource;
import model.datasource.PageDataSource;
import model.datasource.ProjectDataSource;
import model.datasource.StoriesChapterDataSource;
import model.datasource.VersionDataSource;
import model.modelClasses.AMDatabase.AMDatabaseModelAbstractObject;
import model.modelClasses.mainData.BibleChapterModel;
import model.modelClasses.mainData.BookModel;
import model.modelClasses.mainData.LanguageModel;
import model.modelClasses.mainData.PageModel;
import model.modelClasses.mainData.ProjectModel;
import model.modelClasses.mainData.StoriesChapterModel;
import model.modelClasses.mainData.VersionModel;
import signing.Status;
import signing.UWSigning;
import utils.URLDownloadUtil;
import utils.USFMParser;
import utils.UWPreferenceManager;

/**
 * Created by Fechner on 3/15/15.
 */
public class UWDataParser {

    public static final String LAST_MODIFIED_JSON_KEY = "mod";
    public static final String PROJECTS_JSON_KEY = "cat";
    public static final String LANGUAGES_JSON_KEY = "langs";
    private static final String VERSIONS_JSON_KEY = "vers";
    private static final String BOOKS_JSON_KEY = "toc";
    private static final String CHAPTERS_JSON_KEY = "chapters";

    private static UWDataParser ourInstance = null;

    public static UWDataParser getInstance(Context context) {

        if(ourInstance == null){
            ourInstance = new UWDataParser(context);
        }
        return ourInstance;
    }

    private Context context;

    private UWDataParser(Context context) {

        this.context = context;
    }

    public String getSideLoadedDBAsJson(){

        ArrayList<ProjectModel> projects = new ProjectDataSource(context).getAllProjects();
        String json = "{\n[";

        for(ProjectModel project : projects){

            json += "\n" + project.getAsSideLoadedModel(context) + ",\n";
        }

        json = json.substring(0, json.length() - 1);
        json += "\n]\n}";

        return json;
    }
    public JSONArray downloadJsonArray(String url) throws JSONException, IOException{

        String json = URLDownloadUtil.downloadString(url);

        JSONArray jsonArray = new JSONArray(json);
        return jsonArray;
    }

    public JSONObject downloadJsonObject(String url) throws JSONException, IOException{

        String json = URLDownloadUtil.downloadString(url);

        JSONObject jsonObject = new JSONObject(json);
        return jsonObject;
    }

    public AMDatabaseModelAbstractObject updateModelFromJson(AMDatabaseDataSourceAbstract dataSource, JSONObject json, long parentId, boolean sideLoaded){

        AMDatabaseModelAbstractObject model = dataSource.saveOrUpdateModel(json, parentId, sideLoaded);
        return model;
    }

    public void updateProjects(String url, boolean sideLoaded) throws IOException, JSONException {

        JSONObject jsonObject = downloadJsonObject(url);
        long jsonUpdated = jsonObject.getLong(LAST_MODIFIED_JSON_KEY);
        long currentUpdated = UWPreferenceManager.getLastUpdatedDate(context);
        if(jsonUpdated > currentUpdated) {
            updateProjects(jsonObject.getJSONArray(PROJECTS_JSON_KEY), sideLoaded);
            UWPreferenceManager.setLastUpdatedDate(context, jsonUpdated);
        }
    }

    public void updateProjects(JSONArray jsonArray, boolean sideLoaded) throws IOException, JSONException{

        for(int i = 0; i < jsonArray.length(); i++){

            JSONObject jsonObj = jsonArray.getJSONObject(i);
            ProjectModel updatedModel = (ProjectModel) updateModelFromJson(new ProjectDataSource(context), jsonObj, -1, sideLoaded);

            if(updatedModel == null){
                continue;
            }
            else{
                updateLanguages(jsonObj.getJSONArray(LANGUAGES_JSON_KEY), updatedModel.uid, sideLoaded);
            }
        }
    }

    public void updateLanguages(JSONArray jsonArray, long parentId, boolean sideLoaded) throws IOException, JSONException{


        for(int i = 0; i < jsonArray.length(); i++){

            JSONObject jsonObj = jsonArray.getJSONObject(i);
            LanguageModel updatedModel = (LanguageModel) updateModelFromJson(new LanguageDataSource(context), jsonObj, parentId, sideLoaded);

            if(updatedModel == null){
                continue;
            }
            else{
                updateVersions(jsonObj.getJSONArray(VERSIONS_JSON_KEY), updatedModel.uid, sideLoaded);
            }
        }
    }

    public void updateVersions(JSONArray jsonArray, long parentId, boolean sideLoaded) throws IOException, JSONException{

        for(int i = 0; i < jsonArray.length(); i++){

            JSONObject jsonObj = jsonArray.getJSONObject(i);
            VersionModel updatedModel = (VersionModel) updateModelFromJson(new VersionDataSource(context), jsonObj, parentId, sideLoaded);

            if(updatedModel == null){
                continue;
            }
            // update the books
            updateBooks(jsonObj.getJSONArray(BOOKS_JSON_KEY), updatedModel.uid, sideLoaded);
            //update the verification
            updatedModel = updateVersionVerificationStatus(updatedModel);
            updatedModel.getDataSource(context).saveModel(updatedModel);
        }
    }

    public VersionModel updateVersionVerificationStatus(VersionModel model){

        model.verificationStatus = -1;
        model.getVerificationStatus(context);
        return model;
    }

    public void updateBooks(JSONArray jsonArray, long parentId, boolean sideLoaded) throws IOException, JSONException{

        for(int i = 0; i < jsonArray.length(); i++){

            JSONObject jsonObj = jsonArray.getJSONObject(i);
            BookModel updatedModel = (BookModel) updateModelFromJson(new BookDataSource(context), jsonObj, parentId, sideLoaded);

            if(updatedModel.getBibleChildModels(context).size() > 0){
                updateUSFMForBook(updatedModel);
            }
            else if(updatedModel.getStoryChildModels(context).size() > 0){
                updateStoryChapters(updatedModel, sideLoaded);
            }
        }
    }

    public void updateUSFMForBook(BookModel book) throws IOException, JSONException{

        byte[] usfmText = URLDownloadUtil.downloadBytes(book.sourceUrl);
        try {
            UWSigning.addAndVerifySignatureForBook(context, book, usfmText);
        }
        catch (IOException e){
            e.printStackTrace();
        }

        Map<String, String> usfmMap = USFMParser.getInstance().getChaptersFromUsfm(usfmText);
        ArrayList<BibleChapterModel> chapters = book.getBibleChildModels(context);

        for (Map.Entry<String, String> entry : usfmMap.entrySet()){
            BibleChapterModel chapter = new BibleChapterModel();
            chapter.parentId = book.uid;
            chapter.number = entry.getKey();
            chapter.text = entry.getValue();

            for(BibleChapterModel oldChapter : chapters){
                if(Long.parseLong(oldChapter.number.replaceAll("[^0-9]", "")) == Long.parseLong(chapter.number.replaceAll("[^0-9]", ""))){
                    chapter.uid = oldChapter.uid;
                }
            }

            new BibleChapterDataSource(context).saveModel(chapter);
        }
    }

    public void updateStoryChapters(BookModel bookModel, boolean sideLoaded) throws IOException, JSONException{

        byte[] jsonBytes = URLDownloadUtil.downloadBytes(bookModel.sourceUrl);

        UWSigning.addAndVerifySignatureForBook(context, bookModel, jsonBytes);

        String json = new String(jsonBytes);
        JSONObject book = new JSONObject(json);
        updateStoryChapters(book.getJSONArray(CHAPTERS_JSON_KEY), bookModel.uid, sideLoaded);
    }



    public void updateStoryChapters(JSONArray jsonArray, long parentId, boolean sideLoaded) throws IOException, JSONException{

        for(int i = 0; i < jsonArray.length(); i++){

            JSONObject jsonObj = jsonArray.getJSONObject(i);
            StoriesChapterModel updatedModel = (StoriesChapterModel) updateModelFromJson(new StoriesChapterDataSource(context), jsonObj, parentId, sideLoaded);

            if(updatedModel == null){
                continue;
            }
            else{
                updatePages(jsonObj.getJSONArray("frames"), updatedModel.uid, sideLoaded);
            }
        }
    }

    public void updatePages(JSONArray jsonArray, long parentId, boolean sideLoaded) throws IOException, JSONException{

        for(int i = 0; i < jsonArray.length(); i++){

            JSONObject jsonObj = jsonArray.getJSONObject(i);
            PageModel updatedModel = (PageModel) updateModelFromJson(new PageDataSource(context), jsonObj, parentId, sideLoaded);
        }
    }

    public void downloadAndUpdateLanguageLocales(String url) throws JSONException, IOException {

        updateLanguageLocales(downloadJsonArray(url));
    }

    public void updateLanguageLocales(JSONArray jsonArray) throws JSONException, IOException {

        for(int i = 0; i < jsonArray.length(); i++) {

            JSONObject jsonObj = jsonArray.getJSONObject(i);
            updateModelFromJson(new LanguageLocaleDataSource(context), jsonObj, -1, false);
        }

    }
}
