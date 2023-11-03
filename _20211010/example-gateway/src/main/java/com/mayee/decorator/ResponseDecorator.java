package com.mayee.decorator;

import com.alibaba.fastjson.JSON;
import com.mayee.model.CryptoFilterContext;
import com.mayee.model.R;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ResponseDecorator extends ServerHttpResponseDecorator {
    private CryptoFilterContext context;

    public ResponseDecorator(ServerHttpResponse delegate) {
        super(delegate);
    }

    public ResponseDecorator(ServerHttpResponse delegate, CryptoFilterContext context) {
        super(delegate);
        this.context = context;
    }

    @Override
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        Flux<? extends DataBuffer> fluxBody = Flux.from(body);
        ServerHttpResponse response = getDelegate();
        DataBufferFactory bufferFactory = response.bufferFactory();

        // 解决响应分段传输问题
        Flux<DataBuffer> flux = fluxBody.buffer().map(dataBuffers -> {
            DataBuffer dataBuffer = bufferFactory.join(dataBuffers);
            byte[] content = new byte[dataBuffer.readableByteCount()];
            dataBuffer.read(content);
            // 释放掉内存
            DataBufferUtils.release(dataBuffer);

            //判断响应是否有压缩。下游服务可能会开启响应数据压缩，若开启了压缩，必须先解压才能得到原文
            boolean gzip = Objects.equals("gzip", response.getHeaders().getFirst("Content-Encoding"));
            if (gzip) {
                content = unCompress(content);
            }
            //原始响应
            String originResponse = new String(content, context.getCharset());
            // do 加密
            R data = JSON.parseObject(originResponse, R.class);
            data.setMessage("response from crypto filter");
            String cipherText = JSON.toJSONString(data);
            byte[] bytes = cipherText.getBytes(context.getCharset());
            if (gzip) {
                bytes = compress(bytes);
            }
            return bufferFactory.wrap(bytes);
        });
        return super.writeWith(flux);
    }

    private byte[] unCompress(byte[] bytes) {
        if (ObjectUtils.isEmpty(bytes)) {
            return new byte[]{};
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        try (GZIPInputStream gzip = new GZIPInputStream(in)) {
            byte[] buffer = new byte[1024];
            int n;
            while ((n = gzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[]{};
    }

    private byte[] compress(byte[] bytes) {
        if (ObjectUtils.isEmpty(bytes)) {
            return new byte[]{};
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(out)) {
            gzip.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }
}
