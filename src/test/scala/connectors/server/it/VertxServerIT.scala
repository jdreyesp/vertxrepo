package connectors.server.it

import java.util.concurrent.{CountDownLatch, TimeUnit}

import connectors.server.VertxServer
import io.vertx.core.http.{HttpClient, HttpClientOptions, HttpClientResponse}
import io.vertx.core.{AsyncResult, Future, Handler}
import org.scalatest.{BeforeAndAfterAll, FlatSpec}

class VertxServerIT extends FlatSpec with BeforeAndAfterAll {

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

          val barrier : CountDownLatch = new CountDownLatch(1)

          //call with vertx client
          val client : HttpClient = VertxServer.vertx.get.createHttpClient(new HttpClientOptions().setDefaultHost("localhost")
            .setDefaultPort(8080))
          client.getNow("/", new Handler[HttpClientResponse] {
            override def handle(e: HttpClientResponse): Unit = {
              println(s"Response received!!: ${e.statusCode()}")
              barrier.countDown()
            }
          })

          assert(barrier.await(300000, TimeUnit.MILLISECONDS), "Timeout reached")

        }
      }
    )

  }
}
