# Endpoints

API endpoints are documented using Swagger v2

A big "gotcha" that arose was from the feature organization I've used. Each operation contains a message (in the form of
a Query or Command), a response and a handler. This results in many classes that all have the same name, differentiated
only by their package & containing class. In most cases, this is not an issue, but Springfox was not picking up the
proper model or response values. The cause is tracked in [issue 182](https://github.com/springfox/springfox/issues/182)
which is still Open, but a workaround exists. You must annotate the classes that have duplicate names as such:

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