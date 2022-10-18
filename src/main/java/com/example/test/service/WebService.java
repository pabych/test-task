package com.example.test.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

/**
 * Service to communicate with external web-service
 */
@Slf4j
@Component
public class WebService {

    /**
     * Non-blocking, reactive client to communicate with web-service
     */
    private final WebClient webClient;

    /**
     * SOAP service URL
     */
    private final String soapServiceUrl;

    /**
     * Controller with autowiring WebClient and binding SOAP service URL from ${soap.service.url} property
     * @param webClient WebClient
     * @param soapServiceUrl SOAP service URL
     */
    public WebService(WebClient webClient, @Value("${soap.service.url}") String soapServiceUrl) {
        this.webClient = webClient;
        this.soapServiceUrl = soapServiceUrl;
    }

    /**
     * Asynchronously call external web-service according to provided WSDL
     * @param message Message
     * @return {@code Mono<Resultcode>}
     */
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
