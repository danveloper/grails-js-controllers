import com.danveloper.grails.plugins.jscontrollers.GrailsJsControllersHandlerMapping
import com.danveloper.grails.plugins.jscontrollers.GrailsJsSpringController
import com.danveloper.grails.plugins.jscontrollers.JsControllersApplicationContainer
import com.danveloper.grails.plugins.jscontrollers.JsControllersFilter
import com.danveloper.grails.plugins.jscontrollers.JsControllersRequestDispatcher

class JsControllersGrailsPlugin {
    def version = "1.0"

    def grailsVersion = "2.0 > *"
    def pluginExcludes = [
            "grails-app/domain/**",
            "grails-app/views/error.gsp",
            "grails-app/views/index.gsp",
            "grails-app/controllers/**/*.groovy",
            "web-app/css/**/*.*",
            "web-app/js/**/*.*",
            "web-app/images/**/*.*",
    ]

    def title = "JavaScript Controller Plugin"
    def author = "Daniel Woods"
    def authorEmail = "daniel.p.woods@gmail.com"
    def description = 'Plugin to allow for handling controller actions in JavaScript'
    def documentation = "http://grails.org/plugin/js-controllers"
    def license = "APACHE"
    def organization = [ name: "danveloper", url: "http://www.danveloper.com/" ]
    def developers = [ [ name: "Dan Woods", email: "daniel.p.woods@gmail.com", twitter: "@danveloper" ]]
    def issueManagement = [ system: "GITHUB", url: "https://github.com/danveloper/grails-js-controllers/issues" ]
    def scm = [ url: "https://github.com/danveloper/grails-js-controllers" ]

    def doWithWebDescriptor = { webXml ->
        def mappingElement = webXml.'servlet-mapping'
        mappingElement = mappingElement[mappingElement.size() - 1]

        mappingElement + {
            'servlet-mapping' {
                'servlet-name'("grails")
                'url-pattern'("*.jscontroller")
            }
        }

        def contextParam = webXml.'context-param'

        contextParam[contextParam.size() - 1] + {
            'filter' {
                'filter-name'('jsControllersFilter')
                'filter-class'(JsControllersFilter.name)
            }
        }

        def filter = webXml.'filter'
        filter[filter.size() - 1] + {
            'filter-mapping'{
                'filter-name'('jsControllersFilter')
                'url-pattern'('/*')
            }
        }
    }

    def doWithSpring = {
        "${JsControllersApplicationContainer.SPRING_BEAN_ID}"(JsControllersApplicationContainer)

        jsControllersRequestDispatcher(JsControllersRequestDispatcher) {
            jsControllersApplicationContainer = ref(JsControllersApplicationContainer.SPRING_BEAN_ID)
        }

        grailsJsSpringController(GrailsJsSpringController) {
            dispatcher = ref('jsControllersRequestDispatcher')
        }


        jsControllerHandlerMapping(GrailsJsControllersHandlerMapping)

    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }
}
