package com.danveloper.grails.plugins.jscontrollers.support.jasmine;

/**
 * User: danielwoods
 * Date: 6/17/13
 */
public interface JasmineSpecRunner {
    Object[] getResults();
    void exec();
    boolean isFinished();
}
