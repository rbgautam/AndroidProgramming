package com.iaai.onyard.listener;

import java.util.ArrayList;

import com.iaai.onyard.classes.EnhancementField;

public interface GetEnhancementFieldsListener {

    void onEnhancementFieldsRetrieved(ArrayList<EnhancementField> fields);
}
