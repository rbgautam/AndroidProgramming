package com.iaai.onyard.camera;

import android.os.Bundle;
import android.speech.RecognitionListener;

import com.iaai.onyard.event.VoiceRecognitionEvent;
import com.iaai.onyard.event.VoiceRecognitionEvent.VoiceRecognitionResult;
import com.iaai.onyard.utility.LogHelper;
import com.squareup.otto.Bus;

public class OnYardRecognitionListener implements RecognitionListener {

    private final Bus mBus;

    public OnYardRecognitionListener(Bus bus) {
        mBus = bus;
    }

    @Override
    public void onRmsChanged(float rmsdB) {}

    @Override
    public void onResults(Bundle results) {
        mBus.post(new VoiceRecognitionEvent(VoiceRecognitionResult.SPEECH_RESULTS_FOUND, results));
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        mBus.post(new VoiceRecognitionEvent(VoiceRecognitionResult.READY_FOR_SPEECH));
    }

    @Override
    public void onPartialResults(Bundle partialResults) {}

    @Override
    public void onEvent(int eventType, Bundle params) {}

    @Override
    public void onError(int error) {
        mBus.post(new VoiceRecognitionEvent(VoiceRecognitionResult.SPEECH_ERROR, error));
    }

    @Override
    public void onEndOfSpeech() {}

    @Override
    public void onBufferReceived(byte[] buffer) {}

    @Override
    public void onBeginningOfSpeech() {
        LogHelper.logVerbose("Speech starting");
    }
}
