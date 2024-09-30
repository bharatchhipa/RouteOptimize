package com.celebal.route.service;


import com.celebal.route.entity.ApiReqResp;
import com.celebal.route.repository.ApiReqRespRepository;
import com.celebal.route.response.ResponseWrapper;
import com.celebal.route.utils.CommonUtils;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
@Log4j2
@RequiredArgsConstructor
public class ApiReqRespService {
    private final ApiReqRespRepository apiReqRespRepository;

    /**
     * This function stores request and response of API being hitted with other fields like ip, http method, url, time etc
     *
     * @param request
     * @param response
     * @param reqCompleteTime
     */
    public void insertApiReqResponse(HttpServletRequest request, HttpServletResponse response, Long reqCompleteTime,String uuid) {
        try {
            String requestBody = (String) request.getAttribute("body");
            Object object = request.getAttribute("response");
            ApiReqResp apiReqResp = new ApiReqResp();
            apiReqResp.setApiUrl(request.getRequestURI());
            apiReqResp.setReqCompleteTime(reqCompleteTime);
            apiReqResp.setRequest(requestBody);
            apiReqResp.setIpAddress(CommonUtils.getClientIp(request));
            apiReqResp.setApiMethod(request.getMethod());
            apiReqResp.setResponseEntityCode(response.getStatus());
            apiReqResp.setUuid(uuid);

            if(object instanceof ResponseWrapper){
                ResponseWrapper responseWrapper = (ResponseWrapper) request.getAttribute("response");
                if (responseWrapper != null) {
                    apiReqResp.setResponse(new Gson().toJson(responseWrapper));
                }
                if (responseWrapper != null && responseWrapper.getStatus() != null) {
                    apiReqResp.setResponseStatus(responseWrapper.getStatus());
                }
                if (responseWrapper != null && responseWrapper.getCode() != null) {
                    apiReqResp.setResponseCode(responseWrapper.getCode());
                }
            }
            apiReqRespRepository.save(apiReqResp);
        } catch (Exception ex) {
            log.error("An Error Occurred::", ex);
        }
    }

}
