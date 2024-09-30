package com.celebal.route.helper;


import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Component
@Log4j2
public class AuthInterceptorHelper {

    public void addRequestParameterIfAny(HttpServletRequest request) {
        Map<String, String> parameters = getParameters(request);

        StringBuilder reqMessage = new StringBuilder();
        reqMessage.append("method = [").append(request.getMethod()).append("]");
        reqMessage.append(" path = [").append(request.getRequestURI()).append("] ");
        if (!parameters.isEmpty()) {
            StringBuilder reqBody = new StringBuilder().append("parameters = [").append(parameters).append("]");
            reqMessage.append(reqBody);
            request.setAttribute("body", reqBody.toString());
        }
        log.info("REQUEST: {}", reqMessage);

    }

    private Map<String, String> getParameters(HttpServletRequest request) {
        Map<String, String> parameters = new HashMap<>();
        Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String paramName = params.nextElement();
            String paramValue = request.getParameter(paramName);
            parameters.put(paramName, paramValue);
        }
        return parameters;
    }
}
