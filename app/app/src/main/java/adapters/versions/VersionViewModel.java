package adapters.versions;

import android.content.Context;
import android.util.Log;

import org.unfoldingword.mobile.R;

import java.util.ArrayList;
import java.util.List;

import eventbusmodels.DownloadingVersionsEvent;
import model.DaoDBHelper;
import model.DataFileManager;
import model.DownloadState;
import model.daoModels.Language;
import model.daoModels.LanguageLocale;
import model.daoModels.Project;
import model.daoModels.Version;
import model.parsers.MediaType;
import signing.Status;
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

    public void updateContent(){
        version = Version.getVersionForId(version.getId(), DaoDBHelper.getDaoSession(context));
//        for(ResourceViewModel model : resources){
//            model.setState(DownloadState.DOWNLOAD_STATE_DOWNLOADING);
//        }
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
        return  getLanguageName(context) + " (" + version.getLanguage().getLanguageAbbreviation() + ") - " + version.getName();
    }

    private String getLanguageName(Context context){
        LanguageLocale languageLocale = LanguageLocale.getLocalForKey(version.getLanguage().getLanguageAbbreviation(), DaoDBHelper.getDaoSession(context));
        return  (languageLocale != null)? languageLocale.getLanguageName() : "";
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

        private static final String TAG = "ResourceViewModel";
        private DownloadState state = DownloadState.DOWNLOAD_STATE_DOWNLOADING;
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

                case MEDIA_TYPE_VIDEO: return R.drawable.video_icon;

                default: return -1;
            }
        }

        public void setState(DownloadState state) {
            this.state = state;
        }

        public void getDownloadState(final DataFileManager.GetDownloadStateResponse response){

            if(isInLoadingEvent()){
                state = DownloadState.DOWNLOAD_STATE_DOWNLOADING;
                response.foundDownloadState(state);
                return;
            }

            DataFileManager.getStateOfContent(context, version, type, new DataFileManager.GetDownloadStateResponse() {
                @Override
                public void foundDownloadState(DownloadState newState) {
                    state = newState;
                    if(response != null) {
                        response.foundDownloadState(newState);
                    }
                }
            });
        }

        public void getDownloadStateAsync(final DataFileManager.GetDownloadStateResponse response){

            if(isInLoadingEvent()){
                Log.d(TAG, "is in loading event");
                state = DownloadState.DOWNLOAD_STATE_DOWNLOADING;
                response.foundDownloadState(state);
            }
            else {
                Log.d(TAG, "isn't loading event");
                response.foundDownloadState(state);
                DataFileManager.getStateOfContent(context, version, type, new DataFileManager.GetDownloadStateResponse() {
                    @Override
                    public void foundDownloadState(DownloadState newState) {
                        state = newState;
                        if (response != null) {
                            response.foundDownloadState(newState);
                        }
                    }
                });
            }
        }

        private boolean isInLoadingEvent(){
            return DownloadingVersionsEvent.containsModel(version, type);
        }

        public String getTitle(){
            switch (type){

                case MEDIA_TYPE_TEXT: return "Text";

                case MEDIA_TYPE_AUDIO: return "Audio";

                case MEDIA_TYPE_VIDEO: return "Video";

                default: return "";
            }
        }

        public int getVerifiedCheckingLevelImage(){
            if(type == MediaType.MEDIA_TYPE_AUDIO || (type == MediaType.MEDIA_TYPE_TEXT && version.getVerificationStatus() != Status.VERIFIED.ordinal())){
                return R.drawable.verify_fail;
            }
            return ViewContentHelper.getDarkCheckingLevelImageResource(Integer.parseInt(version.getStatusCheckingLevel()));
        }

        public int getCheckingLevelImage(){

            return ViewContentHelper.getDarkCheckingLevelImageResource(Integer.parseInt(version.getStatusCheckingLevel()));
        }

        public void doActionOnModel(final VersionViewHolder viewHolder){

            getDownloadState(new DataFileManager.GetDownloadStateResponse() {
                @Override
                public void foundDownloadState(DownloadState state) {
                    doAction(type, viewHolder, state);
                }
            });
        }

        public void itemClicked(final VersionViewHolder viewHolder){

            final ResourceViewModel viewModel = this;
            getDownloadStateAsync(new DataFileManager.GetDownloadStateResponse() {
                @Override
                public void foundDownloadState(DownloadState state) {
                    if (state == DownloadState.DOWNLOAD_STATE_DOWNLOADED) {
                        itemChosen(viewModel);
                    } else {
                        doAction(type, viewHolder, state);
                    }
                }
            });
        }
        public void checkingLevelClicked(){
            showCheckingLevel(type);
        }


        @Override
        public String toString() {
            return "ResourceViewModel{" +
                    "state=" + state.toString() +
                    ", type=" + type.toString() +
                    '}';
        }
    }

    public interface VersionViewModelListener{
        void doAction(VersionViewModel viewModel, VersionViewHolder viewHolder, DownloadState state, MediaType type);
        void resourceChosen(ResourceViewModel viewModel, Version version);
        void showCheckingLevel(Version version, MediaType type);
    }
}

