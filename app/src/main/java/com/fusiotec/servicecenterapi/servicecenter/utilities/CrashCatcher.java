package com.fusiotec.servicecenterapi.servicecenter.utilities;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Created by Owner on 7/11/2016.
 */
public class CrashCatcher implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler defaultUEH;

    public CrashCatcher(){
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
    }
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        String stacktrace = result.toString();
        printWriter.close();
        try{
            Utils.saveToErrorLogs(stacktrace);
            defaultUEH.uncaughtException(t, e);
        }catch(Exception ex){
            defaultUEH.uncaughtException(t, ex);
        }
    }
}
