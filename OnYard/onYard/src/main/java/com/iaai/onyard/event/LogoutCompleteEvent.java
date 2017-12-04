package com.iaai.onyard.event;

public class LogoutCompleteEvent {

    public enum LogoutResult {
        NO_NETWORK, SUCCESS, FAILURE
    }

    private final LogoutResult mResult;

    public LogoutCompleteEvent(LogoutResult result) {
        mResult = result;
    }

    public LogoutResult getResult() {
        return mResult;
    }
}
