package utils;

import java.io.InputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.SystemClock;

public class AsyncImageLoader extends AsyncTask<String, Integer, Bitmap> {

	private static String network_response;
	private static boolean doAcceptAllSSL = false, doUseCookie = false;
	private onImageLoaderListener mOnImageLoaderListener;
	private int progress;
	private onProgressUpdateListener mOnProgressUpdateListener;
	private Bitmap bmpResult;

	public AsyncImageLoader(onImageLoaderListener mOnImageLoaderListener,
			boolean doAcceptAllSSL, boolean doUseCookie,
			onProgressUpdateListener mOnProgressUpdateListener) {
		this.mOnImageLoaderListener = mOnImageLoaderListener;
		AsyncImageLoader.doAcceptAllSSL = doAcceptAllSSL;
		AsyncImageLoader.doUseCookie = doUseCookie;
		this.mOnProgressUpdateListener = mOnProgressUpdateListener;
	}

	/**
	 * This is our interface that listens for image download completion.
	 */
	public interface onImageLoaderListener {
		/**
		 * This callback will be invoked when the image has finished
		 * downloading.
		 * 
		 * @param image
		 *            the image as Bitmap object or null in case of an error
		 * @param response
		 *            the network response
		 */
		void onImageLoaded(Bitmap image, String response);
	}

	/**
	 * That interface will allow us to update MainActivity's Views
	 */
	public interface onProgressUpdateListener {
		/**
		 * Invoked when AsyncTask.onProgressUpdate() is called.
		 * 
		 * @param progress
		 *            the current download progress
		 */
		void doUpdateProgress(int progress);
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		bmpResult = downloadImage(params[0]);
		/**
		 * This is just an example of a correct way to show the progress on UI
		 * thread. To show a <i>real</i> progress, you will need to know the
		 * length of the file being downloaded
		 */
		while (progress < 100) {
			progress += 1;
			publishProgress(progress);
			SystemClock.sleep(100);
		}

		return bmpResult;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		super.onPostExecute(result);
		/**
		 * called on the UI thread
		 */
		if (mOnImageLoaderListener != null) {
			mOnImageLoaderListener.onImageLoaded(result, network_response);
		}
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		/**
		 * publish the progress on UI thread
		 */
		mOnProgressUpdateListener.doUpdateProgress(values[0]);
	}

	public static Bitmap downloadImage(String url) {

		HttpParams htParams = new BasicHttpParams();
		/**
		 * You can also add timeouts to the settings menu in a real project
		 */
		HttpConnectionParams.setConnectionTimeout(htParams, 10000);
		HttpConnectionParams.setSoTimeout(htParams, 10000);
		HttpGet get = new HttpGet(url);
		DefaultHttpClient client;
		try {
//			if (doAcceptAllSSL)
//				client = (DefaultHttpClient) SSLErrorPreventer
//						.setAcceptAllSSL(new DefaultHttpClient(hparams));
//			else
				client = new DefaultHttpClient(htParams);
			HttpResponse response = null;
//			if (doUseCookie) {
//				CookieStore store = client.getCookieStore();
//				HttpContext ctx = new BasicHttpContext();
//				store.addCookie(Utils.sessionCookie);
//				ctx.setAttribute(ClientContext.COOKIE_STORE, store);
//			}
			response = client.execute(get);
			network_response = response.getStatusLine().toString();
//			MainActivity.tmpResponseForUIDownload = network_response;
			HttpEntity responseEntity = response.getEntity();
			BufferedHttpEntity httpEntity = new BufferedHttpEntity(
					responseEntity);
			InputStream imageStream = httpEntity.getContent();

			return BitmapFactory.decodeStream(imageStream);

		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

    public static String getLastBitFromUrl(String url) {
        String changedUrl = url.replaceFirst(".*/([^/?]+).*", "$1");
        return changedUrl;
    }
}