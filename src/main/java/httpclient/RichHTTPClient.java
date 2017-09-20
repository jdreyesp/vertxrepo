package httpclient;

import com.twitter.finagle.http.Method;
import com.twitter.finagle.http.Response;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Rich HTTP Client using TPA Finagle based connectivity module
 * It implements:
 *  - synchronous requests, with: TTL, Read timeouts, Keep-alive [TODO COMPLETAR]
 */
public interface RichHTTPClient {

    /**
     * Synchronous request
     * @param endpoint endpoint to consume
     * @param method HTTP Method to use.
     * @param body Request body
     * @param headers Request headers
     * @return Response
     * @throws ExecutionException
     * @throws InterruptedException
     */
    Response synchRequest(String endpoint, Method method, byte[] body, Map<String, String> headers) throws ExecutionException, InterruptedException;
}
