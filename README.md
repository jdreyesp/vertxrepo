# Vertx Server example

This is an example to set up a Vertx Server, using Vertx-server library. The example creates a Vertx Server as simple as doing the following:

```
def start(startFuture: Future[Void]): Unit = {
    vertx.createHttpServer()
      .requestHandler(getStartupServiceHandler(loadRouters))
        .listen(8080, "localhost")
    startFuture.complete()
  }
    
```

To test it, I use the client that Vertx library offers, and consume it this way: 

```
val client : HttpClient = VertxServer.vertx.createHttpClient(new HttpClientOptions().setDefaultHost("localhost")
            .setDefaultPort(8080))
          client.getNow("/", new Handler[HttpClientResponse] {
            override def handle(e: HttpClientResponse): Unit = {
              println(s"Response received!!: ${e.statusCode()}")
              barrier.countDown()
            }
          })
          
```

- This is a maven project, and can be tested with ```mvn clean integration-test```

- It uses scalaTest library for tests.

