package com.oreilly.restclient.config;

import com.oreilly.restclient.services.AstroInterface;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class AppConfig {

    @Bean
    public WebClient webClient() {
        return WebClient.create("http://api.open-notify.org");
    }

    @Bean
    public AstroInterface astroInterface(WebClient client) {
        HttpServiceProxyFactory factory =
                HttpServiceProxyFactory.builder(
                        WebClientAdapter.forClient(client)).build();
        return factory.createClient(AstroInterface.class);
    }
}
