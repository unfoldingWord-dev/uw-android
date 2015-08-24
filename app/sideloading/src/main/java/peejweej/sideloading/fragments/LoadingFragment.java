package peejweej.sideloading.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import peejweej.sideloading.R;

/**
 * A Fragment for showing a loading view.
 */
public class LoadingFragment extends android.support.v4.app.DialogFragment {

    public static final String TAG = "LoadingFragment";

    private static final String LOADING_TEXT_PARAM = "LOADING_TEXT_PARAM";

    private LoadingFragmentInteractionListener listener;

    private TextView loadingTextView;

    private Button loadingCancelButton;
    private String loadingText;

    private boolean showCancel = true;

    //region setup

    /**
     * @param loadingText text to display to user
     * @return a newly constructed LoadingFragment
     */
    public static LoadingFragment newInstance(String loadingText) {
        LoadingFragment fragment = new LoadingFragment();
        Bundle args = new Bundle();
        args.putString(LOADING_TEXT_PARAM, loadingText);
        fragment.setArguments(args);
        return fragment;
    }



    public LoadingFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            loadingText = getArguments().getString(LOADING_TEXT_PARAM);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(listener == null) {
            try {
                listener = (LoadingFragmentInteractionListener) activity;
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_loading, container, false);
        setupViews(view);
        return view;
    }

    private void setupViews(View view){

        loadingTextView = (TextView) view.findViewById(R.id.progress_bar_fragment_text_view);
        loadingTextView.setText(loadingText);
        loadingCancelButton = (Button) view.findViewById(R.id.progress_bar_fragment_cancel_button);
        loadingCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelLoading();
            }
        });
        loadingCancelButton.setVisibility((showCancel) ? View.VISIBLE : View.GONE);
    }

    //endregion

    //region accessors

    public void setListener(LoadingFragmentInteractionListener listener) {
        this.listener = listener;
    }

    public void setLoadingText(String text){
        loadingText = text;
        if(loadingTextView != null) {
            loadingTextView.setText(text);
        }
    }

    public void setCanCancel(boolean canCancel){

        showCancel = canCancel;
        if(loadingCancelButton != null) {
            loadingCancelButton.setVisibility((canCancel) ? View.VISIBLE : View.GONE);
        }
    }

    //endregion

    //region ending

    private void cancelLoading() {
        if (listener != null) {
            listener.loadingCanceled();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    //endregion

    public interface LoadingFragmentInteractionListener {
        /**
         * User pressed cancel
         */
        void loadingCanceled();
    }

}
