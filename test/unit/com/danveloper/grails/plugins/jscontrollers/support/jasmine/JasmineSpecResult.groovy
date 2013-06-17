package com.danveloper.grails.plugins.jscontrollers.support.jasmine;

/**
 * User: danielwoods
 * Date: 6/17/13
 */
class JasmineSpecResult {
    String description;
    boolean passed;
    boolean skipped;

    @Override
    public String toString() {
        "[ \"$description\": ${passed ? 'passed' : 'failed'} ]"
    }
}
