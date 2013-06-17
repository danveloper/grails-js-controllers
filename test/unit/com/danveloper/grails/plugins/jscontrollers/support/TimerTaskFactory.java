package com.danveloper.grails.plugins.jscontrollers.javascript;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import java.util.TimerTask;

/**
 * User: danielwoods
 * Date: 6/17/13
 */
public class TimerTaskFactory {

    public static TimerTask createTimerTask(ScriptEngine engine, final Object callable) {
        final Invocable invocable = (Invocable) engine;

        return new TimerTask() {
            @Override
            public void run() {
                try {
                    invocable.invokeMethod(callable, "call");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}
