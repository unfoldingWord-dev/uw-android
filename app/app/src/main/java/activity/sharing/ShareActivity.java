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
import android.util.Log;
import android.view.View;

import com.github.peejweej.androidsideloading.fragments.TypeChoosingFragment;

import org.json.JSONObject;
import org.unfoldingword.mobile.R;

import java.util.ArrayList;
import java.util.List;

import activity.AnimationParadigm;
import activity.UWBaseActivity;
import adapters.VersionShareAdapter;
import fragments.ResourceChoosingFragment;
import model.DaoDBHelper;
import model.SharingHelper;
import model.daoModels.Version;
import model.parsers.MediaType;

public class ShareActivity extends UWBaseActivity implements VersionShareAdapter.VersionAdapterListener {

    private static final String TAG = "ShareActivity";
    private ShareSelectionFragment selectionFragment;

    Version[] versions;

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
        List<Version> versionsList = Version.getAllModels(DaoDBHelper.getDaoSession(getApplicationContext()));
        versions = new Version[versionsList.size()];
        versionsList.toArray(versions);
    }

    private void addFragment(){

        selectionFragment = ShareSelectionFragment.newInstance(versions);

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

        TypeChoosingFragment.constructFragment(SharingHelper.getShareInformation(getApplicationContext(), version, types))
                .show(getSupportFragmentManager(), "TypeChoosingFragment");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void rowSelectedOrDeselected() {
//        int numOfKeyboards = selectionFragment.getSelectedVersion().size();
    }
}
