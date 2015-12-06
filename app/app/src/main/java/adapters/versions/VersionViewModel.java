package adapters.versions;

import android.content.Context;
import android.os.AsyncTask;

import org.unfoldingword.mobile.R;

import java.util.ArrayList;
import java.util.List;

import model.DaoDBHelper;
import model.DataFileManager;
import model.DownloadState;
import model.daoModels.Language;
import model.daoModels.LanguageLocale;
import model.daoModels.Project;
import model.daoModels.Version;
import model.parsers.MediaType;
import view.ViewContentHelper;

/**
 * Created by Fechner on 12/1/15.
 */
public class VersionViewModel {

    private Context context;
    private VersionViewModelListener listener;
    private Version version;
    private List<ResourceViewModel> resources = new ArrayList<>();

    public static List<VersionViewModel> createModels(Context context, Project project, VersionViewModelListener listener){

        List<VersionViewModel> models = new ArrayList<>();
        for(Language language : project.getLanguages()){
            for(Version version : language.getVersions()){
                models.add(new VersionViewModel(context, version, listener));
            }
        }
        return models;
    }
    public VersionViewModel(Context context, Version version, VersionViewModelListener listener) {
        this.listener = listener;
        this.context = context;
        this.version = version;
        setupResources();
    }

    private void setupResources(){
        int numAdded = 0;
        resources.add(numAdded, new ResourceViewModel(MediaType.MEDIA_TYPE_TEXT));
        numAdded++;
        if(version.hasAudio()){
            resources.add(numAdded, new ResourceViewModel(MediaType.MEDIA_TYPE_AUDIO));
            numAdded++;
        }
        if(version.hasVideo()){
            resources.add(numAdded, new ResourceViewModel(MediaType.MEDIA_TYPE_VIDEO));
        }
    }

    public Version getVersion() {
        return version;
    }

    public List<ResourceViewModel> getResources() {
        return resources;
    }

    public String getTitle(){
        return version.getName() + " " + getLanguageName(context) + " - " + version.getLanguage().getLanguageAbbreviation();
    }

    private String getLanguageName(Context context){
        LanguageLocale languageLocale = LanguageLocale.getLocalForKey(version.getLanguage().getLanguageAbbreviation(), DaoDBHelper.getDaoSession(context));
        return (languageLocale != null)? languageLocale.getLanguageName() : "";
    }

    private void doAction(MediaType type, VersionViewHolder viewHolder, DownloadState state){

        this.listener.doAction(this, viewHolder, state, type);
    }

    private void itemChosen(ResourceViewModel viewModel){
        this.listener.resourceChosen(viewModel, version);
    }

    private void showCheckingLevel(MediaType type){
        this.listener.showCheckingLevel(version, type);
    }

    public class ResourceViewModel{

        private MediaType type;

        public ResourceViewModel(MediaType type) {
            this.type = type;
        }

        public MediaType getType() {
            return type;
        }

        public int getImageResource(){

            switch (type){

                case MEDIA_TYPE_TEXT: return R.drawable.reading_icon;

                case MEDIA_TYPE_AUDIO: return R.drawable.audio_icon;

                case MEDIA_TYPE_VIDEO: return R.drawable.video_icone;

                default: return -1;
            }
        }

        public void getDownloadState(final GetDownloadStateResponse response){

            new AsyncTask<Context, DownloadState, DownloadState>(){
                @Override
                protected DownloadState doInBackground(Context... params) {
                    return DataFileManager.getStateOfContent(params[0], version, type);
                }

                @Override
                protected void onPostExecute(DownloadState downloadState) {
                    super.onPostExecute(downloadState);
                    response.foundDownloadState(downloadState);
                }
            }.execute(context);

        }

        public String getTitle(){
            switch (type){

                case MEDIA_TYPE_TEXT: return "Text";

                case MEDIA_TYPE_AUDIO: return "Audio";

                case MEDIA_TYPE_VIDEO: return "Video";

                default: return "";
            }
        }

        public int getCheckingLevelImage(){
            return ViewContentHelper.getDarkCheckingLevelImageResource(Integer.parseInt(version.getStatusCheckingLevel()));
        }

        public void doActionOnModel(final VersionViewHolder viewHolder){


            getDownloadState(new GetDownloadStateResponse() {
                @Override
                public void foundDownloadState(DownloadState state) {
                    doAction(type, viewHolder, state);
                }
            });
        }

        public void itemClicked(final VersionViewHolder viewHolder){

            final ResourceViewModel viewModel = this;
            getDownloadState(new GetDownloadStateResponse() {
                @Override
                public void foundDownloadState(DownloadState state) {
                    if(state == DownloadState.DOWNLOAD_STATE_DOWNLOADED){
                        itemChosen(viewModel);
                    }
                    else {
                        doAction(type, viewHolder, state);
                    }
                }
            });
        }
        public void checkingLevelClicked(){
            showCheckingLevel(type);
        }
    }

    public interface GetDownloadStateResponse{
        void foundDownloadState(DownloadState state);
    }

    public interface VersionViewModelListener{
        void doAction(VersionViewModel viewModel, VersionViewHolder viewHolder, DownloadState state, MediaType type);
        void resourceChosen(ResourceViewModel viewModel, Version version);
        void showCheckingLevel(Version version, MediaType type);
    }
}

