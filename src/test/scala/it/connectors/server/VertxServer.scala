package it.connectors.server

import java.util.concurrent.TimeUnit

import io.vertx.core.http.HttpMethod.POST
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.{AbstractVerticle, Future, Handler, Vertx}
import io.vertx.ext.web.{Router, RoutingContext}
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.rx.java.RxHelper

class VertxServer extends AbstractVerticle {

  var deploymentId : Option[String] = None

  override def start(startFuture: Future[Void]): Unit = {
    vertx = Vertx.vertx
    val observableFuture = RxHelper.observableFuture[String]
    vertx.deployVerticle(classOf[VertxServer].getName, observableFuture.toHandler)
    deploymentId = Some(observableFuture.timeout(10, TimeUnit.SECONDS)
      .toBlocking
      .first())
    println("Vertx server initialized......")
  }

  override def stop(): Unit = {
    vertx.undeploy(deploymentId.get)
    deploymentId = None
    println("Vertx server stopped......")
  }

  private def getStartupServiceHandler(router: Router): Handler[HttpServerRequest] = {
    new Handler[HttpServerRequest] {
      override def handle(event: HttpServerRequest): Unit = {
        router.accept(event)
      }
    }
  }

  def loadRouters: Router = {
    val router: Router = Router.router(vertx)
    router.route.handler(BodyHandler.create())
    router.route(POST, "/testing_interno/v1/test4").handler(getTestPayloadMockHandler)
    router
  }

  private def getTestPayloadMockHandler: Handler[RoutingContext] = new Handler[RoutingContext] {
    override def handle(context: RoutingContext): Unit = {
      context.request().response()
        .putHeader("Content-Type", "application/json")
        .setStatusCode(200)
          .end()
    }
  }

}

object VertxServer extends VertxServer {

}
