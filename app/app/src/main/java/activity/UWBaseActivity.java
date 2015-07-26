package activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.unfoldingword.mobile.R;

import fragments.LoadingFragment;
import view.UWToolbar;

/**
 * A login screen that offers login via email/password.
 */
abstract public class UWBaseActivity extends ActionBarActivity implements UWToolbar.UWToolbarListener{

    private UWToolbar toolbar = null;

    public UWToolbar getToolbar(){
        return toolbar;
    }
    private LoadingFragment loadingFragment;

    private static final String TAG = "BaseSignupLoginActivity";

    protected boolean isLoading;
    private RelativeLayout loadingBox;
    private boolean isActive = false;



    abstract public AnimationParadigm getAnimationParadigm();


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

    public boolean isActive() {
        return isActive;
    }

    public void setupToolbar(boolean hasLogo, String titleText, boolean titleClickable,
                             String rightButtonText, boolean rightButtonClickable){

        setupToolbar(hasLogo, titleText, titleClickable);
        toolbar.setRightButtonText(rightButtonText, rightButtonClickable);
    }

    public void setupToolbar(boolean hasLogo, String titleText, boolean titleClickable){

        setupToolbar(hasLogo);
        toolbar.setTitle(titleText, titleClickable);
    }

    public void setupToolbar(boolean hasLogo){

        toolbar = new UWToolbar((Toolbar) findViewById(R.id.toolbar), this, hasLogo, getBackResource(), this);
        setToolbarColor(getResources().getColor(R.color.primary_dark));
    }

    protected void setToolbarColor(int color){
        this.toolbar.setBackgroundColor(color);
    }

    @Override
    public void centerButtonClicked() {

    }
    @Override
    public void leftButtonClicked() {
        handleBack();
    }
    @Override
    public void checkingLevelButtonClicked() {

    }
    @Override
    public void rightButtonClicked() {

    }

    protected void setLoadingBoxVisible(boolean visible, int viewGroupId){

        if(loadingBox == null){
            ViewGroup group = (ViewGroup) findViewById(viewGroupId);
            loadingBox = (RelativeLayout) getLayoutInflater().inflate(R.layout.loading_box, group);
            loadingBox = (RelativeLayout) findViewById(R.id.loading_box_base_layout);
        }

        loadingBox.setVisibility((visible) ? View.VISIBLE : View.GONE);
    }

    protected void rightToolbarButtonPressed(){}

    public void goToNextActivity(Class nextClass){

        int enterAnimation = getNextAnimationEnter(getAnimationParadigm());
        int exitAnimation = getNextAnimationExit(getAnimationParadigm());

        startActivity(new Intent(getApplicationContext(), nextClass));
        overridePendingTransition(enterAnimation, exitAnimation);
    }

    public void goToNewActivity(Class nextClass){

        int enterAnimation = getNextAnimationEnter(AnimationParadigm.ANIMATION_VERTICAL);
        int exitAnimation = getNextAnimationExit(AnimationParadigm.ANIMATION_VERTICAL);
        startActivity(new Intent(getApplicationContext(), nextClass));
        overridePendingTransition(enterAnimation, exitAnimation);
    }

    public void goToNewActivity(Intent intent){

        int enterAnimation = getNextAnimationEnter(AnimationParadigm.ANIMATION_VERTICAL);
        int exitAnimation = getNextAnimationExit(AnimationParadigm.ANIMATION_VERTICAL);
        startActivity(intent);
        overridePendingTransition(enterAnimation, exitAnimation);
    }

    public void goToNextActivity(Intent intent){

        int enterAnimation = getNextAnimationEnter(getAnimationParadigm());
        int exitAnimation = getNextAnimationExit(getAnimationParadigm());

        startActivity(intent);
        overridePendingTransition(enterAnimation, exitAnimation);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            handleBack();
        }
        return super.onOptionsItemSelected(item);
    }

    protected void handleBack(){

        int enterAnimation = getEndingAnimationEnter(getAnimationParadigm());
        int exitAnimation = getEndingAnimationExit(getAnimationParadigm());
        finish();
        overridePendingTransition(enterAnimation, exitAnimation);
    }

    protected void goBackToActivity(Class activity){

        int enterAnimation = getEndingAnimationEnter(AnimationParadigm.ANIMATION_VERTICAL);
        int exitAnimation = getEndingAnimationExit(AnimationParadigm.ANIMATION_VERTICAL);
        startActivity(new Intent(getApplicationContext(), activity));
        overridePendingTransition(enterAnimation, exitAnimation);
        finish();
    }

    protected void goBackToActivity(Class activity, Bundle extras){

        int enterAnimation = getEndingAnimationEnter(AnimationParadigm.ANIMATION_VERTICAL);
        int exitAnimation = getEndingAnimationExit(AnimationParadigm.ANIMATION_VERTICAL);
        startActivity(new Intent(getApplicationContext(), activity).putExtras(extras));
        overridePendingTransition(enterAnimation, exitAnimation);
        finish();
    }

    protected void setLoadingViewVisibility(final boolean visible, final String loadingText, final boolean cancelable){

        runOnUiThread(new Runnable() {
                @Override
                public void run() {
                if (!visible) {
                    if (loadingFragment != null) {
                        loadingFragment.dismiss();
                    }
                    return;
                }
                else {
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

                    } else if(!loadingFragment.isVisible()){
                        loadingFragment.show(getSupportFragmentManager(), LoadingFragment.TAG);
                    }
                    loadingFragment.setLoadingText(loadingText);
                    loadingFragment.setCanCancel(cancelable);
                }
            }
        });
    }

    public int getNextAnimationEnter(AnimationParadigm paradigm){

        switch (paradigm){
            case ANIMATION_FORWARD_RIGHT_BACK_DOWN:
            case ANIMATION_LEFT_RIGHT:{
                return R.anim.enter_from_right;
            }
            case ANIMATION_FORWARD_UP_BACK_LEFT:
            case ANIMATION_VERTICAL:
            default: {
                return R.anim.enter_from_bottom;
            }
        }
    }

    public int getNextAnimationExit(AnimationParadigm paradigm){

        switch (paradigm){
            case ANIMATION_FORWARD_RIGHT_BACK_DOWN:
            case ANIMATION_LEFT_RIGHT:{
                return R.anim.exit_on_left;
            }
            case ANIMATION_FORWARD_UP_BACK_LEFT:
            case ANIMATION_VERTICAL:
            default:{
                return R.anim.enter_center;
            }
        }
    }

    public int getEndingAnimationEnter(AnimationParadigm paradigm){

        switch (paradigm){
            case ANIMATION_FORWARD_UP_BACK_LEFT:
            case ANIMATION_LEFT_RIGHT:{
                return R.anim.left_in;
            }
            case ANIMATION_FORWARD_RIGHT_BACK_DOWN:
            case ANIMATION_VERTICAL:
            default:{
                return R.anim.enter_center;
            }
        }
    }

    public int getEndingAnimationExit(AnimationParadigm paradigm){

        switch (paradigm){
            case ANIMATION_FORWARD_UP_BACK_LEFT:
            case ANIMATION_LEFT_RIGHT:{
                return R.anim.right_out;
            }
            case ANIMATION_FORWARD_RIGHT_BACK_DOWN:
            case ANIMATION_VERTICAL:
            default:{
                return R.anim.exit_on_bottom;
            }
        }
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

    public int getBackResource(){
        if(getAnimationParadigm() == AnimationParadigm.ANIMATION_VERTICAL ||
                getAnimationParadigm() == AnimationParadigm.ANIMATION_FORWARD_RIGHT_BACK_DOWN){
            return R.drawable.x_button;
        }
        else {
            return R.drawable.back_button_light;
        }
    }
}



