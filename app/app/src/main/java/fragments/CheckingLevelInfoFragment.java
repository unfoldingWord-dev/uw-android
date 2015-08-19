package fragments;

import android.app.Dialog;
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

/**
 * Created by Fechner on 8/18/15.
 */
public class CheckingLevelInfoFragment extends DialogFragment {

    public CheckingLevelInfoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.verification_fragment, container, false);

        TextView tView = (TextView) view.findViewById(R.id.textView);
        String versionName = BuildConfig.VERSION_NAME;

        tView.setText(versionName);
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.verification_fragment_layout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
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
}