println "${basedir} ${pluginBasedir}"
ant.mkdir(dir:"${basedir}/web-app/WEB-INF/js.controllers")
ant.copy(file:"${pluginBasedir}/grails-app/js.controllers/application.container.js", todir: "${basedir}/web-app/WEB-INF/js.controllers")