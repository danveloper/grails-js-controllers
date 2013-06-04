package com.danveloper.grails.plugins.jscontrollers;

import grails.util.Metadata;
import grails.web.UrlConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.codehaus.groovy.grails.commons.ControllerArtefactHandler;
import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.codehaus.groovy.grails.commons.GrailsClass;
import org.codehaus.groovy.grails.compiler.GrailsProjectWatcher;
import org.codehaus.groovy.grails.web.errors.GrailsExceptionResolver;
import org.codehaus.groovy.grails.web.mapping.RegexUrlMapping;
import org.codehaus.groovy.grails.web.mapping.UrlMapping;
import org.codehaus.groovy.grails.web.mapping.UrlMappingInfo;
import org.codehaus.groovy.grails.web.mapping.UrlMappingsHolder;
import org.codehaus.groovy.grails.web.mapping.filter.UrlMappingsFilter;
import org.codehaus.groovy.grails.web.servlet.WrappedResponseHolder;
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest;
import org.codehaus.groovy.grails.web.util.WebUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: danielwoods
 * Date: 6/3/13
 */
public class JsControllersFilter extends OncePerRequestFilter {
    public static final boolean WAR_DEPLOYED = Metadata.getCurrent().isWarDeployed();
    private UrlPathHelper urlHelper = new UrlPathHelper();
    private static final Log LOG = LogFactory.getLog(UrlMappingsFilter.class);
    private GrailsApplication application;
    private UrlConverter urlConverter;
    private JsControllersRequestDispatcher jsControllersRequestDispatcher;

    @Override
    protected void initFilterBean() throws ServletException {
        super.initFilterBean();
        urlHelper.setUrlDecode(false);
        final ServletContext servletContext = getServletContext();
        final WebApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        application = WebUtils.lookupApplication(servletContext);
        ApplicationContext mainContext = application.getMainContext();
        urlConverter = mainContext.getBean(UrlConverter.BEAN_NAME, UrlConverter.class);

        if (applicationContext.containsBean(JsControllersApplicationContainer.SPRING_BEAN_ID)) {
            jsControllersRequestDispatcher = applicationContext.getBean(JsControllersRequestDispatcher.class);
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        UrlMappingsHolder holder = WebUtils.lookupUrlMappings(getServletContext());

        String uri = urlHelper.getPathWithinApplication(request);
        if (!"/".equals(uri) && noRegexMappings(holder)) {
            // not index request, no controllers, and no URL mappings for views, so it's not a Grails request
            processFilterChain(request, response, filterChain);
            return;
        }

        if (isUriExcluded(holder, uri)) {
            processFilterChain(request, response, filterChain);
            return;
        }

        GrailsWebRequest webRequest = new GrailsWebRequest(request, response, getServletContext());
        WebUtils.storeGrailsWebRequest(webRequest);
        UrlMappingInfo[] urlInfos = holder.matchAll(uri);
        WrappedResponseHolder.setWrappedResponse(response);
        boolean dispatched = false;
        try {
            for (UrlMappingInfo info : urlInfos) {
                if (info != null) {
                    final RequestDispatcher dispatcher;
                    try {
                        info.configure(webRequest);
                        String action = info.getActionName() == null ? "" : info.getActionName();
                        final String controllerName = info.getControllerName();
                        String pluginName = info.getPluginName();
                        String featureUri = WebUtils.SLASH + urlConverter.toUrlElement(controllerName) + WebUtils.SLASH + urlConverter.toUrlElement(action);

                        Object featureId = null;
                        if (pluginName != null) {
                            Map featureIdMap = new HashMap();
                            featureIdMap.put("uri", featureUri);
                            featureIdMap.put("pluginName", pluginName);
                            featureId = featureIdMap;
                        } else {
                            featureId = featureUri;
                        }
                        GrailsClass controller = application.getArtefactForFeature(ControllerArtefactHandler.TYPE, featureId);
                        if (controller != null || !jsControllersRequestDispatcher.isHandled(controllerName)) {
                            continue;
                        }

                        String forwardUrl = WebUtils.SLASH + "grails" + WebUtils.SLASH + "app" + WebUtils.SLASH +
                                info.getControllerName() + WebUtils.SLASH + info.getActionName() + ".jscontroller";

                        dispatcher = request.getRequestDispatcher(forwardUrl);

                    } catch (Exception e) {
                        if (e instanceof MultipartException) {
                            reapplySitemesh(request);
                            throw ((MultipartException) e);
                        }
                        LOG.error("Error when matching URL mapping [" + info + "]:" + e.getMessage(), e);
                        continue;
                    }

                    dispatched = true;

                    if (!WAR_DEPLOYED) {
                        checkDevelopmentReloadingState(request);
                    }

                    request = checkMultipart(request);

                    dispatcher.forward(request, response);

                    break;
                }
            }
        } finally {
            WrappedResponseHolder.setWrappedResponse(null);
        }

        if (!dispatched) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("No match found, processing remaining filter chain.");
            }
            processFilterChain(request, response, filterChain);
        }
    }

    public static boolean isUriExcluded(UrlMappingsHolder holder, String uri) {
        boolean isExcluded = false;
        @SuppressWarnings("unchecked")
        List<String> excludePatterns = holder.getExcludePatterns();
        if (excludePatterns != null && excludePatterns.size() > 0) {
            for (String excludePattern : excludePatterns) {
                int wildcardLen = 0;
                if (excludePattern.endsWith("**")) {
                    wildcardLen = 2;
                } else if (excludePattern.endsWith("*")) {
                    wildcardLen = 1;
                }
                if (wildcardLen > 0) {
                    excludePattern = excludePattern.substring(0, excludePattern.length() - wildcardLen);
                }
                if ((wildcardLen == 0 && uri.equals(excludePattern)) || (wildcardLen > 0 && uri.startsWith(excludePattern))) {
                    isExcluded = true;
                    break;
                }
            }
        }
        return isExcluded;
    }

    private boolean noRegexMappings(UrlMappingsHolder holder) {
        for (UrlMapping mapping : holder.getUrlMappings()) {
            if (mapping instanceof RegexUrlMapping) {
                return false;
            }
        }
        return true;
    }

    private void checkDevelopmentReloadingState(HttpServletRequest request) {
        while (GrailsProjectWatcher.isReloadInProgress()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // ignore
            }
        }
        if (request.getAttribute(GrailsExceptionResolver.EXCEPTION_ATTRIBUTE) != null) return;
        MultipleCompilationErrorsException compilationError = GrailsProjectWatcher.getCurrentCompilationError();
        if (compilationError != null) {
            throw compilationError;
        }
        Throwable currentReloadError = GrailsProjectWatcher.getCurrentReloadError();
        if (currentReloadError != null) {
            throw new RuntimeException(currentReloadError);
        }
    }

    protected HttpServletRequest checkMultipart(HttpServletRequest request) throws MultipartException {
        // Lookup from request attribute. The resolver that handles MultiPartRequest is dealt with earlier inside DefaultUrlMappingInfo with Grails
        HttpServletRequest resolvedRequest = (HttpServletRequest) request.getAttribute(MultipartHttpServletRequest.class.getName());
        if (resolvedRequest != null) return resolvedRequest;
        return request;
    }

    private void reapplySitemesh(HttpServletRequest request) {
        request.removeAttribute("com.opensymphony.sitemesh.APPLIED_ONCE");
    }

    private void processFilterChain(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        try {
            WrappedResponseHolder.setWrappedResponse(response);
            if (filterChain != null) {
                filterChain.doFilter(request, response);
            }
        } finally {
            WrappedResponseHolder.setWrappedResponse(null);
        }
    }
}
