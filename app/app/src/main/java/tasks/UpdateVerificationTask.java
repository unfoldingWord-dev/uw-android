package tasks;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;

import model.daoModels.Book;
import signing.UWSigning;
import utils.URLDownloadUtil;

/**
 * Created by Fechner on 6/17/15.
 */
public class UpdateVerificationTask extends AsyncTask<Book,Void, byte[]> {

    private VerificationTaskListener listener;
    private Context context;

    public UpdateVerificationTask(Context context, VerificationTaskListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected byte[] doInBackground(Book... params) {

        Book book = params[0];

        try {
            byte[] bookText = URLDownloadUtil.downloadBytes(book.getSourceUrl());
            String sigText = URLDownloadUtil.downloadString(book.getSignatureUrl());
            UWSigning.updateVerification(context, book, bookText, sigText);
            return bookText;
        }
        catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(byte[] s) {
        super.onPostExecute(s);
        listener.verificationFinishedWithResult(s);
    }

        public interface VerificationTaskListener {
        void verificationFinishedWithResult(byte[] text);
    }
}
