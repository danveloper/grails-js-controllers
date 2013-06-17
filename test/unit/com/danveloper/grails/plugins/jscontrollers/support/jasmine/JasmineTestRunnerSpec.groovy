package com.danveloper.grails.plugins.jscontrollers.support.jasmine

import spock.lang.Specification

import javax.script.Bindings
import javax.script.Invocable
import javax.script.ScriptContext
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

/**
 * User: danielwoods
 * Date: 6/17/13
 */
class JasmineTestRunnerSpec extends Specification {
    static final def BASE_PATH = "/com/danveloper/grails/plugins/jscontrollers"
    static final def JASMINE_BOOTSTRAP_FILE = "/support/jasmine.bootstrap.js"
    static final def JASMINE_JS_FILE = "/support/jasmine.js"
    static final def SPEC_RUNNER_FILE = "/support/jasmine.spec.runner.js"

    private static ScriptEngine engine

    protected static JasmineSpecRunner runner

    def setupSpec() {
        ScriptEngineManager manager = new ScriptEngineManager();
        engine = manager.getEngineByName("nashorn");

        if (engine == null) engine = manager.getEngineByName("javascript");

        Bindings bindings = engine.createBindings();
        bindings.put("_jsr223Engine", engine);
        engine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);

        println "Boostrapping Jasmine..."
        load JASMINE_BOOTSTRAP_FILE

        println "Loading Jasmine Lib..."
        load JASMINE_JS_FILE

        println "Loading Spec Runner..."
        load SPEC_RUNNER_FILE

        def invocable = (Invocable)engine
        runner = invocable.getInterface(engine.get("JasmineSpecRunner"), JasmineSpecRunner)
    }

    void load(String script) {
        this.class.getResourceAsStream("${BASE_PATH}/$script").withReader { r ->
            load r
        }
    }

    void load(InputStream stream) {
        stream.withReader { r ->
            load r
        }
    }

    void load(Reader r) {
        engine.eval r
    }

    void exec() {
        runner.exec();
    }

    def getResults() {
        Thread.start {
            while (!runner.isFinished()) {
                Thread.sleep 1000
            }
            true
        }.join()
        runner.results.collect { specResult ->
            new JasmineSpecResult(
                    description: specResult.description,
                    passed: specResult.passedCount >= 1.0,
                    skipped: specResult.skipped
            )
        }
    }

}
