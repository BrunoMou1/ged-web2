package com.ged.apigateway.routes;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;

@Configuration
public class Routes {

    @Value("${auth.service.url}")
    private String authServiceUrl;

    @Value("${company.service.url}")
    private String companyServiceUrl;

    @Value("${document.service.url}")
    private String documentServiceUrl;

    @Value("${metadata.service.url}")
    private String metadataServiceUrl;

    @Bean
    public RouterFunction<ServerResponse> customRouteLocator() {
        return GatewayRouterFunctions.route()
                .route(RequestPredicates.path("/auth/**"), HandlerFunctions.http(authServiceUrl))
                .route(RequestPredicates.path("/v1/api/company/**"), HandlerFunctions.http(companyServiceUrl))
                .route(RequestPredicates.path("/v1/api/companymembership/**"), HandlerFunctions.http(companyServiceUrl))
                .route(RequestPredicates.path("/v1/api/documents/**"), HandlerFunctions.http(documentServiceUrl))
                .route(RequestPredicates.path("/v1/api/metadata/**"), HandlerFunctions.http(metadataServiceUrl))
                .build();

    }
}