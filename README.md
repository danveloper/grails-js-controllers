Grails JavaScript Controller Plugin
=====================

This plugin offers developers the ability to write classes in JavaScript, and have Grails respect them as controllers. The default path from which the plugin resolves controllers is the `web-app/WEB-INF/js.controllers` directory. This directory is automatically created at plugin installation time.

Controllers
---
In the same way that Grails controllers are little more than (albeit enhanced) Groovy classes, JavaScript controllers can be any class that has a corresponding method to handle the incoming action.

```javascript

function PaymentController() {

	this.index = function(params) {
	    return {foo: 'bar', c: "payment"};
	}

}

ApplicationContainer.register("payment.controller", PaymentController, { scope: "prototype" });

```

The above example defines a controller and registers it within the Grails context. The key to the mapping is the first parameter to the `register` method, which declared the path that is to be handled by this controller. JavaScript controllers are resolved by convention given a **.controller** suffix. In this example, requests to **/payment/index** will delegate to the `index` function of the `PaymentController` class. All JavaScript controllers must register themselves as `path`.`controller` within the `ApplicationContainer`.

Actions should return a JavaScript object representing the model. This return object will be coerced to a Map before being delegated to the view.

Once you have a JavaScript controller built and registered within the ApplicationContainer, you must also register it with the JavaScript execution environment. Scripts are resolved relative to the `web-app/WEB-INF/js.controllers` directory and can be registered by using the `register(String)` method off of the `JsControllersApplicationContainer` class. Accessing this class must be performed through dependency injection or application context lookup.

For example, you may register a JavaScript controller at application initialization from your `BootStrap.groovy` class:

```groovy
class BootStrap {

    def grailsApplication

    def init = { servletContext ->
        def container = grailsApplication.mainContext.getBean(JsControllersApplicationContainer)
        container.register("payment.controller.js")
    }
    def destroy = {
    }
}
```

You can think about registration as a two-step process: the first step is to register your script within the JavaScript execution context; the second step is to have your script register itself with the ApplicationContainer.

This registration process can also be performed dynamically and at runtime. As long as each JavaScript controller registers itself within the `ApplicationContainer`, and follows the conventions outlined above, the controllers will be resolved for otherwise unmapped urls.

Scripts can also be registered from the classpath. Using the **classpath:** prefix to the script name when calling the `register(String)` method will instruct the `ApplicationContainer` to resolve the script from the classpath instead of from a path relative to the `web-app/WEB-INF/js.controllers` directory.

```groovy 
class BootStrap {

    def grailsApplication

    def init = { servletContext ->
        def container = grailsApplication.mainContext.getBean(JsControllersApplicationContainer)
        container.register("classpath:controllers/payment.controller.js")
    }
    def destroy = {
    }
}
```

Parameters
---
Each controller action should take a single argument. This argument is a Map of the parameters from the request. Parameters need to be explicitly retrieved and set, as the following example demonstrates.

```javascript
function PaymentController() {

    this.index = function(params) {
        return {foo: 'bar', c: "payment", name: params.get("name")};
    }

}
```

While the params object allows for explicit `get` and `put` operations, it also allows you direct access to the `request` object from within your JavaScript controller action. The request object is an instance of an HttpServletRequest and can be treated as such.

Services
---
The plugin exposes the beans from the Grails application to the JavaScript controllers. This means that the JavaScript controller has access to the extent of the Grails application context. Accessing services or other controllers is as simple as directly accessing the "wired" bean. Beans are wired into the JavaScript execution context by name, so referencing them from the controller should be handled as regular in the same manner as accessing a property.

Given a Grails Service, `MyGrailsService`:

```groovy
class MyGrailsService {

	def serviceMethod(String arg) {
		// ... do some stuff ...
	}

}
```

Call the `serviceMethod`:

```javascript
function PaymentController() {

	this.index = function(params) {
		var result = myGrailsService.serviceMethod("my optional parameters")
      
		return {result: result};
	}

}
```

Views
---
Views for JavaScript controllers are resolved in the same manner as views from regular Grails controllers. By convention, a controller action for **/payment/index** will resolve the `views/payment/index.gsp` view.

Nothing special needs to be done to make GSPs work with JavaScript controllers. The JavaScript object that is returned from the controller action is coerced to a Java Map and delegated to the controller in the same way that a regular Groovy controller would handle an action.

Given the `PaymentController`:

```javascript
function PaymentController() {

    this.index = function(params) {
        return {name: "Payment Controller"};
    }

}
```

And the GSP `views/payment/index.gsp`:

```jsp
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>Payment Controller Index</title>
</head>
<body>
<h1>${name}</h1>
</body>
</html>
```

We see a H1 rendered with "Payment Controller", just as you would expect. Sitemeshing, TagLibs, and Resources also work as expected.

Using Libraries
---
The plugin allows you to leverage additional libraries within your JavaScript controllers. It is important to note that the JavaScript execution environment is different from the Grails application. That means that resources that are to be made accessible for your JavaScript controllers ___must___ be explicitly registered.

Registering a library script is as simple as registering a controller. Scripts can be resolved from either the classpath or from a physical path relative to the `web-app/WEB-INF/js.controllers` path. A good practice may be to store libraries in their own sub-directory.

```groovy
class BootStrap {

    def grailsApplication

    def init = { servletContext ->
        def container = grailsApplication.mainContext.getBean(JsControllersApplicationContainer)
        container.register("libs/payment.library.js")
        container.register("payment.controller.js")
    }
    def destroy = {
    }
}
```

Like controllers, libraries must also be registered with the `ApplicationContainer`.

```javascript
function PaymentLibrary() {
    this.makePayment = function(cardnum, expiraion) {
        // ... do some awesome javascript stuff ...
        return obj;
    };
}

ApplicationContainer.register("payment.library", PaymentLibrary, { scope: 'singleton' });
```

Classes can be registered with the `ApplicationContainer` in either a **Prototype** or a **Singleton** scope. While controllers should be scoped prototype, it may make sense for your requirement to register instances of your libraries as singletons. The above example demonsrates that process.

Once your library is registered with the `ApplicationContainer`, you can retrieve it for use in your controller through the `getBean(String)` method.

```javascript
function PaymentController() {

    this.paymentLibrary = $.getBean('payment.library');

    this.index = function(params) {
        return {name: "Payment Controller", payment: this.paymentLibrary.makePayment(123, 1234)};
    };

}
```

Caveats
---
Some caveats to using JavaScript controllers is that the Groovy-enriched objects from the Grails context don't translate so well. That means that trying to leverage things like Domain class dynamic finders or other MOP methods/properties will not work.

The best workaround (and probably the best way of handling this in general) is to call a service class method that calls into MOP methods, or retrieves data with GORM.

License
---
Apache, or whatever. I mean... feel free to hack it up and change it all you want.

Author
---
Dan Woods, the man. t{ [@danveloper](http://twitter.com/danveloper) } g{ [danielpwoods@gmail.com](mailto:daniel.p.woods@gmail.com) }
