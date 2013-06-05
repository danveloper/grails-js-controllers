package com.danveloper.grails.plugins.jscontrollers

import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest
import org.springframework.util.Assert
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.mvc.Controller

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * User: danielwoods
 * Date: 6/3/13
 */
class GrailsJsSpringController implements Controller {
    JsControllersRequestDispatcher dispatcher

    @Override
    ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes()
        Assert.state(ra instanceof GrailsWebRequest, "Bound RequestContext is not an instance of GrailsWebRequest")
        GrailsWebRequest webRequest = ra

        webRequest.with {
            params.put "out", out

            Object result = dispatcher.dispatch(controllerName, actionName, params)

            if (result != null) {
                new ModelAndView("/${controllerName}/${actionName}", (Map)result)
            } else {
                null
            }
        }
    }
}
