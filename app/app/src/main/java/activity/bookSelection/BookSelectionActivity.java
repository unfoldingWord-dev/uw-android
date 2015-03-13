package activity.bookSelection;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;

import org.unfoldingword.mobile.R;

import java.util.ArrayList;
import java.util.Collections;

import adapters.selectionAdapters.GeneralRowInterface;
import adapters.selectionAdapters.InitialPageModel;
import model.database.ModelCaching;
import model.datasource.ProjectDataSource;
import model.database.DBManager;
import model.modelClasses.mainData.LanguageModel;
import model.modelClasses.mainData.ProjectModel;

/**
 * Created by Fechner on 2/27/15.
 */
public class BookSelectionActivity extends GeneralSelectionActivity{

    static String BOOK_INDEX_STRING = "BOOK_INDEX_STRING";

    ArrayList<ProjectModel> mProjects = null;

    @Override
    protected int getContentView() {
        return R.layout.activity_general_list;
    }

    @Override
    protected ArrayList<String> getListOfLanguages() {

        if (mProjects == null) {
            addProjects();
        }

        return ModelCaching.getAvailableLanguages(getApplicationContext(), mProjects);
    }

    @Override
    protected String getIndexStorageString() {
        return BOOK_INDEX_STRING;
    }

    @Override
    protected Class getChildClass() {
        return VersionSelectionActivity.class;
    }

    @Override
    protected String getActionBarTitle() {
        return mProjects.get(0).meta;
    }

    protected ArrayList<GeneralRowInterface> getData(){

        ArrayList<GeneralRowInterface> data = new ArrayList<GeneralRowInterface>();
        String selectedLanguage = PreferenceManager.getDefaultSharedPreferences(this).getString(
                getResources().getString(R.string.selected_language), "English");

        if (mProjects == null) {
            addProjects();
        }
        Context context = getApplicationContext();

        for (ProjectModel project : mProjects) {

            LanguageModel language = project.getLanguageModel(context, selectedLanguage);

            if (language != null) {
                data.add(new InitialPageModel(language.projectName, Long.toString(language.uid)));
            }
        }
        return data;
    }

    private void addProjects(){

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            String chosenProject = extras.getString(CHOSEN_ID);

            this.mProjects = new ArrayList<ProjectModel>();
            ArrayList<ProjectModel> projects = new ProjectDataSource(this.getApplicationContext()).getAllProjects();

            for (ProjectModel project : projects) {

                if (project.meta.equalsIgnoreCase(chosenProject)) {
                    mProjects.add(project);
                }
            }
            Collections.sort(mProjects);
        }
    }
}
