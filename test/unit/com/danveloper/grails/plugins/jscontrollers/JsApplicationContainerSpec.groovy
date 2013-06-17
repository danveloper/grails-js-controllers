package com.danveloper.grails.plugins.jscontrollers

import com.danveloper.grails.plugins.jscontrollers.support.jasmine.JasmineTestRunnerSpec

/**
 * User: danielwoods
 * Date: 6/17/13
 */
class JsApplicationContainerSpec extends JasmineTestRunnerSpec {

    static final def CONTAINER = new FileInputStream(new File("./grails-app/conf/js.controllers/application.container.js"))
    final def APP_SPEC_JS = "/js.spec/application.container.spec.js"
    final def DISPATCHER_JS = "/js.spec/dispatcher.spec.js"

    def setupSpec() {
        load CONTAINER
    }

    def "test container is initialized"() {
        setup:
        load APP_SPEC_JS

        when:
        exec()

        then:
        assert !(false in results*.passed)
    }

    def "test dispatcher"() {
        setup:
        load DISPATCHER_JS

        when:
        exec()

        then:
        assert !(false in results*.passed)
    }
}
