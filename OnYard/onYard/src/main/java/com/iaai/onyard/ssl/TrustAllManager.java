package com.iaai.onyard.ssl;

import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * Implementation of X509TrustManager that trusts all certificates.
 */
public class TrustAllManager implements X509TrustManager {
	public void checkClientTrusted(X509Certificate[] cert, String authType) {
	}

	public void checkServerTrusted(X509Certificate[] cert, String authType) {
	}

	public X509Certificate[] getAcceptedIssuers() {
		return null;
	}
}
