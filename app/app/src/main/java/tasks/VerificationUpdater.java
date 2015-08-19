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
public class VerificationUpdater {

    private VerificationTaskListener listener;
    private Context context;

    public VerificationUpdater(Context context, VerificationTaskListener listener) {
        this.context = context;
        this.listener = listener;
    }

    protected void execute(Book book) {

        try {
            byte[] bookText = URLDownloadUtil.downloadBytes(book.getSourceUrl());
            String sigText = URLDownloadUtil.downloadString(book.getSignatureUrl());
            UWSigning.updateVerification(context, book, bookText, sigText);
            listener.verificationFinishedWithResult(bookText);
        }
        catch (IOException e){
            e.printStackTrace();
            listener.verificationFinishedWithResult(null);
        }

    }

        public interface VerificationTaskListener {
        void verificationFinishedWithResult(byte[] text);
    }
}
