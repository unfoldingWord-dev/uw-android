/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.unfoldingword.mobile.R;

import fragments.LoadingFragment;
import view.UWToolbarViewGroup;

/**
 * A login screen that offers login via email/password.
 */
abstract public class UWBaseActivity extends ActionBarActivity implements UWToolbarViewGroup.UWToolbarListener{

    private static final String TAG = "UWBaseActivity";

    private UWToolbarViewGroup toolbar;
    private LoadingFragment loadingFragment;
    private RelativeLayout loadingBox;

    protected boolean isLoading;
    private boolean isActive = false;

    /**
     *
     * @return The AnimationParadigm for this activity
     */
    abstract public AnimationParadigm getAnimationParadigm();


    //region Parent Overrides

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isActive = true;
    }
    @Override
    protected void onStart() {
        super.onStart();
        isActive = true;
    }
    @Override
    protected void onResume() {
        super.onResume();
        isActive = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActive = false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            handleBack();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        handleBack();
    }

    /**
     *Before 2.0
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            handleBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //endregion

    //region Accessors

    public boolean isActive() {
        return isActive;
    }

    public UWToolbarViewGroup getToolbar(){

        return toolbar;
    }

    //endregion

    //region Setup

    /**
     * Sets up the toolbar with the passed parameters
     * @param hasLogo will show the logo if true
     * @param titleText will show the title with this text
     * @param titleClickable whether the title should be clickable
     * @param rightButtonClickable whether the right button should be shown and clickable.
     */
    public void setupToolbar(boolean hasLogo, String titleText, boolean titleClickable, boolean rightButtonClickable){

        setupToolbar(hasLogo, titleText, titleClickable);
        toolbar.setRightImageVisible(rightButtonClickable);
    }

    /**
     * Sets up the toolbar with the passed parameters
     * @param hasLogo will show the logo if true
     * @param titleText will show the title with this text
     * @param titleClickable whether the title should be clickable
     */
    public void setupToolbar(boolean hasLogo, String titleText, boolean titleClickable){

        setupToolbar(hasLogo);
        toolbar.setTitle(titleText, titleClickable);
    }

    /**
     * Sets up the toolbar based on the passed parameters
     * @param hasLogo will show the logo if true
     */
    public void setupToolbar(boolean hasLogo){

        toolbar = new UWToolbarViewGroup((Toolbar) findViewById(R.id.toolbar), this, hasLogo, getBackResource(), this);
        setToolbarColor(getResources().getColor(R.color.primary_dark));
    }

    /**
     * Will set the background color of the toolbar
     * @param color the desired color resource of the toolbar
     */
    protected void setToolbarColor(int color){
        this.toolbar.setBackgroundColor(color);
    }

    /**
     *
     * @return resource for the back button
     */
    public int getBackResource(){
        if(getAnimationParadigm() == AnimationParadigm.ANIMATION_VERTICAL ||
                getAnimationParadigm() == AnimationParadigm.ANIMATION_FORWARD_RIGHT_BACK_DOWN){
            return R.drawable.x_button;
        }
        else {
            return R.drawable.back_button_light;
        }
    }

    //endregion

    //region user interaction

    //endregion


    /**
     * creates a loading box and puts it in the passed viewGroup
     * @param visible Loading box should be showing
     * @param viewGroupId
     */
    protected void setLoadingBoxVisible(boolean visible, int viewGroupId){

        if(loadingBox == null){
            ViewGroup group = (ViewGroup) findViewById(viewGroupId);
            loadingBox = (RelativeLayout) getLayoutInflater().inflate(R.layout.loading_box, group);
            loadingBox = (RelativeLayout) findViewById(R.id.loading_box_base_layout);
        }

        loadingBox.setVisibility((visible) ? View.VISIBLE : View.GONE);
    }

    /**
     * Creates and Loading fragment.
     * @param visible true if the fragment should be visible
     * @param loadingText text to show in the loading fragment
     * @param cancelable whether the fragment should be cancelable.
     */
    public void setLoadingFragmentVisibility(final boolean visible, final String loadingText, final boolean cancelable){

         runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!visible) {
                    if (loadingFragment != null) {
                        loadingFragment.dismiss();
                    }
                    return;
                } else {
                    if (loadingFragment == null) {
                        loadingFragment = LoadingFragment.newInstance(loadingText);

                        loadingFragment.setCancelable(cancelable);
                        loadingFragment.setListener(new LoadingFragment.LoadingFragmentInteractionListener() {
                            @Override
                            public void loadingCanceled() {
                                loadingFragment.dismiss();
                                getSupportFragmentManager().popBackStackImmediate();
                                loadingFragment = null;
                            }
                        });

                        loadingFragment.show(getSupportFragmentManager(), LoadingFragment.TAG);

                    } else if (!loadingFragment.isVisible()) {
                        loadingFragment.show(getSupportFragmentManager(), LoadingFragment.TAG);
                    }
                    loadingFragment.setLoadingText(loadingText);
                    loadingFragment.setCanCancel(cancelable);
                }
            }
        });
    }

    /**
     * rotates the screen to portrait if it's currently in landscape, or landscape it it's currently in portrait
     * @param shouldMakeSensorBased whether the activity should reset to sensor based orientation after changing
     */
    protected void rotate(boolean shouldMakeSensorBased){
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        else{
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        if(shouldMakeSensorBased){
            waitAndMakeOrientationSensorBased();
        }
    }

    /**
     * waits for 2 second and makes the activity's orientation sensor based
     */
    protected void waitAndMakeOrientationSensorBased(){

        Thread thread = new Thread("orientationThread") {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        wait(2000);
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            }
        };
        thread.start();
    }

    //endregion

    //region Changing Activities

    /**
     * Starts new activity with a vertical popover-like animation
     * @param nextClass the class of the new activity
     */
    public void goToNewActivity(Class nextClass){
        goToNewActivity(new Intent(getApplicationContext(), nextClass));
    }

    /**
     * Starts new activity with a vertical popover-like animation
     * @param intent Intent of the new activity
     */
    public void goToNewActivity(Intent intent){

        int enterAnimation = AnimationParadigm.getNextAnimationEnter(AnimationParadigm.ANIMATION_VERTICAL);
        int exitAnimation = AnimationParadigm.getNextAnimationExit(AnimationParadigm.ANIMATION_VERTICAL);
        startActivity(intent);
        overridePendingTransition(enterAnimation, exitAnimation);
    }

    /**
     * starts a new activity with the activity's forward animation
     * @param nextClass class for new activity
     */
    public void goToNextActivity(Class nextClass){

        goToNextActivity(new Intent(getApplicationContext(), nextClass));
    }

    /**
     * starts a new activity with the activity's forward animation
     * @param intent Intent for new activity
     */
    public void goToNextActivity(Intent intent){

        int enterAnimation = AnimationParadigm.getNextAnimationEnter(getAnimationParadigm());
        int exitAnimation = AnimationParadigm.getNextAnimationExit(getAnimationParadigm());

        startActivity(intent);
        overridePendingTransition(enterAnimation, exitAnimation);
    }

    public void onBackPressed(boolean isSharing) {
        handleBack();
    }

    /**
     * goes back to the previous activity using the activity's back animation
     */
    protected void handleBack(){

        int enterAnimation = AnimationParadigm.getEndingAnimationEnter(getAnimationParadigm());
        int exitAnimation = AnimationParadigm.getEndingAnimationExit(getAnimationParadigm());
        finish();
        overridePendingTransition(enterAnimation, exitAnimation);
    }

    /**
     * goes to a new activity using the activity's back animation
     * @param activity new activity class to start
     */
    protected void goBackToActivity(Class activity){

        goBackToActivity(activity, null);
    }

    /**
     * starts a new activity using the activity's back animation
     * @param activity new activity class to start
     * @param extras option extras to add to the new Intent
     */
    protected void goBackToActivity(Class activity, Bundle extras){

        int enterAnimation = AnimationParadigm.getEndingAnimationEnter(AnimationParadigm.ANIMATION_VERTICAL);
        int exitAnimation = AnimationParadigm.getEndingAnimationExit(AnimationParadigm.ANIMATION_VERTICAL);

        Intent intent = new Intent(getApplicationContext(), activity);
        if(extras != null){
            intent.putExtras(extras);
        }

        startActivity(intent);
        overridePendingTransition(enterAnimation, exitAnimation);
        finish();
    }
    //endregion

    public void showAlert(String title, String message){

        showAlert(title, message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    public void showAlert(String title, String message, DialogInterface.OnClickListener clickListener){

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Dismiss", clickListener);
        alertDialog.show();
    }

    //region Toolbar Listener

    @Override
    public void centerButtonClicked() {

    }
    @Override
    public void leftButtonClicked() {
        handleBack();
    }

    @Override
    public void rightButtonClicked() {

    }
    //endregion


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     */

    /**
     *
     * @return true if app has storage permissions
     */
    public boolean verifyOrRequestStoragePermissions() {
        if (!verifyStoragePermissions()) {
            askForStoragePermission();
            return false;
        }
        else{
            return true;
        }
    }

    public boolean verifyStoragePermissions() {
        return permissionIsAllowed(Manifest.permission.WRITE_EXTERNAL_STORAGE) && permissionIsAllowed(Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    private boolean permissionIsAllowed(String permissionID) {
        return ContextCompat.checkSelfPermission(this, permissionID) == PackageManager.PERMISSION_GRANTED;
    }

    public void askForStoragePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            showAlert("Storage Permission", "Allow access to external storage if you would like to share or receive Versions directly from another device", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    askForStoragePermission();
                }
            });
        }
        else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            showAlert("Storage Permission", "Allow access to external storage if you would like to share or receive Versions directly from another device", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    askForStoragePermission();
                }
            });
        }
        else{
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    storagePermissionWasGranted();
                }
            }
        }
    }

    public void storagePermissionWasGranted() { }

    public void showChoiceDialogue(String title, String message, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener) {

        View titleView = View.inflate(getApplicationContext(), R.layout.alert_title, null);
        ((TextView) titleView.findViewById(R.id.alert_title_text_view)).setText(title);

        new android.app.AlertDialog.Builder(this)
                .setCustomTitle(titleView)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Yes", positiveListener)
                .setNegativeButton("No", negativeListener)
                .show();
    }
}



