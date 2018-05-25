/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package fragments;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.unfoldingword.mobile.BuildConfig;
import org.unfoldingword.mobile.R;

import activity.StatementOfFaithActivity;
import activity.TranslationGuidelinesActivity;
import activity.UWBaseActivity;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Fechner on 8/18/15.
 * Dialogue fragment for showing the checking level info
 */
public class CheckingLevelInfoFragment extends DialogFragment {

    public CheckingLevelInfoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.verification_fragment, container, false);
        ButterKnife.bind(this, view);
        TextView textView = (TextView) view.findViewById(R.id.version_text_view);
        textView.setText(BuildConfig.VERSION_NAME);

        LinearLayout layout = (LinearLayout) view.findViewById(R.id.verification_fragment_layout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getDialog() != null) {
                    getDialog().dismiss();
                }
            }
        });
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @OnClick(R.id.translation_guidelines_button) void showGuidelinesDialog() {
        ((UWBaseActivity) getActivity()).goToNewActivity(TranslationGuidelinesActivity.class);
    }

    @OnClick(R.id.statement_of_faith_button) void showStatementOfFaithDialog() {
        ((UWBaseActivity) getActivity()).goToNewActivity(StatementOfFaithActivity.class);
    }

    @OnClick(R.id.privacy_policy_button)
    void showPrivacyPolicy() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://unfoldingword.org/app/"));
        getActivity().startActivity(browserIntent);
    }
}