package connectors.server

import io.vertx.core.http.HttpMethod.{GET, POST}
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.{Future, Handler, Vertx}
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.{Router, RoutingContext}

class VertxServer {

  var vertx : Vertx = null

  def start(startFuture: Future[Void]): Unit = {
    vertx.createHttpServer()
      .requestHandler(getStartupServiceHandler(loadRouters))
        .listen(8080, "localhost")
    startFuture.complete()
  }

  def stop(): Unit = {
    vertx.close()
  }

  private def getStartupServiceHandler(router: Router): Handler[HttpServerRequest] = {
    new Handler[HttpServerRequest] {
      override def handle(event: HttpServerRequest): Unit = {
        event.response().end("hello,world")
        //router.accept(event)
      }
    }
  }

  def loadRouters: Router = {
    val router: Router = Router.router(vertx)
    router.route.handler(BodyHandler.create())
    router.route(GET, "/").handler(getEsbMockHandler)
    router.route(POST, "/testing_interno/v1/test4").handler(getTestPayloadMockHandler)
    router
  }

  private def getEsbMockHandler: Handler[RoutingContext] = new Handler[RoutingContext] {
    override def handle(context: RoutingContext): Unit = {

      val response : String = "Nice response!"

      context.request().response()
        .putHeader("Content-Type", "application/json")
        .putHeader("Content-Length", s"${response.length}")
        .setStatusCode(200)
        .write(response)
        .end()
    }
  }
  private def getTestPayloadMockHandler: Handler[RoutingContext] = new Handler[RoutingContext] {
    override def handle(context: RoutingContext): Unit = {
      context.request().response()
        .putHeader("Content-Type", "text/plain")
          .exceptionHandler(new Handler[Throwable] {
            override def handle(e: Throwable): Unit = println(e)
          })
        .setStatusCode(200)
          .end()
    }
  }

}

object VertxServer extends VertxServer {

  vertx = Vertx.vertx()

}
