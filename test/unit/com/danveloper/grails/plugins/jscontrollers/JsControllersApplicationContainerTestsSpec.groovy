package com.danveloper.grails.plugins.jscontrollers

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.context.ApplicationContext
import spock.lang.Specification

/**
 * User: danielwoods
 * Date: 6/4/13
 */
class JsControllersApplicationContainerTestsSpec extends Specification {

    def "test container initialization"() {
        setup:
        GrailsApplication grailsApplication = Mock()
        ApplicationContext applicationContext = Mock()
        grailsApplication.getMainContext() >> applicationContext
        applicationContext.getBeanDefinitionNames() >> []

        when:
        def container = new JsControllersApplicationContainer(grailsApplication: grailsApplication)
        container.init()

        then:
        assert container.engine

    }

    def "test beans bound"() {
        setup:
        GrailsApplication grailsApplication = Mock()
        ApplicationContext applicationContext = Mock()
        grailsApplication.getMainContext() >> applicationContext
        applicationContext.getBeanDefinitionNames() >> (['grails'] as String[])

        when:
        def called = false
        def container = Spy(JsControllersApplicationContainer)
        container.grailsApplication = grailsApplication
        container.bindBeanByName(_) >> { String name -> called = true }
        container.init()

        then:
        assert called
    }

    def "test container is retrievable"() {
        setup:
        GrailsApplication grailsApplication = Mock()
        ApplicationContext applicationContext = Mock()
        grailsApplication.getMainContext() >> applicationContext
        applicationContext.getBeanDefinitionNames() >> []

        when:
        def container = new JsControllersApplicationContainer(grailsApplication: grailsApplication)
        container.init()
        container.load("/test.js", new File(".", "/test/unit/com/danveloper/grails/plugins/jscontrollers").absolutePath)

        then:
        assert container.getObject("TestContainer") != null
    }

    def "test reloading"() {
        setup:
        GrailsApplication grailsApplication = Mock()
        ApplicationContext applicationContext = Mock()
        grailsApplication.getMainContext() >> applicationContext
        applicationContext.getBeanDefinitionNames() >> []
        def container = Spy(JsControllersApplicationContainer)
        container.grailsApplication = grailsApplication
        container.init()
        def path = new File(".", "/test/unit/com/danveloper/grails/plugins/jscontrollers").absolutePath
        def scriptName = "/test.js"
        container.load(scriptName, path)

        when:
        def called = false
        container.reloadContainer() >> { called = true }
        container.reload("${path}${scriptName}")

        then:
        assert called
    }

    def "test not reloading stuff that isnt ours"() {
        setup:
        GrailsApplication grailsApplication = Mock()
        ApplicationContext applicationContext = Mock()
        grailsApplication.getMainContext() >> applicationContext
        applicationContext.getBeanDefinitionNames() >> []
        def container = Spy(JsControllersApplicationContainer)
        container.grailsApplication = grailsApplication
        container.init()
        def path = new File(".", "/test/unit/com/danveloper/grails/plugins/jscontrollers").absolutePath
        def scriptName = "/test.js"
        container.load(scriptName, path)

        when:
        def called = false
        container.reloadContainer() >> { called = true }
        container.reload("./blah")

        then:
        assert !called
    }
}
