package com.mayee.decorator;

import com.mayee.model.CryptoFilterContext;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import reactor.core.publisher.Flux;

import java.util.Objects;

public class RequestDecorator extends ServerHttpRequestDecorator {

    private CryptoFilterContext context;

    public RequestDecorator(ServerHttpRequest delegate) {
        super(delegate);
    }

    public RequestDecorator(ServerHttpRequest delegate, CryptoFilterContext context) {
        super(delegate);
        this.context = context;
    }

    /**
     * change content-length
     *
     * @return
     */
    @Override
    public HttpHeaders getHeaders() {
        HttpHeaders headers = context.getRequestHeaders();
        long contentLength = headers.getContentLength();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.putAll(headers);
        if (contentLength > 0) {
            httpHeaders.setContentLength(contentLength);
        } else {
            httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
        }
        return httpHeaders;
    }

    /**
     * read bytes to Flux<Databuffer>
     *
     * @return
     */
    @Override
    public Flux<DataBuffer> getBody() {
        Flux<DataBuffer> fluxBody = context.getFluxBody();
        if (Objects.nonNull(fluxBody)) {
            return fluxBody;
        }
        return super.getBody();
    }
}
