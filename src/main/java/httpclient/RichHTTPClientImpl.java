package httpclient;

import com.ing.apisdk.toolkit.connectivity.api.JavaService;
import com.ing.apisdk.toolkit.connectivity.api.ServiceConverter;
import com.ing.apisdk.toolkit.connectivity.transport.http.japi.RichHttpRequestBuilder;
import com.twitter.finagle.Http;
import com.twitter.finagle.Service;
import com.twitter.finagle.http.Method;
import com.twitter.finagle.http.Request;
import com.twitter.finagle.http.Response;
import com.twitter.io.Buf;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public class RichHTTPClientImpl implements RichHTTPClient {

    private final JavaService<Request, Response> client;

    public RichHTTPClientImpl(final String hostUrl) {
        final Service<Request, Response> finagle_client = Http.client().newService(hostUrl, "myService");
        this.client = ServiceConverter.asJava(finagle_client);
    }

    public Response synchRequest(String endpoint, Method method, byte[] body, Map<String, String> headers) throws ExecutionException, InterruptedException {

        final RichHttpRequestBuilder requestBuilder = new RichHttpRequestBuilder()
                .withMethod(method)
                .withPath(endpoint)
                .withContent(new Buf.ByteArray(body, 0, body.length));

        headers.forEach(
                (name,value) -> requestBuilder.withHeader(name,value)
        );

        return getClient().apply(requestBuilder.build()).get();
    }


    public JavaService<Request, Response> getClient() {
        return client;
    }

}
