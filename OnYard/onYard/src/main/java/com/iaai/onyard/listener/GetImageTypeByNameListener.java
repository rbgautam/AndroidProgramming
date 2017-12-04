package com.iaai.onyard.listener;

import android.content.Context;

import com.iaai.onyardproviderapi.classes.ImageTypeInfo;

public interface GetImageTypeByNameListener {

    void onImageTypeRetrieved(ImageTypeInfo imageType, Context context);
}
