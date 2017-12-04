package com.iaai.onyard.listener;

import java.util.ArrayList;

import com.iaai.onyardproviderapi.classes.ImageReshootInfo;

public interface GetReshootsInfoListener {

    void onReshootsInfoRetrieved(ArrayList<ImageReshootInfo> reshoots);
}
