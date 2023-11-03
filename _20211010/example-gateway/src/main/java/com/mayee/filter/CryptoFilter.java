package com.mayee.filter;

import com.alibaba.fastjson.JSONObject;
import com.mayee.decorator.RequestDecorator;
import com.mayee.decorator.ResponseDecorator;
import com.mayee.model.CryptoFilterContext;
import io.netty.buffer.ByteBufAllocator;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
public class CryptoFilter implements GlobalFilter, Ordered {
    private final static Logger log = Logger.getLogger(AuthFilter.class.getName());

    /*
     * default HttpMessageReader
     */
    private static final List<HttpMessageReader<?>> MESSAGE_READERS = HandlerStrategies.withDefaults().messageReaders();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        String crypto = headers.getFirst("crypto");
        // 如果没开启加密
        if (!"true".equals(crypto)) {
            return chain.filter(exchange).then(Mono.fromRunnable(() -> log.info("crypto not enable!")));
        }
        CryptoFilterContext context = new CryptoFilterContext();
        MediaType contentType = headers.getContentType();
        if (MediaType.APPLICATION_JSON.includes(contentType)) {
            return readBodyData(exchange, chain, context);
        }
        if (MediaType.APPLICATION_FORM_URLENCODED.includes(contentType)) {
            return readFormData(exchange, chain, context);
        }
        return chain.filter(exchange);
    }

    private Mono<Void> readBodyData(ServerWebExchange exchange, GatewayFilterChain chain, CryptoFilterContext context) {
        /*
         * join the body
         */
        final ServerHttpRequest request = exchange.getRequest();
        Charset charset = Optional.ofNullable(request.getHeaders().getContentType())
                .map(MediaType::getCharset)
                .orElse(StandardCharsets.UTF_8);
        context.setCharset(charset);
        return DataBufferUtils.join(request.getBody())
                //这里是为了处理请求 body 为 null 时不往下走，当然一般有请求体不会为 null
                .defaultIfEmpty(exchange.getResponse().bufferFactory().wrap("{}".getBytes(context.getCharset())))
                .flatMap(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    String originBody = new String(bytes, context.getCharset());
                    context.setOriginBody(originBody);
                    log.info("原始请求 body(密文): " + originBody);

                    // do 解密
                    String decryptBody = new JSONObject().fluentPut("uname", "from crypto filter").toJSONString();
                    context.setDecryptBody(decryptBody);

                    // 这里是解密后的请求体
                    Flux<DataBuffer> fluxBody = Flux.defer(() -> {
                        DataBuffer buffer = new DefaultDataBufferFactory().wrap(decryptBody.getBytes(context.getCharset()));
                        DataBufferUtils.retain(buffer);
                        return Mono.just(buffer);
                    });
                    context.setFluxBody(fluxBody);

                    HttpHeaders headers = new HttpHeaders();
                    headers.putAll(exchange.getRequest().getHeaders());
                    headers.remove(HttpHeaders.CONTENT_LENGTH);
                    context.setRequestHeaders(headers);

                    RequestDecorator requestDecorator = new RequestDecorator(exchange.getRequest(), context);
                    ResponseDecorator responseDecorator = new ResponseDecorator(exchange.getResponse(), context);
                    /*
                     * mutate exchange with new ServerHttpRequest
                     */
                    ServerWebExchange mutatedExchange = exchange.mutate().request(requestDecorator).response(responseDecorator).build();
                    /*
                     * read body string with default messageReaders
                     */
                    return ServerRequest.create(mutatedExchange, MESSAGE_READERS)
                            .bodyToMono(String.class)
                            .then(chain.filter(mutatedExchange));
                });
    }

    private Mono<Void> readFormData(ServerWebExchange exchange, GatewayFilterChain chain, CryptoFilterContext context) {
        final ServerHttpRequest request = exchange.getRequest();
        Charset charset = Optional.ofNullable(request.getHeaders().getContentType())
                .map(MediaType::getCharset)
                .orElse(StandardCharsets.UTF_8);
        context.setCharset(charset);
        // MultiValueMap<String, String> multiValueMap
        return exchange.getFormData().doOnNext(multiValueMap -> {
            /*
             * repackage form data
             */
            String formData = packageFormData(multiValueMap, context.getCharset().name());
            log.info("原始请求 form-data(密文): " + formData);
            context.setOriginFormData(formData);

            // do 解密
            String decryptBody = "{\"uname\":\"from crypto filter\"}";
            context.setDecryptBody(decryptBody);

            // 这里是解密后的请求体
            Flux<DataBuffer> fluxBody = DataBufferUtils.read(
                    new ByteArrayResource(decryptBody.getBytes(context.getCharset())),
                    new NettyDataBufferFactory(ByteBufAllocator.DEFAULT),
                    decryptBody.getBytes(context.getCharset()).length);
            context.setFluxBody(fluxBody);

        }).then(Mono.defer(() -> {
            HttpHeaders headers = new HttpHeaders();
            headers.putAll(exchange.getRequest().getHeaders());
            headers.remove(HttpHeaders.CONTENT_LENGTH);
            context.setRequestHeaders(headers);

            RequestDecorator requestDecorator = new RequestDecorator(request, context);
            ResponseDecorator responseDecorator = new ResponseDecorator(exchange.getResponse(), context);
            ServerWebExchange mutateExchange = exchange.mutate().request(requestDecorator).response(responseDecorator).build();
            return chain.filter(mutateExchange);
        }));
    }

    private String packageFormData(MultiValueMap<String, String> formData, String charset) {
        return formData.entrySet().stream().map(entry -> {
            String key = entry.getKey();
            String value = entry.getValue().stream().peek(v -> {
                try {
                    URLEncoder.encode(v, charset);
                } catch (UnsupportedEncodingException ignore) {
                }
            }).collect(Collectors.joining("&"));
            return String.format("%s=%s", key, value);
        }).collect(Collectors.joining("&"));
    }

    @Override
    public int getOrder() {
        // 这里 order 必须高于 WRITE_RESPONSE_FILTER_ORDER ，修改请求/响应才能生效
        return NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER - 1;
    }
}
