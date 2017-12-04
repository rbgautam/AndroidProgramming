package com.iaai.onyard.ssl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;

/**
 * Subclass of SSLSocketFactory that trusts all certificates.
 */
public class TrustAllSSLSocketFactory extends SSLSocketFactory {
	
    /**
     * The factory used to create SSL sockets.
     */
    private javax.net.ssl.SSLSocketFactory mFactory;

    /**
     * Default constructor.
     * 
     * @throws KeyManagementException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws UnrecoverableKeyException
     */
    public TrustAllSSLSocketFactory() throws KeyManagementException,
                    NoSuchAlgorithmException, KeyStoreException,
                    UnrecoverableKeyException 
    {
            super(null);
            try 
            {
                    SSLContext sslcontext = SSLContext.getInstance("TLS");
                    sslcontext.init(null, new TrustManager[] { new TrustAllManager() },
                                    null);
                    mFactory = sslcontext.getSocketFactory();
                    setHostnameVerifier(new AllowAllHostnameVerifier());
            } 
            catch (Exception ex) 
            {
            }
    }

    /**
     * Gets the Trust-All SSL socket factory.
     * 
     * @return The default SSL socket factory.
     * @throws KeyManagementException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws UnrecoverableKeyException
     */
    public static SocketFactory getDefault() throws KeyManagementException,
                    NoSuchAlgorithmException, KeyStoreException,
                    UnrecoverableKeyException {
            return new TrustAllSSLSocketFactory();
    }

    /* (non-Javadoc)
     * @see org.apache.http.conn.ssl.SSLSocketFactory#createSocket()
     */
    @Override
    public Socket createSocket() throws IOException {
            return mFactory.createSocket();
    }

    /* (non-Javadoc)
     * @see org.apache.http.conn.ssl.SSLSocketFactory#createSocket(java.net.Socket, java.lang.String, int, boolean)
     */
    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose)
                    throws IOException {
            return mFactory.createSocket(socket, host, port, autoClose);
    }

    public Socket createSocket(InetAddress inaddr, int i, InetAddress inaddr1,
                    int j) throws IOException {
            return mFactory.createSocket(inaddr, i, inaddr1, j);
    }

    public Socket createSocket(InetAddress inaddr, int i) throws IOException {
            return mFactory.createSocket(inaddr, i);
    }

    public Socket createSocket(String s, int i, InetAddress inaddr, int j)
                    throws IOException {
            return mFactory.createSocket(s, i, inaddr, j);
    }

    public Socket createSocket(String s, int i) throws IOException {
            return mFactory.createSocket(s, i);
    }

    public String[] getDefaultCipherSuites() {
            return mFactory.getDefaultCipherSuites();
    }

    public String[] getSupportedCipherSuites() {
            return mFactory.getSupportedCipherSuites();
    }
}