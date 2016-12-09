/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.unfoldingword.mobile.BuildConfig;
import org.unfoldingword.mobile.R;

import java.util.List;

import fragments.LoadingFragment;
import model.DaoDBHelper;
import model.DatabaseOpenHelper;
import model.daoModels.DaoSession;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity {
    /**
     * Determines whether to always show the simplified settings UI, where
     * settings are presented in a single list. When false, settings are shown
     * as a master/detail two-pane view on tablets. When true, a single pane is
     * shown on tablets.
     */
    private static final boolean ALWAYS_SIMPLE_PREFS = false;

    private LoadingFragment loadingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setupSimplePreferencesScreen();
    }

    /**
     * Shows the simplified settings UI if the device configuration if the
     * device configuration dictates that a simplified, single-pane UI should be
     * shown.
     */
    private void setupSimplePreferencesScreen() {
        if (!isSimplePreferences(this)) {
            return;
        }

        addPreferencesFromResource(R.xml.pref_general);

        Preference button = findPreference("reset_url");
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {
                return resetURL();
            }
        });

        Preference preloadButton = findPreference("reset_preload");
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {
                requestResetDatabase();
                return true;
            }
        });


        Preference versionPref = findPreference("app_version");
        versionPref.setSummary(BuildConfig.VERSION_NAME);

        Preference buildPref = findPreference("app_build");
        buildPref.setSummary(Integer.toString(BuildConfig.VERSION_CODE));

        // Add 'notifications' preferences, and a corresponding header.
//        PreferenceCategory fakeHeader = new PreferenceCategory(this);
//        fakeHeader.setTitle(R.string.pref_header_data);
//        getPreferenceScreen().addPreference(fakeHeader);



        // Bind the summaries of EditText/List/Dialog/Ringtone preferences to
        // their values. When their values change, their summaries are updated
        // to reflect the new value, per the Android Design guidelines.
        bindPreferenceSummaryToValue(findPreference("base_url"));
//        this.view
        View view = View.inflate(getApplicationContext(), R.layout.verification_fragment, (LinearLayout) findViewById(R.id.activity_preference_base_layout));
        TextView textView = (TextView) view.findViewById(R.id.version_text_view);
        textView.setText(BuildConfig.VERSION_NAME);
    }

    boolean resetURL() {
        Resources resources = getResources();
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).
                edit().putString("base_url", resources.getString(R.string.pref_default_base_url)).commit();
        setPreferenceScreen(null);
        setupSimplePreferencesScreen();
        return true;
    }

    void requestResetDatabase() {
        showChoiceDialogue("Reset Database?", "This will reset all data to that of the most recent update. Any new Versions loaded since then will be lost.", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                resetDatabase();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
    }

    void resetDatabase() {
        DatabaseOpenHelper helper = DatabaseOpenHelper.getSharedInstance(getApplicationContext(),
                getApplicationContext().getResources().getString(R.string.database_name), null);
        helper.deleteDatabase();
        DaoDBHelper.getDaoSession(getApplicationContext(), new DaoDBHelper.AsynchronousDatabaseAccessorCompletion() {
            @Override
            public void loadedSession(@Nullable DaoSession session) {

            }
        });
    }

//    /**
//     * Creates and Loading fragment.
//     * @param visible true if the fragment should be visible
//     * @param loadingText text to show in the loading fragment
//     * @param cancelable whether the fragment should be cancelable.
//     */
//    public void setLoadingFragmentVisibility(final boolean visible, final String loadingText, final boolean cancelable){
//
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (!visible) {
//                    if (loadingFragment != null) {
//                        loadingFragment.dismiss();
//                    }
//                    return;
//                } else {
//                    if (loadingFragment == null) {
//                        loadingFragment = LoadingFragment.newInstance(loadingText);
//
//                        loadingFragment.setCancelable(cancelable);
//                        loadingFragment.setListener(new LoadingFragment.LoadingFragmentInteractionListener() {
//                            @Override
//                            public void loadingCanceled() {
//                                loadingFragment.dismiss();
//                                getFragmentManager().popBackStackImmediate();
//                                loadingFragment = null;
//                            }
//                        });
//
//                        loadingFragment.show(getSupportFragmentManager(), LoadingFragment.TAG);
//
//                    } else if (!loadingFragment.isVisible()) {
//                        loadingFragment.show(getSupportFragmentManager(), LoadingFragment.TAG);
//                    }
//                    loadingFragment.setLoadingText(loadingText);
//                    loadingFragment.setCanCancel(cancelable);
//                }
//            }
//        });
//    }

    public void showChoiceDialogue(String title, String message, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener) {

        View titleView = View.inflate(getApplicationContext(), R.layout.alert_title, null);
        ((TextView) titleView.findViewById(R.id.alert_title_text_view)).setText(title);

        new AlertDialog.Builder(this)
                .setCustomTitle(titleView)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, positiveListener)
                .setNegativeButton(android.R.string.no, negativeListener)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this) && !isSimplePreferences(this);
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Determines whether the simplified settings UI should be shown. This is
     * true if this is forced via {@link #ALWAYS_SIMPLE_PREFS}, or the device
     * doesn't have newer APIs like {@link PreferenceFragment}, or the device
     * doesn't have an extra-large screen. In these cases, a single-pane
     * "simplified" settings UI should be shown.
     */
    private static boolean isSimplePreferences(Context context) {
        return ALWAYS_SIMPLE_PREFS
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
                || !isXLargeTablet(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        if (!isSimplePreferences(this)) {
            loadHeadersFromResource(R.xml.pref_headers, target);
        }
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            preference.setSummary(stringValue);
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("base_url"));
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
        }
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DataSyncPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
        }
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return super.isValidFragment(fragmentName);
    }
}
