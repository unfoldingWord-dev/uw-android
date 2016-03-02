/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package utils;


import org.apache.http.conn.ssl.SSLSocketFactory;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;



/**
 * This is a helper class that extends <b>SSLSocketFactory</b> and needed to
 * prevent the <i>SSLPeerUnverifiedException</i> which is caused by a
 * non-trusted SSL certificate.
 */
public class SSLErrorPreventer extends SSLSocketFactory {
	SSLContext sslContext = SSLContext.getInstance("TLS");

	public SSLErrorPreventer(KeyStore truststore)
			throws NoSuchAlgorithmException, KeyManagementException,
			KeyStoreException, UnrecoverableKeyException {
		super(truststore);

		TrustManager tm = new X509TrustManager() {

			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			@Override
			public void checkClientTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {

			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {

			}
		};

		sslContext.init(null, new TrustManager[] { tm }, null);
	}

	public SSLErrorPreventer(SSLContext context) throws KeyManagementException,
			NoSuchAlgorithmException, KeyStoreException,
			UnrecoverableKeyException {
		super(null);
		sslContext = context;
	}

	@Override
	public Socket createSocket(Socket socket, String host, int port,
			boolean autoClose) throws IOException, UnknownHostException {
		return sslContext.getSocketFactory().createSocket(socket, host, port,
				autoClose);
	}

	@Override
	public Socket createSocket() throws IOException {
		return sslContext.getSocketFactory().createSocket();
	}
	
	/**
	 * Configures a HttpClient to accept all SSL certificates
	 * 
	 * @param client
	 *            the instance of HttpClient to configure
	 * @return a DefaultHttpClient that accepts all SSL certificates. Parameters
	 *         which were previously applied do persist.
	 */
//	public static HttpClient setAcceptAllSSL(HttpClient client) {
//		try {
//			X509TrustManager tm = new X509TrustManager() {
//				public void checkClientTrusted(X509Certificate[] xcs,
//						String string) throws CertificateException {
//				}
//
//				public void checkServerTrusted(X509Certificate[] xcs,
//						String string) throws CertificateException {
//				}
//
//				public X509Certificate[] getAcceptedIssuers() {
//					return null;
//				}
//			};
//			SSLContext ctx = SSLContext.getInstance("TLS");
//			ctx.init(null, new TrustManager[] { tm }, null);
//			SSLSocketFactory ssf = new SSLErrorPreventer(ctx);
//			ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//			ClientConnectionManager ccm = client.getConnectionManager();
//			SchemeRegistry sr = ccm.getSchemeRegistry();
//			sr.register(new Scheme("https", ssf, 443));
//			return new DefaultHttpClient(ccm, client.getParams());
//		} catch (Exception ex) {
//			return null;
//		}
//	}
}