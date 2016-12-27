package com.welab.service2;

import com.github.kristofa.brave.*;
import com.github.kristofa.brave.http.*;
import com.github.kristofa.brave.servlet.BraveServletFilter;
import com.github.kristofa.brave.servlet.ServletHttpServerRequest;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * 自定义Trance信息
 * @author xiaoqian.wen
 * @create 2016-12-27 15:34
 **/
public class BraveDefineFilter implements Filter {

    private final ServerRequestInterceptor requestInterceptor;
    private final ServerResponseInterceptor responseInterceptor;
    private final SpanNameProvider spanNameProvider;

    private FilterConfig filterConfig;

    public BraveDefineFilter(ServerRequestInterceptor requestInterceptor, ServerResponseInterceptor responseInterceptor, SpanNameProvider spanNameProvider) {
        this.requestInterceptor = requestInterceptor;
        this.responseInterceptor = responseInterceptor;
        this.spanNameProvider = spanNameProvider;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    @Override
    public void doFilter(final ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        String alreadyFilteredAttributeName = getAlreadyFilteredAttributeName();
        boolean hasAlreadyFilteredAttribute = request.getAttribute(alreadyFilteredAttributeName) != null;

        if (hasAlreadyFilteredAttribute) {
            // Proceed without invoking this filter...
            filterChain.doFilter(request, response);
        } else {
            final ServletHttpServerRequest serverRequest = new ServletHttpServerRequest((HttpServletRequest) request);
            final BraveDefineFilter.StatusExposingServletResponse statusExposingServletResponse = new BraveDefineFilter.StatusExposingServletResponse((HttpServletResponse) response);
            requestInterceptor.handle(new ServerRequestAdapter() {
                @Override
                public TraceData getTraceData() {
                    String sampled = serverRequest.getHttpHeaderValue(BraveHttpHeaders.Sampled.getName());
                    if(sampled != null) {
                        if(sampled.equals("0") || sampled.toLowerCase().equals("false")) {
                            return TraceData.builder().sample(Boolean.valueOf(false)).build();
                        }

                        String parentSpanId = serverRequest.getHttpHeaderValue(BraveHttpHeaders.ParentSpanId.getName());
                        String traceId = serverRequest.getHttpHeaderValue(BraveHttpHeaders.TraceId.getName());
                        String spanId = serverRequest.getHttpHeaderValue(BraveHttpHeaders.SpanId.getName());
                        if(traceId != null && spanId != null) {
                            SpanId span = this.getSpanId(traceId, spanId, parentSpanId);
                            return TraceData.builder().sample(Boolean.valueOf(true)).spanId(span).build();
                        }
                    }

                    return TraceData.builder().build();
                }

                @Override
                public String getSpanName() {
                    return spanNameProvider.spanName(serverRequest);
                }

                @Override
                public Collection<KeyValueAnnotation> requestAnnotations() {
                    KeyValueAnnotation uriAnnotation = KeyValueAnnotation.create("http.url", serverRequest.getUri().toString());

                    Collection<KeyValueAnnotation> collection = new ArrayList<KeyValueAnnotation>();
                    KeyValueAnnotation kv = KeyValueAnnotation.create("radioid", "165646485468486364");
                    collection.add(kv);
                    collection.add(uriAnnotation);
                    return collection;
                }

                private SpanId getSpanId(String traceId, String spanId, String parentSpanId) {
                    return SpanId.builder().traceId(IdConversion.convertToLong(traceId)).spanId(IdConversion.convertToLong(spanId)).parentId(parentSpanId == null?null:Long.valueOf(IdConversion.convertToLong(parentSpanId))).build();
                }
            });

            try {
                filterChain.doFilter(request, statusExposingServletResponse);
            } finally {
                responseInterceptor.handle(new HttpServerResponseAdapter(new HttpResponse() {
                    @Override
                    public int getHttpStatusCode() {
                        return statusExposingServletResponse.getStatus();
                    }
                }));
            }
        }
    }

    @Override
    public void destroy() {

    }

    private String getAlreadyFilteredAttributeName() {
        String name = getFilterName();
        if (name == null) {
            name = getClass().getName();
        }
        return name + ".FILTERED";
    }

    private final String getFilterName() {
        return (this.filterConfig != null ? this.filterConfig.getFilterName() : null);
    }


    private static class StatusExposingServletResponse extends HttpServletResponseWrapper {
        // The Servlet spec says: calling setStatus is optional, if no status is set, the default is OK.
        private int httpStatus = HttpServletResponse.SC_OK;

        public StatusExposingServletResponse(HttpServletResponse response) {
            super(response);
        }

        @Override
        public void sendError(int sc) throws IOException {
            httpStatus = sc;
            super.sendError(sc);
        }

        @Override
        public void sendError(int sc, String msg) throws IOException {
            httpStatus = sc;
            super.sendError(sc, msg);
        }

        @Override
        public void setStatus(int sc) {
            httpStatus = sc;
            super.setStatus(sc);
        }

        public int getStatus() {
            return httpStatus;
        }
    }
}
