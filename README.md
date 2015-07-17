[![Build Status](https://travis-ci.org/excelsior-oss/restler.svg?branch=master)](https://travis-ci.org/excelsior-oss/restler)
[![Maven Central](https://img.shields.io/maven-central/v/org.restler/restler-core.svg)](https://maven-badges.herokuapp.com/maven-central/org.restler/restler-core)

Restler
=======
 
Overview
--------

*Restler* is a library that generates a client of a web service by its annotated Spring controller interface at runtime. 

### Features
 * Custom authentication, authorization and errors mapping strategies.
 * Form-based authorization.
 * Cookie and HTTP Basic authentication.
 * Automatic reauthorization.
 * Exception class name based error mapping strategy.
 * Support of async controllers through methods returning DefferedResult or Callabe objects

### Simple usage example

Assuming, you have following interface on the server
```java
/** 
  * An annotated Spring controller interface
  */
@Controller
@RequestMapping("greeter")
public interface Greeter {

	@RequestMapping("greetings/{language}")	
	String getGreeting(@PathVariable String language, @RequestParam(defaultValue = "Anonimous") String name); 

}
```

Then you can invoke getGreating method on implementation class using following code snippet
```java
Service service = new ServiceBuilder("https://www.excelsior-usa.com/api").build();
Greeter greeter = service.produceClient(Greeter.class);
String greeting = greeter.getGreeting("en","Boddy"); // the result of https://www.excelsior-usa.com/api/greeter/greetings/en?name=Boddy call
```
