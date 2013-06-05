package com.danveloper.grails.plugins.jscontrollers

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.context.ApplicationContext
import spock.lang.Specification

import javax.script.Invocable

/**
 * User: danielwoods
 * Date: 6/4/13
 */
class JsControllersRequestDispatcherTestsSpec extends Specification {

    def "test dispatching"() {
        setup:
        GrailsApplication grailsApplication = Mock()
        ApplicationContext applicationContext = Mock()
        grailsApplication.getMainContext() >> applicationContext
        applicationContext.getBeanDefinitionNames() >> []
        def container = Spy(JsControllersApplicationContainer)
        container.grailsApplication = grailsApplication
        container.init()
        container.getObject(_) >> true
        def invocable = Spy(Invocable)
        container.getInvocable() >> invocable
        def dispatched = false
        invocable.invokeMethod(_, _, _) >> { dispatched = true }
        def dispatcher = new JsControllersRequestDispatcher(jsControllersApplicationContainer: container)

        when:
        dispatcher.dispatch("foo", "bar", [:])

        then:
        assert dispatched
    }

    def "test handling"() {
        setup:
        GrailsApplication grailsApplication = Mock()
        ApplicationContext applicationContext = Mock()
        grailsApplication.getMainContext() >> applicationContext
        applicationContext.getBeanDefinitionNames() >> []
        def container = Spy(JsControllersApplicationContainer)
        container.grailsApplication = grailsApplication
        container.init()
        container.getObject(_) >> true
        def invocable = Spy(Invocable)
        container.getInvocable() >> invocable
        def handled = false
        invocable.invokeMethod(_, _, _) >> { handled = true }
        def dispatcher = new JsControllersRequestDispatcher(jsControllersApplicationContainer: container)

        when:
        dispatcher.isHandled("foo")

        then:
        assert handled
    }
}
