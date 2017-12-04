package com.iaai.onyard.classes;

import java.io.IOException;

import org.apache.http.HttpResponse;

import com.iaai.onyard.application.OnYard;
import com.iaai.onyard.utility.DataHelper;


public class UnauthorizedReason {

    private static class UnauthorizedType {

        public static final int UNKNOWN_CODE = -1;
        public static final int INVALID_TOKEN_CODE = 0;
        public static final int MULTIPLE_LOGIN_CODE = 1;

        private static final String INVALID_TOKEN_CONTENT_STRING = "Invalid token";
        private static final String MULTIPLE_LOGIN_CONTENT_STRING = "Multiple login";

        private static final String INVALID_TOKEN_MESSAGE = OnYard.DEFAULT_FORCEFUL_LOGOUT_MESSAGE;
        private static final String MULTIPLE_LOGIN_MESSAGE = "User %s has logged in to a second device. You may only be "
                + "logged in to one device at a time. You will now be logged out.";
    }

    private int mUnauthorizedType = UnauthorizedType.UNKNOWN_CODE;
    private String mUnauthorizedUser;

    public UnauthorizedReason() {
        mUnauthorizedType = UnauthorizedType.UNKNOWN_CODE;
    }

    public UnauthorizedReason(HttpResponse response, String unauthUser) {
        mUnauthorizedUser = unauthUser;
        String unauthReason;

        try {
            unauthReason = DataHelper.convertStreamToString(
                    response.getEntity().getContent()).replace("\"", "");
        }
        catch (final IllegalStateException e) {
            return;
        }
        catch (final IOException e) {
            return;
        }
        catch (final Exception e) {
            return;
        }

        if (unauthReason == null) {
            return;
        }

        if (unauthReason.equals(UnauthorizedType.INVALID_TOKEN_CONTENT_STRING)) {
            mUnauthorizedType = UnauthorizedType.INVALID_TOKEN_CODE;
        }
        else {
            if (unauthReason.equals(UnauthorizedType.MULTIPLE_LOGIN_CONTENT_STRING)) {
                mUnauthorizedType = UnauthorizedType.MULTIPLE_LOGIN_CODE;
            }
        }
    }

    public String getUserFriendlyMessage() {
        switch (mUnauthorizedType) {
            case UnauthorizedType.INVALID_TOKEN_CODE:
                return UnauthorizedType.INVALID_TOKEN_MESSAGE;
            case UnauthorizedType.MULTIPLE_LOGIN_CODE:
                return String.format(UnauthorizedType.MULTIPLE_LOGIN_MESSAGE, mUnauthorizedUser);
            default:
                return UnauthorizedType.INVALID_TOKEN_MESSAGE;
        }
    }
}

