/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package tasks;

import android.content.Context;

import java.io.IOException;
import model.daoModels.Book;
import signing.UWSigning;
import utils.URLDownloadUtil;

/**
 * Created by PJ Fechner on 6/17/15.
 * Class for updating the verification of a book.
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
            UWSigning.updateBookVerification(context, book, bookText, sigText);
            listener.verificationFinishedWithResult(bookText, sigText);
        }
        catch (IOException e){
            e.printStackTrace();
            listener.verificationFinishedWithResult(null, null);
        }
    }

    public interface VerificationTaskListener {
        /**
         * Called when the Verification process is finished.
         * @param text byte array of the  text of the book that was verified
         */
        void verificationFinishedWithResult(byte[] text, String sigText);
    }
}
