package activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import org.unfoldingword.mobile.R;

import fragments.LoadingFragment;
import view.UWToolbarViewGroup;

/**
 * A login screen that offers login via email/password.
 */
abstract public class UWBaseActivity extends ActionBarActivity implements UWToolbarViewGroup.UWToolbarListener{

    private UWToolbarViewGroup toolbar = null;

    public UWToolbarViewGroup getToolbar(){
        return toolbar;
    }
    private LoadingFragment loadingFragment;

    private static final String TAG = "BaseSignupLoginActivity";

    protected boolean isLoading;
    private RelativeLayout loadingBox;
    private boolean isActive = false;

    abstract public AnimationParadigm getAnimationParadigm();


    //region Parent Overrides
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
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

    // Before 2.0
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
    //endregion

    //region Setup
    public void setupToolbar(boolean hasLogo, String titleText, boolean titleClickable, boolean rightButtonClickable){

        setupToolbar(hasLogo, titleText, titleClickable);
        toolbar.setRightImageVisible(rightButtonClickable);
    }

    public void setupToolbar(boolean hasLogo, String titleText, boolean titleClickable){

        setupToolbar(hasLogo);
        toolbar.setTitle(titleText, titleClickable);
    }

    public void setupToolbar(boolean hasLogo){

        toolbar = new UWToolbarViewGroup((Toolbar) findViewById(R.id.toolbar), this, hasLogo, getBackResource(), this);
        setToolbarColor(getResources().getColor(R.color.primary_dark));
    }

    protected void setToolbarColor(int color){
        this.toolbar.setBackgroundColor(color);
    }

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


    //region View changing in current activity
    protected void setLoadingBoxVisible(boolean visible, int viewGroupId){

        if(loadingBox == null){
            ViewGroup group = (ViewGroup) findViewById(viewGroupId);
            loadingBox = (RelativeLayout) getLayoutInflater().inflate(R.layout.loading_box, group);
            loadingBox = (RelativeLayout) findViewById(R.id.loading_box_base_layout);
        }

        loadingBox.setVisibility((visible) ? View.VISIBLE : View.GONE);
    }

    public void setLoadingViewVisibility(final boolean visible, final String loadingText, final boolean cancelable){

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
                        loadingFragment.setmListener(new LoadingFragment.LoadingFragmentInteractionListener() {
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

    protected void rotate(){
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        else{
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

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

    public void goToNewActivity(Class nextClass){
        goToNewActivity(new Intent(getApplicationContext(), nextClass));
    }

    public void goToNewActivity(Intent intent){

        int enterAnimation = AnimationParadigm.getNextAnimationEnter(AnimationParadigm.ANIMATION_VERTICAL);
        int exitAnimation = AnimationParadigm.getNextAnimationExit(AnimationParadigm.ANIMATION_VERTICAL);
        startActivity(intent);
        overridePendingTransition(enterAnimation, exitAnimation);
    }

    public void goToNextActivity(Class nextClass){

        goToNextActivity(new Intent(getApplicationContext(), nextClass));
    }

    public void goToNextActivity(Intent intent){

        int enterAnimation = AnimationParadigm.getNextAnimationEnter(getAnimationParadigm());
        int exitAnimation = AnimationParadigm.getNextAnimationExit(getAnimationParadigm());

        startActivity(intent);
        overridePendingTransition(enterAnimation, exitAnimation);
    }

    public void onBackPressed(boolean isSharing) {
        handleBack();
    }

    protected void handleBack(){

        int enterAnimation = AnimationParadigm.getEndingAnimationEnter(getAnimationParadigm());
        int exitAnimation = AnimationParadigm.getEndingAnimationExit(getAnimationParadigm());
        finish();
        overridePendingTransition(enterAnimation, exitAnimation);
    }

    protected void goBackToActivity(Class activity){

        goBackToActivity(activity, null);
    }

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
}



