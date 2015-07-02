package fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import org.unfoldingword.mobile.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoadingFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoadingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoadingFragment extends android.support.v4.app.DialogFragment {

    public static final String TAG = "LoadingFragment";
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String LOADING_TEXT_PARAM = "LOADING_TEXT_PARAM";

    private String loadingText;
    private boolean showCancel = true;

    private LoadingFragmentInteractionListener mListener;

    private TextView loadingTextView;
    private Button loadingCancelButton;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param loadingText Parameter 1.
     * @return A new instance of fragment LoadingFragment.
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_loading, container, false);
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
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void cancelLoading() {
        if (mListener != null) {
            mListener.loadingCanceled();
        }
    }

    public void setmListener(LoadingFragmentInteractionListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(mListener == null) {
            try {
                mListener = (LoadingFragmentInteractionListener) activity;
            } catch (ClassCastException e) {
                e.printStackTrace();
//                throw new ClassCastException(activity.toString()
//                        + " must implement OnFragmentInteractionListener");
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface LoadingFragmentInteractionListener {
        // TODO: Update argument type and name
        public void loadingCanceled();
    }

}
