package com.iaai.onyard.sync;


public class PendingSyncSession {

    private final String mSessionId;
    private final Integer mAppId;

    public PendingSyncSession(String sessionId, Integer appId) {
        mSessionId = sessionId;
        mAppId = appId;
    }

    public String getSessionId() {
        return mSessionId;
    }

    public Integer getAppId() {
        return mAppId;
    }
}
