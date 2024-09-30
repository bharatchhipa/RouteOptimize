package com.celebal.route.interceptor;



import com.celebal.route.helper.AuthInterceptorHelper;
import com.celebal.route.service.ApiReqRespService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Component
@Log4j2
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {


    private final ApiReqRespService apiReqRespService;
    private final AuthInterceptorHelper authInterceptorHelper;

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object object, Exception exception) {

        long startTime = (Long) request.getAttribute("startTime");
        long timeTaken = System.currentTimeMillis() - startTime;
        log.info("Ends :: Request URI::" + request.getRequestURI() + ":: total Time Taken in ms=" + timeTaken);
        if (request.getMethod() != null &&
                !(request.getRequestURI().equalsIgnoreCase("/initializeToken"))) {
            String uuid = MDC.get("uuid");
            apiReqRespService.insertApiReqResponse(request, response, timeTaken,uuid);
        }
        MDC.remove("ip");
        MDC.remove("uniqueId");
        MDC.remove("eventType");
        MDC.remove("referenceNumber");
        MDC.remove("uuid");
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object object, ModelAndView model) {
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) {
        String uuid = UUID.randomUUID().toString();
        MDC.put("uuid",uuid);
        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);
        request.setAttribute("uuid",uuid);
        String uri = request.getRequestURI();
        String[] path = uri.split("/");
        if( path != null && path.length >2){
            String referenceNumber=path[2];
            MDC.put("referenceNumber",referenceNumber);
        }
        log.info("Starts :: Request URI::" + request.getRequestURI());
        authInterceptorHelper.addRequestParameterIfAny(request);
        return true;
    }
}
