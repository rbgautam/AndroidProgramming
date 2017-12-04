package com.iaai.onyard.filter;

import android.text.InputFilter;
import android.text.Spanned;

import com.iaai.onyard.utility.DataHelper;


public class InputFilterAlphaNumeric implements InputFilter {

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart,
            int dend) {
        for (int i = start; i < end; i++) {
            if (!DataHelper.isAsciiLetterOrNumber(source.charAt(i))) {
                return "";
            }
        }
        return null;
    }
}
