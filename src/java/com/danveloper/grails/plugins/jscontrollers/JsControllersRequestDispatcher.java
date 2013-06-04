package com.danveloper.grails.plugins.jscontrollers;

import javax.script.Invocable;
import javax.script.ScriptException;
import java.util.Map;

/**
 * User: danielwoods
 * Date: 6/3/13
 */
public class JsControllersRequestDispatcher {
    private JsControllersApplicationContainer jsControllersApplicationContainer;

    public Object dispatch(String controller, String action, Map params) {
        Object dispatcher = jsControllersApplicationContainer
                .getObject(JsControllersApplicationContainer.DISPATCHER_ID);
        Invocable invocable = jsControllersApplicationContainer.getInvocable();
        try {
            return invocable.invokeMethod(dispatcher, "dispatch", controller, action, params);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isHandled(String controller) {
        Object dispatcher = jsControllersApplicationContainer
                .getObject(JsControllersApplicationContainer.DISPATCHER_ID);
        Invocable invocable = jsControllersApplicationContainer.getInvocable();
        try {
            Object result = invocable.invokeMethod(dispatcher, "isHandled", controller);
            return (Boolean) result;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    public void setJsControllersApplicationContainer(JsControllersApplicationContainer jsControllersApplicationContainer) {
        this.jsControllersApplicationContainer = jsControllersApplicationContainer;
    }
}
