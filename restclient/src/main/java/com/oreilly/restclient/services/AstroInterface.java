package com.oreilly.restclient.services;

import com.oreilly.restclient.json.AstroResponse;
import org.springframework.web.service.annotation.GetExchange;
import reactor.core.publisher.Mono;

public interface AstroInterface {
    @GetExchange("/astros.json")
    Mono<AstroResponse> getResponse();
}
