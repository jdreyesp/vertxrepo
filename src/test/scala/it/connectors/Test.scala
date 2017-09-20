package it.connectors

import java.net.URI
import java.util

import com.twitter.finagle.http.{Method, Response}
import httpclient.{RichHTTPClient, RichHTTPClientImpl}
import io.vertx.core.{AsyncResult, Future, Handler}
import it.connectors.server.VertxServer
import org.scalatest.{BeforeAndAfterAll, FlatSpec}

class Test extends FlatSpec with BeforeAndAfterAll {

  val vertxStartedFuture : Future[Void] = Future.future()

  override protected def beforeAll(): Unit = {
    VertxServer.start(vertxStartedFuture)
  }

  override protected def afterAll(): Unit = {
    VertxServer.stop()
  }

  "this is a test" should "be ok" in {
    val uriPath = "http://127.0.0.1:8080/testing_interno/v1/test4"
    val uri : URI = new URI(uriPath)

    //setup vertx


    vertxStartedFuture.setHandler(
      new Handler[AsyncResult[Void]] {
        override def handle(e: AsyncResult[Void]): Unit = {

          //call with client
          val client : RichHTTPClient = new RichHTTPClientImpl(uri.getHost + ":" + uri.getPort)
          val response : Response = client.synchRequest(uri.getPath,
            Method.Post,
            "{\"pepe\" : true}".getBytes,
            new util.HashMap[String, String]())

          assert(response != null)

          println(s"Status code: ${response.statusCode}")
          println(s"Response body: ${response.contentString}")

        }
      }
    )

  }
}
