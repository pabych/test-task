# Test-task
## General Info
### Task requirements:
* Create an application, with 1 REST endpoint, where you asynchronously process requests using Spring WebFlux.
* Upon receiving request place it's id in processing context, to be able to retrieve it later.
* During processing of request you should asynchronously call external web-service according to provided WSDL and pass field 'message' of incoming attribute as a parameter to the call.
* After successful transmission (200 OK), save request entity into H2 database using Spring Data R2DBC.
* Return message id in response.
* Share resulting project with us via github.
* Project should be built with Gradle.

## Technologies
Project is created with:
* Java version: 11
* Spring boot: 2.7.4
## Launch
To run application please use command:
```
$ ./gradlew bootRun
```