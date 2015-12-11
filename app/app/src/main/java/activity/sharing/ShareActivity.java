/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package activity.sharing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;

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

            if(version.hasVideo() || version.hasAudio()){
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

//            TypeChoosingFragment.constructFragment(SharingHelper.getShareInformation(getApplicationContext(), version, new ArrayList<MediaType>()))
//                    .show(getSupportFragmentManager(), "TypeChoosingFragment");
        }
    }

    private void shareVersion(List<MediaType> types, Version version){

        setLoadingFragmentVisibility(true, "Preparing Sharable Version", false);
        SharingHelper.getShareInformation(getApplicationContext(), version, types, new SharingHelper.SideLoadInformationResponse() {
            @Override
            public void informationLoaded(SideLoadInformation information) {

                TypeChoosingFragment.constructFragment(information)
                        .show(getSupportFragmentManager(), "TypeChoosingFragment");

                setLoadingFragmentVisibility(false, "", true);
            }
        });

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
