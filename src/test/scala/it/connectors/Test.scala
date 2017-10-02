package it.connectors

import java.util.concurrent.{CountDownLatch, TimeUnit}

import io.vertx.core.http.{HttpClient, HttpClientOptions, HttpClientResponse}
import io.vertx.core.{AsyncResult, Future, Handler}
import it.connectors.server.VertxServer
import org.scalatest.{BeforeAndAfterAll, FlatSpec}

class Test extends FlatSpec with BeforeAndAfterAll {

  val vertxStartedFuture : Future[Void] = Future.future()

  override protected def beforeAll(): Unit = {
    println("Starting vertx server...")
    VertxServer.start(vertxStartedFuture)
  }

  override protected def afterAll(): Unit = {
    println("Shutting down vertx server...")
    VertxServer.stop()
  }


  it should "consume with vertx client" in {

    vertxStartedFuture.setHandler(
      new Handler[AsyncResult[Void]] {
        override def handle(e: AsyncResult[Void]): Unit = {

          val response = null
          val barrier : CountDownLatch = new CountDownLatch(1)

          //call with vertx client
          val client : HttpClient = VertxServer.vertx.createHttpClient(new HttpClientOptions().setDefaultHost("localhost")
            .setDefaultPort(8080))
          client.getNow("/", new Handler[HttpClientResponse] {
            override def handle(e: HttpClientResponse): Unit = {
              println(s"Response received!!: ${e.statusCode()}")
              barrier.countDown()
            }
          })

          try{
            assert(barrier.await(300000, TimeUnit.MILLISECONDS), "Timeout reached")
          } catch {
            case e: InterruptedException => fail("Interrupt exception occured")
          }
        }
      }
    )

  }
}
