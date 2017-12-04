package com.iaai.onyard.performancetest;

import java.util.Calendar;

import android.content.Context;

import com.iaai.onyard.application.OnYard;
import com.iaai.onyard.application.OnYard.LogMode;
import com.iaai.onyard.utility.LogHelper;

/**
 * Object that marks start/end times of a method's execution and writes performance data
 * to the log. This class's methods do nothing if debug mode is disabled.
 */
public class Timer {
    /**
     * Name of the method that is having its performance measured.
     */
    private final String MethodName;
    /**
     * Start time of the method's execution.
     */
    private long startTime;
    /**
     * End time of the method's execution.
     */
    private long endTime;

    /**
     * Constructor with name of method, for log identification.
     * 
     * @param methodName The name of the method which shall have its performance measured.
     */
    public Timer(String methodName)
    {
        MethodName = methodName;
    }

    /**
     * Mark start time of the method execution.
     */
    public void start()
    {
        startTime = Calendar.getInstance().getTimeInMillis();
    }

    /**
     * Mark end time of the method execution.
     */
    public void end()
    {
        endTime = Calendar.getInstance().getTimeInMillis();
    }

    public void logVerbose() {
        if (OnYard.LOG_MODE == LogMode.VERBOSE) {
            LogHelper.logVerbose(MethodName + " executed in " + (endTime - startTime) + " ms");
        }
    }

    /**
     * Make an info log entry containing method name and performance data.
     * 
     * @param context The current context.
     */
    public void logInfo(Context context)
    {
        if(OnYard.LOG_MODE == LogMode.DEBUG ||
                OnYard.LOG_MODE == LogMode.VERBOSE ||
                OnYard.LOG_MODE == LogMode.INFO) {
            LogHelper.logInfo(context,
                    MethodName + " executed in " + (endTime - startTime) + " ms", this.getClass()
                    .getSimpleName());
        }
    }
}
