package com.danveloper.grails.plugins.jscontrollers

import org.codehaus.groovy.grails.web.servlet.GrailsControllerHandlerMapping
import org.springframework.web.servlet.mvc.Controller

import javax.servlet.http.HttpServletRequest

/**
 * User: danielwoods
 * Date: 6/3/13
 */
class GrailsJsControllersHandlerMapping extends GrailsControllerHandlerMapping {
    @Override
    protected Object getHandlerInternal(HttpServletRequest request) {
        return getWebApplicationContext().getBean(GrailsJsSpringController) as Controller
    }
}
