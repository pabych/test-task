package com.example.test.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;


@Slf4j
@Component
public class WebService {

    private final WebClient webClient;
    private final String soapServiceUrl;

    public WebService(WebClient webClient, @Value("${soap.service.url}") String soapServiceUrl) {
        this.webClient = webClient;
        this.soapServiceUrl = soapServiceUrl;
    }

    public Mono<Resultcode> postMessage(Message message) {
        return webClient.post()
                .uri(soapServiceUrl)
                .contentType(MediaType.TEXT_XML)
                .body(Mono.just(message), Message.class)
                .retrieve()
                .onStatus(
                        HttpStatus::isError,
                        clientResponse ->
                                clientResponse
                                        .bodyToMono(String.class)
                                        .flatMap(
                                                errorResponseBody ->
                                                        Mono.error(
                                                                new ResponseStatusException(
                                                                        clientResponse.statusCode(),
                                                                        errorResponseBody))))

                .bodyToMono(Resultcode.class)
                .doOnError(ex -> log.error("postMessage failed", ex));
    }
}
