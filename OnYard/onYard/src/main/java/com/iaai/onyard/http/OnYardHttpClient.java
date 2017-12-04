package com.iaai.onyard.http;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.iaai.onyard.ssl.TrustAllSSLSocketFactory;


public class OnYardHttpClient {

    private final DefaultHttpClient mHttpClient;
    private final int mConnectionTimeout = 10 * 1000;
    private final int mSocketTimeout = 60 * 1000;

    public OnYardHttpClient() throws KeyManagementException, UnrecoverableKeyException,
    NoSuchAlgorithmException, KeyStoreException {
        final HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, mConnectionTimeout);
        HttpConnectionParams.setSoTimeout(params, mSocketTimeout);

        final SchemeRegistry schreg = new SchemeRegistry();
        schreg.register(new Scheme("https", new TrustAllSSLSocketFactory(), 443));
        final ClientConnectionManager connManager = new ThreadSafeClientConnManager(params, schreg);

        mHttpClient = new DefaultHttpClient(connManager, params);
    }

    public HttpResponse execute(HttpUriRequest request) throws ClientProtocolException, IOException {
        return mHttpClient.execute(request);
    }
}
