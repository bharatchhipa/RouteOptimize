package com.celebal.route.interceptor;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Objects;

@Component
@ControllerAdvice
@RequiredArgsConstructor
@Log4j2
public class RequestBodyInterceptor implements RequestBodyAdvice {

    private final HttpServletRequest request;
    @Value("${servlet.path.index}")
    private Integer servletIndexPath = 2;

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        String[] servletPath = request.getRequestURI().split("/");
        if (request.getMethod() != null) {
            StringBuilder reqMessage = new StringBuilder();
            reqMessage.append("method = [").append(request.getMethod()).append("]");
            reqMessage.append(" path = [").append(request.getRequestURI()).append("] ");

            StringBuilder reqbody = new StringBuilder();
            if (!Objects.isNull(body)) {
                reqbody.append("body = [").append(new Gson().toJson(body)).append("] ");
                reqMessage.append(reqbody);
            }
            log.info("REQUEST: {}", reqMessage);
            String requestAttribute = (String) request.getAttribute("body");
            if (requestAttribute != null && !requestAttribute.isEmpty()) {
                reqbody.append(requestAttribute);
            }
            request.setAttribute("body", reqbody.toString());
        }
        return body;
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        return inputMessage;
    }

}