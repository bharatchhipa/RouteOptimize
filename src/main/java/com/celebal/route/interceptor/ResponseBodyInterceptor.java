package com.celebal.route.interceptor;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
@RequiredArgsConstructor
@Log4j2
public class ResponseBodyInterceptor implements ResponseBodyAdvice<Object> {

    @Value("${servlet.path.index}")
    private Integer servletIndexPath = 2;

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType,
                                  MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        displayResp(((ServletServerHttpRequest) request).getServletRequest(), ((ServletServerHttpResponse) response).getServletResponse(), body);
        return body;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }


    public void displayResp(HttpServletRequest request, HttpServletResponse response, Object body) {
        String[] servletPath = request.getRequestURI().split("/");
        if (request.getMethod() != null) {
            StringBuilder respMessage = new StringBuilder();
            respMessage.append(" method = [").append(request.getMethod()).append("]");
            respMessage.append(" responseBody = [").append(new Gson().toJson(body)).append("]");

            log.info("RESPONSE: {}", respMessage);
            request.setAttribute("response", body);
        }
    }

}
