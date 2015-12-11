/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package activity.sharing;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TextView;

import com.github.peejweej.androidsideloading.fragments.TypeChoosingFragment;
import com.github.peejweej.androidsideloading.model.SideLoadInformation;

import org.unfoldingword.mobile.R;

import java.util.ArrayList;
import java.util.List;

import activity.AnimationParadigm;
import activity.UWBaseActivity;
import fragments.ResourceChoosingFragment;
import fragments.selection.ShareSelectionFragment;
import model.DaoDBHelper;
import model.DataFileManager;
import model.DownloadState;
import model.SharingHelper;
import model.daoModels.Project;
import model.daoModels.Version;
import model.parsers.MediaType;

public class ShareActivity extends UWBaseActivity {

    private static final String TAG = "ShareActivity";
    private ShareSelectionFragment selectionFragment;

    private Project[] projects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        setupToolbar(false, getString(R.string.app_name), false);
        setupData();
        addFragment();
    }

    @Override
    public AnimationParadigm getAnimationParadigm() {
        return AnimationParadigm.ANIMATION_VERTICAL;
    }

    private void setupData(){
        List<Project> projectList = Project.getAllModels(DaoDBHelper.getDaoSession(getApplicationContext()));
        projects = new Project[projectList.size()];
        projectList.toArray(projects);
    }

    private void addFragment(){

        selectionFragment = ShareSelectionFragment.newInstance(projects);

        getSupportFragmentManager().beginTransaction().add(R.id.share_fragment_frame, selectionFragment).commit();
    }

    public void shareClicked(View view) {

        final Version version = selectionFragment.getSelectedVersion();
        if(version != null) {

            DataFileManager.getStateOfContent(getApplicationContext(), version, MediaType.MEDIA_TYPE_AUDIO, new DataFileManager.GetDownloadStateResponse() {
                @Override
                public void foundDownloadState(DownloadState state) {

                    if(state == DownloadState.DOWNLOAD_STATE_DOWNLOADED){

                        ResourceChoosingFragment.newInstance(version, new ResourceChoosingFragment.ResourceChoosingListener() {
                            @Override
                            public void resourcesChosen(DialogFragment dialogFragment, List<MediaType> types) {
                                shareVersion(types, version);
                                dialogFragment.dismiss();
                            }
                        }).show(getSupportFragmentManager(), "ResourceChoosingFragment");

                    }
                    else{
                        shareVersion(new ArrayList<MediaType>(), version);
                    }

                }
            });
        }
    }

    private void shareVersion(List<MediaType> types, Version version){

        setLoadingFragmentVisibility(true, "Preparing Sharable Version", false);
        SharingHelper.getShareInformation(getApplicationContext(), version, types, new SharingHelper.SideLoadInformationResponse() {
            @Override
            public void informationLoaded(SideLoadInformation information) {

                if(information != null) {
                    TypeChoosingFragment.constructFragment(information)
                            .show(getSupportFragmentManager(), "TypeChoosingFragment");
                }
                else{
                    showFailedAlert();
                }
                setLoadingFragmentVisibility(false, "", true);
            }
        });
    }

    private void showFailedAlert(){

        View titleView = View.inflate(getApplicationContext(), R.layout.alert_title, null);
        ((TextView) titleView.findViewById(R.id.alert_title_text_view)).setText("Sharing Failure");
        new AlertDialog.Builder(this)
                .setCustomTitle(titleView)
                .setMessage("Sharing failed. Please try again")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

//    @Override
//    public void rowSelectedOrDeselected() {
////        int numOfKeyboards = selectionFragment.getSelectedVersion().size();
//    }
}
