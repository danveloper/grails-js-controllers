import com.danveloper.grails.plugins.jscontrollers.GrailsJsControllersHandlerMapping
import com.danveloper.grails.plugins.jscontrollers.GrailsJsSpringController
import com.danveloper.grails.plugins.jscontrollers.JsControllersApplicationContainer
import com.danveloper.grails.plugins.jscontrollers.JsControllersFilter
import com.danveloper.grails.plugins.jscontrollers.JsControllersRequestDispatcher
import org.springframework.context.ApplicationContext

class JsControllersGrailsPlugin {
    def version = "1.2-SNAPSHOT"

    def grailsVersion = "2.0 > *"

    def title = "JavaScript Controller Plugin"
    def description = 'Plugin to allow for handling controller actions in JavaScript'
    def documentation = "http://grails.org/plugin/js-controllers"
    def license = "APACHE"
    def organization = [ name: "danveloper", url: "http://www.danveloper.com/" ]
    def developers = [ [ name: "Dan Woods", email: "daniel.p.woods@gmail.com", twitter: "@danveloper" ]]
    def issueManagement = [ system: "GITHUB", url: "https://github.com/danveloper/grails-js-controllers/issues" ]
    def scm = [ url: "https://github.com/danveloper/grails-js-controllers" ]

    def watchedResources = ["file:./web-app/**/*.*"]

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
        ApplicationContext ctx = event.ctx
        def applicationContainer = ctx.getBean(JsControllersApplicationContainer)
        if (event.source?.path) {
            applicationContainer.reload((String)event.source.path)
        }
    }
}
