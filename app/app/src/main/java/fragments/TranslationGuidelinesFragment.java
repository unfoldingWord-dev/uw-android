package fragments;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import org.unfoldingword.mobile.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TranslationGuidelinesFragment extends DialogFragment {


    public TranslationGuidelinesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_only_textview, container, false);
        setupView(view);
        return view;
    }

    private void setupView(View view){
        String text = getResources().getString(R.string.translation_guidelines);
        ((TextView) view.findViewById(R.id.only_textview_fragment_text_view)).setText(text);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
}
