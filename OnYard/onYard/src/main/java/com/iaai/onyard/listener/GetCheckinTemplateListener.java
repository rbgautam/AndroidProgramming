package com.iaai.onyard.listener;

import java.util.ArrayList;

import com.iaai.onyard.classes.CheckinField;

public interface GetCheckinTemplateListener {

    void onCheckinTemplateRetrieved(ArrayList<CheckinField> fields);
}
