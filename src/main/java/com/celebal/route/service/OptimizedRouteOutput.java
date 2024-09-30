package com.celebal.route.service;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OptimizedRouteOutput {
    private List<Integer> stops;
    private double optimizedDistance;
    private String formattedRoute;

}
