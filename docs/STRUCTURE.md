# Code base structure

## Overview
There are three main, top level, concerns found inside this code base. These are **domain**, **infrastructure** and 
**features**.

* Domain
  
  Contained in the domain are the models, which describe the Things the system keeps track of, their relationships
  and their behaviour.
  
* Infrastructure

  Infrastructure is that boilerplate plumbing that makes the application work. Security configuration, Swagger setup, 
  repositories, Exception handling filters, Web Socket wiring, the mediator and more can all be found here.
  
  Of special note is the aggregation of all repositories in to an object called CmdbContext to allow a single point of
  access to all of the application data.
    
* Features
  
  All of the actual functions of the application are found in features. They are meant to be vertical slices of 
  functionality and do away with the need for a service layer. Rest controllers take advantage of a mediator to delegate
  all logic to a series of purpose-built handlers. See Jimmy Bogard's blog post [CQRS with MediatR and AutoMapper](https://lostechies.com/jimmybogard/2015/05/05/cqrs-with-mediatr-and-automapper/)
  for more background.

## Endpoints

API endpoints are documented using Swagger v2

A big "gotcha" that arose was from the feature organization I've used. Each operation contains a message (in the form of
a Query or Command), a response and a handler. This results in many classes that all have the same name, differentiated
only by their package & containing class. In most cases, this is not an issue, but Springfox was not picking up the
proper model or response values. The cause is tracked in [issue 182](https://github.com/springfox/springfox/issues/182)
which is still open, but a workaround exists. You must annotate the classes that have duplicate names as such:

```java
public class Get1
{
    @ApiModel("Get1Query")
    public static class Query {}
}

public class Get2
{
    @ApiModel("Get2Query")
    public static class Query {}
}
```

