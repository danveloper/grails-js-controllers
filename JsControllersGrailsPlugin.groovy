import com.danveloper.grails.plugins.jscontrollers.GrailsJsControllersHandlerMapping
import com.danveloper.grails.plugins.jscontrollers.GrailsJsSpringController
import com.danveloper.grails.plugins.jscontrollers.JsControllersApplicationContainer
import com.danveloper.grails.plugins.jscontrollers.JsControllersFilter
import com.danveloper.grails.plugins.jscontrollers.JsControllersRequestDispatcher

class JsControllersGrailsPlugin {
    // the plugin version
    def version = "1.0-SNAPSHOT"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.0 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp",
            "grails-app/views/index.gsp",
            "grails-app/controllers/**/*.groovy",
            "web-app/css/**/*.*",
            "web-app/js/**/*.*",
            "web-app/images/**/*.*",
            "grails-app/resourceMappers/**/test/*",
            "grails-app/conf/*Resources.groovy"
    ]

    // TODO Fill in these fields
    def title = "Js Controllers Plugin" // Headline display name of the plugin
    def author = "Your name"
    def authorEmail = ""
    def description = '''\
Brief summary/description of the plugin.
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/js-controllers"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
//    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
//    def organization = [ name: "My Company", url: "http://www.my-company.com/" ]

    // Any additional developers beyond the author specified above.
//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

    // Location of the plugin's issue tracker.
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // Online location of the plugin's browseable source code.
//    def scm = [ url: "http://svn.codehaus.org/grails-plugins/" ]

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

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { applicationContext ->
        // TODO Implement post initialization spring config (optional)
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

    def onShutdown = { event ->
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}
