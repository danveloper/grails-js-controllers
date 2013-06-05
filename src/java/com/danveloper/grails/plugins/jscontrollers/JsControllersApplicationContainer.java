package com.danveloper.grails.plugins.jscontrollers;

import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.codehaus.groovy.grails.plugins.GrailsPluginManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.script.*;
import javax.servlet.ServletContext;
import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * User: danielwoods
 * Date: 6/3/13
 */
@Service
public class JsControllersApplicationContainer {
    public static final String SPRING_BEAN_ID = "jsControllerApplicationContainer";
    public static final String DISPATCHER_ID = "Dispatcher";

    public static final String CONTROLLER_DIR = "web-app/WEB-INF/js.controllers";
    public static final String APPLICATION_SCRIPT_NAME = "application.container.js";

    @Autowired
    protected GrailsApplication grailsApplication;
    @Autowired
    protected ServletContext servletContext;
    @Autowired
    protected GrailsPluginManager pluginManager;

    protected ScriptEngine engine;

    @PostConstruct
    public void init() {
        ScriptEngineManager manager = new ScriptEngineManager();
        engine = manager.getEngineByName("nashorn") != null ? manager.getEngineByName("rhino") : null;

        if (engine == null) engine = manager.getEngineByName("javascript");

        Bindings bindings = engine.createBindings();
        engine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);

        load("classpath:/js.controllers/" + APPLICATION_SCRIPT_NAME);

        List<String> beanNames = Arrays.asList(grailsApplication.getMainContext().getBeanDefinitionNames());
        for (String beanName : beanNames) {
            bindBeanByName(beanName);
        }
    }

    /**
     * Will evaluate and load a script within the JavaScript execution context.
     *
     * @param scriptName - Either a classpath reference using the convention "classpath:/path/to/file.js" or an on-disk location such as "/path/to/file.js"
     */
    public void load(String scriptName) {
        if (scriptName.startsWith("classpath:")) {
            try {
                InputStream inputStream = getClass().getResourceAsStream(scriptName.replaceAll("^classpath:",""));
                engine.eval(new InputStreamReader(inputStream));
            } catch (ScriptException e) {
                throw new RuntimeException(e);
            }
        } else {
            load(scriptName, getBaseDir());
        }
    }

    /**
     * Will evaluate and load a script within the the JavaScript execution context. Additional parameter of specifying a non-standard base directory to find the script
     * @param scriptName
     * @param baseDir
     */
    public void load(String scriptName, String baseDir) {
        try {
            InputStream inputStream = new FileInputStream(new File((baseDir+scriptName)));
            engine.eval(new InputStreamReader(inputStream));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Will return a reference to an object that exists in the JavaScript execution context.
     *
     * @param className
     * @return
     */
    public Object getObject(String className) {
        return engine.get(className);
    }

    /**
     * Returns an invocable instance of the scripting engine.
     * @return
     */
    public Invocable getInvocable() {
        return (Invocable)engine;
    }

    protected void bindBeanByName(String beanName) {
        try {
            Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put(beanName, grailsApplication.getMainContext().getBean(beanName));
        } catch (Exception e) {} // recover gracefully
    }

    protected String getBaseDir() {
        try {
            return new File(".", CONTROLLER_DIR).getCanonicalPath()+"/";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
