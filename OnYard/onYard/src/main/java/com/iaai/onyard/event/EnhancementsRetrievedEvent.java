package com.iaai.onyard.event;

import java.util.ArrayList;

import com.iaai.onyardproviderapi.classes.EnhancementInfo;


public class EnhancementsRetrievedEvent {

    private final ArrayList<EnhancementInfo> mEnhancementsList;

    public EnhancementsRetrievedEvent(ArrayList<EnhancementInfo> enhancements) {
        mEnhancementsList = enhancements;
    }

    public ArrayList<EnhancementInfo> getEnhancementsList() {
        return mEnhancementsList;
    }
}
