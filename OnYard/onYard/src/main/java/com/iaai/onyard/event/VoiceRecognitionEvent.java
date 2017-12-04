package com.iaai.onyard.event;

import android.os.Bundle;

public class VoiceRecognitionEvent {

    public enum VoiceRecognitionResult {
        SPEECH_ERROR, READY_FOR_SPEECH, SPEECH_RESULTS_FOUND
    }

    private final VoiceRecognitionResult mResult;
    private final Bundle mSpeechResults;
    private final int mError;

    public VoiceRecognitionEvent(VoiceRecognitionResult result) {
        mResult = result;
        mSpeechResults = new Bundle();
        mError = -1;
    }

    public VoiceRecognitionEvent(VoiceRecognitionResult result, Bundle speechResults) {
        mResult = result;
        mSpeechResults = speechResults;
        mError = -1;
    }

    public VoiceRecognitionEvent(VoiceRecognitionResult result, int errorCode) {
        mResult = result;
        mSpeechResults = new Bundle();
        mError = errorCode;
    }

    public VoiceRecognitionResult getResult() {
        return mResult;
    }

    public int getErrorCode() {
        return mError;
    }

    public Bundle getSpeechResults() {
        return mSpeechResults;
    }
}
