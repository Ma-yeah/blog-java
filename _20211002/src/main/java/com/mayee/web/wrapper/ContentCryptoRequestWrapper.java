/*
 * Copyright 2002-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mayee.web.wrapper;

import com.alibaba.fastjson.JSON;
import com.mayee.model.UserModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.web.util.WebUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * {@link javax.servlet.http.HttpServletRequest} wrapper that caches all content read from
 * the {@linkplain #getInputStream() input stream} and {@linkplain #getReader() reader},
 * and allows this content to be retrieved via a {@link #getContentAsByteArray() byte array}.
 *
 * <p>This class acts as an interceptor that only caches content as it is being
 * read but otherwise does not cause content to be read. That means if the request
 * content is not consumed, then the content is not cached, and cannot be
 * retrieved via {@link #getContentAsByteArray()}.
 *
 * <p>Used e.g. by {@link org.springframework.web.filter.AbstractRequestLoggingFilter}.
 * Note: As of Spring Framework 5.0, this wrapper is built on the Servlet 3.1 API.
 *
 * @author Juergen Hoeller
 * @author Brian Clozel
 * @see ContentCryptoResponseWrapper
 * @since 4.1.3
 */
@Slf4j
public class ContentCryptoRequestWrapper extends HttpServletRequestWrapper {

    private static final String FORM_CONTENT_TYPE = "application/x-www-form-urlencoded";


    private final ByteArrayOutputStream cachedContent;

    @Nullable
    private final Integer contentCacheLimit;

    @Nullable
    private ServletInputStream inputStream;

    @Nullable
    private BufferedReader reader;


    /**
     * Create a new ContentCachingRequestWrapper for the given servlet request.
     *
     * @param request the original servlet request
     */
    public ContentCryptoRequestWrapper(HttpServletRequest request) {
        super(request);
        int contentLength = request.getContentLength();
        this.cachedContent = new ByteArrayOutputStream(contentLength >= 0 ? contentLength : 1024);
        this.contentCacheLimit = null;
    }

    /**
     * Create a new ContentCachingRequestWrapper for the given servlet request.
     *
     * @param request           the original servlet request
     * @param contentCacheLimit the maximum number of bytes to cache per request
     * @see #handleContentOverflow(int)
     * @since 4.3.6
     */
    public ContentCryptoRequestWrapper(HttpServletRequest request, int contentCacheLimit) {
        super(request);
        this.cachedContent = new ByteArrayOutputStream(contentCacheLimit);
        this.contentCacheLimit = contentCacheLimit;
    }

    /**
     * @param
     * @Description:
     * @return: java.lang.String
     * @Author: Bobby.Ma
     * @Date: 2021/10/7 2:27
     */
    private String decryptBody(ServletRequest request) {
        // 读取body参数, 解密操作 ...
        String body = readBody(request);
        UserModel userModel = new UserModel()
                .setId(1)
                .setUuid("YX8848")
                .setUname("解密后的用户");
        return JSON.toJSONString(userModel);
    }

    /**
     * @param request
     * @Description:
     * @return: java.lang.String
     * @Author: Bobby.Ma
     * @Date: 2021/10/7 4:08
     */
    public String readBody(ServletRequest request) {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return builder.toString();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (this.inputStream == null) {
            // 解密后的请求体参数
            String decryptBody = decryptBody(getRequest());
            this.inputStream = new ContentCachingInputStream(new ByteArrayInputStream(decryptBody.getBytes(StandardCharsets.UTF_8)));
        }
        return this.inputStream;
    }

    @Override
    public String getCharacterEncoding() {
        String enc = super.getCharacterEncoding();
        return (enc != null ? enc : WebUtils.DEFAULT_CHARACTER_ENCODING);
    }

    @Override
    public BufferedReader getReader() throws IOException {
        if (this.reader == null) {
            this.reader = new BufferedReader(new InputStreamReader(getInputStream(), getCharacterEncoding()));
        }
        return this.reader;
    }

    // url 参数解密，重写这个方法
    @Override
    public String getParameter(String name) {
        if (this.cachedContent.size() == 0 && isFormPost()) {
            writeRequestParametersToCachedContent();
        }
        return super.getParameter(name);
    }

    // url 参数解密，重写这个方法
    @Override
    public Map<String, String[]> getParameterMap() {
        if (this.cachedContent.size() == 0 && isFormPost()) {
            writeRequestParametersToCachedContent();
        }
        return super.getParameterMap();
    }

    // url 参数解密，重写这个方法
    @Override
    public Enumeration<String> getParameterNames() {
        if (this.cachedContent.size() == 0 && isFormPost()) {
            writeRequestParametersToCachedContent();
        }
        return super.getParameterNames();
    }

    // url 参数解密，重写这个方法
    @Override
    public String[] getParameterValues(String name) {
        if (this.cachedContent.size() == 0 && isFormPost()) {
            writeRequestParametersToCachedContent();
        }
        return super.getParameterValues(name);
    }


    private boolean isFormPost() {
        String contentType = getContentType();
        return (contentType != null && contentType.contains(FORM_CONTENT_TYPE) &&
                HttpMethod.POST.matches(getMethod()));
    }

    // url 参数解密，重写这个方法
    private void writeRequestParametersToCachedContent() {
        try {
            if (this.cachedContent.size() == 0) {
                String requestEncoding = getCharacterEncoding();
                Map<String, String[]> form = super.getParameterMap();
                for (Iterator<String> nameIterator = form.keySet().iterator(); nameIterator.hasNext(); ) {
                    String name = nameIterator.next();
                    List<String> values = Arrays.asList(form.get(name));
                    for (Iterator<String> valueIterator = values.iterator(); valueIterator.hasNext(); ) {
                        String value = valueIterator.next();
                        this.cachedContent.write(URLEncoder.encode(name, requestEncoding).getBytes());
                        if (value != null) {
                            this.cachedContent.write('=');
                            this.cachedContent.write(URLEncoder.encode(value, requestEncoding).getBytes());
                            if (valueIterator.hasNext()) {
                                this.cachedContent.write('&');
                            }
                        }
                    }
                    if (nameIterator.hasNext()) {
                        this.cachedContent.write('&');
                    }
                }
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to write request parameters to cached content", ex);
        }
    }

    /**
     * Return the cached request content as a byte array.
     * <p>The returned array will never be larger than the content cache limit.
     * <p><strong>Note:</strong> The byte array returned from this method
     * reflects the amount of content that has has been read at the time when it
     * is called. If the application does not read the content, this method
     * returns an empty array.
     *
     * @see #ContentCryptoRequestWrapper(HttpServletRequest, int)
     */
    public byte[] getContentAsByteArray() {
        return this.cachedContent.toByteArray();
    }

    /**
     * Template method for handling a content overflow: specifically, a request
     * body being read that exceeds the specified content cache limit.
     * <p>The default implementation is empty. Subclasses may override this to
     * throw a payload-too-large exception or the like.
     *
     * @param contentCacheLimit the maximum number of bytes to cache per request
     *                          which has just been exceeded
     * @see #ContentCryptoRequestWrapper(HttpServletRequest, int)
     * @since 4.3.6
     */
    protected void handleContentOverflow(int contentCacheLimit) {
    }

    private class ContentCachingInputStream extends ServletInputStream {

        private final ByteArrayInputStream bis;

        private boolean overflow = false;

        public ContentCachingInputStream(ByteArrayInputStream bis) {
            this.bis = bis;
        }

        @Override
        public int read() throws IOException {
            int ch = this.bis.read();
            if (ch != -1 && !this.overflow) {
                if (contentCacheLimit != null && cachedContent.size() == contentCacheLimit) {
                    this.overflow = true;
                    handleContentOverflow(contentCacheLimit);
                } else {
                    cachedContent.write(ch);
                }
            }
            return ch;
        }

        @Override
        public int read(byte[] b) throws IOException {
            int count = this.bis.read(b);
            writeToCache(b, 0, count);
            return count;
        }

        private void writeToCache(final byte[] b, final int off, int count) {
            if (!this.overflow && count > 0) {
                if (contentCacheLimit != null &&
                        count + cachedContent.size() > contentCacheLimit) {
                    this.overflow = true;
                    cachedContent.write(b, off, contentCacheLimit - cachedContent.size());
                    handleContentOverflow(contentCacheLimit);
                    return;
                }
                cachedContent.write(b, off, count);
            }
        }

        @Override
        public int read(final byte[] b, final int off, final int len) throws IOException {
            int count = this.bis.read(b, off, len);
            writeToCache(b, off, count);
            return count;
        }

        @Override
        public boolean isFinished() {
            return true;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {

        }
    }

}
