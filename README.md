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

The above example defines a controller and registers it to be handled in the Grails context as a controller. The key to the mapping is the first parameter to the `register` method, which defines the path within the web application context that is to be handled by this controller. In this example, requests to **/payment/index** will delegate to the `index` function of the `PaymentController` class.

Controllers should return a JavaScript object representing the model. This return object will be coerced to a Java Map before being delegated to the view.

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
The JavaScript controller context exposes access to the beans from the Grails context. This means that the JavaScript controller has access to the extent of the Grails application context. Accessing services or other controllers is as simple as directly accessing the "wired" bean. Beans are wired into the JavaScript execution context by name, so referencing them from the controller should be handled as regular property accesses.

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
