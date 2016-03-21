package activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.unfoldingword.mobile.BuildConfig;
import org.unfoldingword.mobile.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import fragments.CheckingLevelInfoFragment;
import utils.UWPreferenceManager;

public class CustomSettingsActivity extends UWBaseActivity {

    @Bind(R.id.version_number_text_view)
    TextView versionNumberTextView;

    @Bind(R.id.build_number_text_view)
    TextView buildNumberTextView;

    @Bind(R.id.base_url_text_view)
    TextView baseUrlTextView;

    @Bind(R.id.settings_checking_level_fragment_frame)
    FrameLayout checkingLevelFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_settings);
        ButterKnife.bind(this);
        setupToolbar(false, "Settings", false);
        setupViews();
    }

    private void setupViews(){

        addCheckingLevelFragment();
        updateViews();
    }

    private void updateViews(){

        baseUrlTextView.setText(UWPreferenceManager.getDataDownloadUrl(getApplicationContext()));
        versionNumberTextView.setText(BuildConfig.VERSION_NAME);
        buildNumberTextView.setText(Integer.toString(BuildConfig.VERSION_CODE));
    }

    private void addCheckingLevelFragment(){
        CheckingLevelInfoFragment fragment = new CheckingLevelInfoFragment();
        getSupportFragmentManager().beginTransaction().add(checkingLevelFrame.getId(), fragment, "CheckingLevelInfoFragment" + checkingLevelFrame.getId()).commit();
    }

    @Override
    public AnimationParadigm getAnimationParadigm() {
        return AnimationParadigm.ANIMATION_VERTICAL;
    }

    public void baseUrlClicked(View view) {
        changeUrl();
    }

    private void changeUrl(){

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText editText = new EditText(this);
        alert.setTitle("Change Base URL");
        editText.setText(UWPreferenceManager.getDataDownloadUrl(getApplicationContext()));
        alert.setView(editText);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                changeUrl(editText.getText().toString());
                dialog.dismiss();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });

        alert.show();
    }

    private void changeUrl(String newUrl) {

        if (URLUtil.isValidUrl(newUrl)){
            UWPreferenceManager.setDataDownloadUrl(getApplicationContext(), newUrl);
            updateViews();
        }
        else{
            showAlert("Error", "The URL you entered appears to be invalid");
        }
    }

    public void resetUrlClicked(View view) {
        resetUrl();
    }

    private void resetUrl(){
        changeUrl(getResources().getString(R.string.pref_default_base_url));
    }
}
