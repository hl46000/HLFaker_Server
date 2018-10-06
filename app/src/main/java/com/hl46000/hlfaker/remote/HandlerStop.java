package com.hl46000.hlfaker.remote;

import com.hl46000.hlfaker.RunCommand;

import java.lang.Thread.UncaughtExceptionHandler;

/**
 * Created by hl46000 on 11/2/17.
 */

public class HandlerStop implements UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        String broadcastCmd = "am broadcast -a android.intent.action.TIME_TICK --include-stopped-packages";
        new RunCommand().runRootCommand(broadcastCmd);
    }
}
