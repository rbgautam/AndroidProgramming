package com.iaai.onyard.listener;

import java.util.HashMap;

import com.iaai.onyardproviderapi.classes.ImageCaptionInfo;

public interface PopulateCaptionsListener {

    void onCaptionsPopulated(HashMap<Integer, ImageCaptionInfo> orderCaptionMap);
}
