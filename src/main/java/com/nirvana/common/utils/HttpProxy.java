package com.nirvana.common.utils;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.*;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

/**
 * Created by Nirvana on 2017/11/7.
 */
public class HttpProxy {

    private String originEx;

    private String targetEx;

    private String targetHost;

    private CloseableHttpClient client;

    public HttpProxy(String targetHost) {
        this(targetHost, null, null);
    }

    public HttpProxy(String targetHost, String originEx, String targetEx) {
        this.targetHost = targetHost;
        this.client = HttpClientBuilder.create().disableAutomaticRetries().setConnectionTimeToLive(5, TimeUnit.SECONDS).build();
        if (StringUtils.isNotBlank(originEx) && targetEx != null) {
            this.originEx = originEx;
            this.targetEx = targetEx;
        }
    }

    public void proxy(HttpServletRequest request, HttpServletResponse response) throws IOException {
        switch (request.getMethod()) {
            case "GET":
                proxyGET(request, response);
                break;
            case "POST":
                proxyPOST(request, response);
                break;
            case "PUT":
                proxyPUT(request, response);
                break;
            case "DELETE":
                proxyDELETE(request, response);
                break;
        }
        response.getOutputStream().flush();
    }

    private void proxyGET(HttpServletRequest request0, HttpServletResponse response0) throws IOException {
        HttpGet get = new HttpGet(getUri(request0));
        setHeaders(get, request0);
        CloseableHttpResponse response = client.execute(get);
        setHeaders(response0, response);
        response.getEntity().writeTo(response0.getOutputStream());
    }

    private void proxyPOST(HttpServletRequest request0, HttpServletResponse response0) throws IOException {
        HttpPost post = new HttpPost(getUri(request0));
        setHeaders(post, request0);
        post.setEntity(new InputStreamEntity(request0.getInputStream()));
        CloseableHttpResponse response = client.execute(post);
        setHeaders(response0, response);
        response.getEntity().writeTo(response0.getOutputStream());
    }

    private void proxyPUT(HttpServletRequest request0, HttpServletResponse response0) throws IOException {
        HttpPut put = new HttpPut(getUri(request0));
        setHeaders(put, request0);
        put.setEntity(new InputStreamEntity(request0.getInputStream()));
        CloseableHttpResponse response = client.execute(put);
        setHeaders(response0, response);
        response.getEntity().writeTo(response0.getOutputStream());
    }

    private void proxyDELETE(HttpServletRequest request0, HttpServletResponse response0) throws IOException {
        HttpDelete delete = new HttpDelete(getUri(request0));
        setHeaders(delete, request0);
        CloseableHttpResponse response = client.execute(delete);
        setHeaders(response0, response);
        response.getEntity().writeTo(response0.getOutputStream());
    }

    private String getUri(HttpServletRequest request0) {
        StringBuilder sb = new StringBuilder(targetHost);
        String requestURI = request0.getRequestURI();
        if (StringUtils.isNotBlank(originEx)) {
            if (requestURI.startsWith(originEx)) {
                requestURI = requestURI.replaceFirst(originEx, targetEx);
            }
        }
        sb.append(requestURI);
        if (request0.getQueryString() != null) {
            sb.append("?");
            sb.append(request0.getQueryString());
        }
        return sb.toString();
    }

    private void setHeaders(HttpRequest request, HttpServletRequest request0) {
        Enumeration<String> headers = request0.getHeaderNames();
        while (headers.hasMoreElements()) {
            String header = headers.nextElement();
            if (!header.equalsIgnoreCase("Transfer-Encoding") && !header.equalsIgnoreCase("Content-Length")) {
                request.setHeader(header, request0.getHeader(header));
            }
        }
    }

    private void setHeaders(HttpServletResponse response0, CloseableHttpResponse response) {
        for (Header header : response.getAllHeaders()) {
            response0.setHeader(header.getName(), header.getValue());
        }
    }

}
