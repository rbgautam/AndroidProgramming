package com.iaai.onyard.utility;

import com.iaai.onyard.application.OnYard;
import com.iaai.onyard.classes.OnYardFieldOption;


public class EnhancementFieldHelper {

    public static OnYardFieldOption getCompleteOption() {
        return new OnYardFieldOption(OnYard.EnhancementOptions.COMPLETE_DISPLAY_NAME,
                OnYard.EnhancementOptions.COMPLETE_VALUE);
    }

    public static OnYardFieldOption getNaOption() {
        return new OnYardFieldOption(OnYard.EnhancementOptions.NA_DISPLAY_NAME,
                OnYard.EnhancementOptions.NA_VALUE);
    }

    public static OnYardFieldOption getRequestApprovalOption() {
        return new OnYardFieldOption(OnYard.EnhancementOptions.REQUEST_APPROVAL_DISPLAY_NAME,
                OnYard.EnhancementOptions.REQUEST_APPROVAL_VALUE);
    }


}
