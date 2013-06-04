package com.danveloper.grails.plugins.jscontrollers;

import org.codehaus.groovy.grails.commons.GrailsApplication;
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
    @Autowired
    GrailsApplication grailsApplication;
    @Autowired
    ServletContext servletContext;

    private ScriptEngine engine;

    public static final String SPRING_BEAN_ID = "jsControllerApplicationContainer";
    public static final String APPLICATION_SCRIPT_NAME = "application.container.js";
    public static final String DISPATCHER_ID = "Dispatcher";
    public static final String CONTROLLER_DIR = "web-app/WEB-INF/js.controllers";

    @PostConstruct
    public void init() {
        ScriptEngineManager manager = new ScriptEngineManager();
        engine = manager.getEngineByName("nashorn") != null ? manager.getEngineByName("rhino") : null;

        if (engine == null) engine = manager.getEngineByName("javascript");

        Bindings bindings = engine.createBindings();
        engine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);

        register(APPLICATION_SCRIPT_NAME);

        List<String> beanNames = Arrays.asList(grailsApplication.getMainContext().getBeanDefinitionNames());
        for (String beanName : beanNames) {
            bindBeanByName(beanName);
        }
    }

    /**
     * Will evaluate and register a script within the JavaScript execution context.
     *
     * @param scriptName - Either a classpath reference using the convention "classpath:/path/to/file.js" or an on-disk location such as "/path/to/file.js"
     */
    public void register(String scriptName) {
        if (scriptName.startsWith("classpath:")) {
            register(scriptName, this.getClass().getClassLoader());
        } else {
            try {
                InputStream inputStream = new FileInputStream(new File((getBaseDir()+scriptName)));
                engine.eval(new InputStreamReader(inputStream));
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ScriptException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Will resolve a script from the classpath, using a provided {@link ClassLoader} class to resolve the resource.
     *
     * @param scriptName - Does <b>NOT</b> take a prepended "classpath:".
     * @param classLoader
     * @return
     */
    public Object register(String scriptName, ClassLoader classLoader) {
        try {
            InputStream inputStream = classLoader.getClass().getResourceAsStream(scriptName);
            return engine.eval(new InputStreamReader(inputStream));
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

    private void bindBeanByName(String beanName) {
        try {
            Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put(beanName, grailsApplication.getMainContext().getBean(beanName));
        } catch (Exception e) {} // recover gracefully
    }

    private String getBaseDir() {
        try {
            return new File(".", CONTROLLER_DIR).getCanonicalPath()+"/";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
