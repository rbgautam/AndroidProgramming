package com.iaai.onyard.event;

public class ChangeCameraParamsEvent {

    public enum CameraParamChange {
        VOICE_COMMANDS_DISABLE, VOICE_COMMANDS_ENABLE
    }

    private final CameraParamChange mChange;

    public ChangeCameraParamsEvent(CameraParamChange result) {
        mChange = result;
    }

    public CameraParamChange getChange() {
        return mChange;
    }

}
