package com.celebal.route.factory;

import com.celebal.route.response.ResponseWrapper;
import com.celebal.route.service.Stop;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface RouteFactory {
    public ResponseEntity<ResponseWrapper> optimizeRoute(List<Stop> stops);
}

