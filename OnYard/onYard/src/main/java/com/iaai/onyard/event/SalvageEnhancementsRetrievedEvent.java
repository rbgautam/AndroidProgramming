package com.iaai.onyard.event;

import java.util.ArrayList;

import com.iaai.onyardproviderapi.classes.SalvageEnhancementInfo;


public class SalvageEnhancementsRetrievedEvent {

    private final ArrayList<SalvageEnhancementInfo> mSalvageEnhancementsList;

    public SalvageEnhancementsRetrievedEvent(ArrayList<SalvageEnhancementInfo> enhancements) {
        mSalvageEnhancementsList = enhancements;
    }

    public ArrayList<SalvageEnhancementInfo> getSalvageEnhancementsList() {
        return mSalvageEnhancementsList;
    }
}
