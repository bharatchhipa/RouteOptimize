package com.celebal.route.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class RouteGenerationRequest {
    @NotEmpty
    private String bucketName;
    @NotEmpty
    private String fileName;
}
