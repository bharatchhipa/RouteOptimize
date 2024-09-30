package com.celebal.route.controller;


import com.celebal.route.request.RouteGenerationRequest;
import com.celebal.route.response.ResponseWrapper;
import com.celebal.route.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class RouteOptimizationController {


    private final RouteService routeService;

    @PostMapping("/route")
    public ResponseEntity<ResponseWrapper> findOptimizedRouteAndGenerateChart(@RequestBody @Valid RouteGenerationRequest routeGenerationRequest){
        return routeService.findOptimizedRouteAndGenerateChart(routeGenerationRequest);

    }

}
