package com.mayee.model;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Flux;

import java.nio.charset.Charset;

public class CryptoFilterContext {

    private String originBody;
    private String decryptBody;
    private String originFormData;
    private String deCryptFormData;
    private HttpHeaders requestHeaders;
    private Charset charset;
    private Flux<DataBuffer> fluxBody;

    public String getOriginBody() {
        return originBody;
    }

    public void setOriginBody(String originBody) {
        this.originBody = originBody;
    }

    public String getDecryptBody() {
        return decryptBody;
    }

    public void setDecryptBody(String decryptBody) {
        this.decryptBody = decryptBody;
    }

    public HttpHeaders getRequestHeaders() {
        return requestHeaders;
    }

    public void setRequestHeaders(HttpHeaders requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public String getOriginFormData() {
        return originFormData;
    }

    public void setOriginFormData(String originFormData) {
        this.originFormData = originFormData;
    }

    public String getDeCryptFormData() {
        return deCryptFormData;
    }

    public void setDeCryptFormData(String deCryptFormData) {
        this.deCryptFormData = deCryptFormData;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public Flux<DataBuffer> getFluxBody() {
        return fluxBody;
    }

    public void setFluxBody(Flux<DataBuffer> fluxBody) {
        this.fluxBody = fluxBody;
    }
}
