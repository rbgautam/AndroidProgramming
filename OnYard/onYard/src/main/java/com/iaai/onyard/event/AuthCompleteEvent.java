package com.iaai.onyard.event;

public class AuthCompleteEvent {

    public enum AuthResult {
        NO_NETWORK, SUCCESS, FAILURE, EXPIRED
    }

    private final AuthResult mResult;

    public AuthCompleteEvent(AuthResult result) {
        mResult = result;
    }

    public AuthResult getResult() {
        return mResult;
    }
}
