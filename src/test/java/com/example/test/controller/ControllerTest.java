package com.example.test.controller;

import com.example.test.entity.RequestEntity;
import com.example.test.repository.RequestEntityRepository;
import com.example.test.service.Message;
import com.example.test.service.Resultcode;
import com.example.test.service.WebService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

/**
 * Integration test for "/v1/process" endpoint
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ControllerTest {

    private static final String PROCESS_ENDPOINT = "/v1/process";

    @Autowired
    private WebTestClient webClient;
    @Autowired
    private RequestEntityRepository requestEntityRepository;

    @MockBean
    private WebService webService;

    /**
     * Removing all records from DB before each test
     */
    @BeforeEach
    void setUp() {
        requestEntityRepository.deleteAll().subscribe();
    }

    /**
     * Test process method. Web-service is mocked
     * Check if the data was persisted to DB
     */
    @Test
    void process() {
        int id = 1;
        RequestEntity requestEntity = RequestEntity.builder()
                .id(id)
                .message("test")
                .build();

        Resultcode resultcode = new Resultcode();
        resultcode.setValue(200);
        when(webService.postMessage(isA(Message.class))).thenReturn(Mono.just(resultcode));

        webClient
                .post().uri(PROCESS_ENDPOINT)
                .bodyValue(requestEntity)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Integer.class).isEqualTo(id);

        requestEntityRepository.findAll()
                .as(StepVerifier::create)
                .expectNext(requestEntity)
                .verifyComplete();
    }

    /**
     * Test process method if web-service returns 5xx error. Web-service is mocked
     * Check if the data was not persisted to DB
     */
    @Test
    void process_5xx() {
        int id = 1;
        RequestEntity requestEntity = RequestEntity.builder()
                .id(id)
                .message("test")
                .build();

        Resultcode resultcode = new Resultcode();
        resultcode.setValue(200);
        when(webService.postMessage(isA(Message.class))).thenThrow(WebClientResponseException.InternalServerError.class);

        webClient
                .post().uri(PROCESS_ENDPOINT)
                .bodyValue(requestEntity)
                .exchange()
                .expectStatus()
                .is5xxServerError();

        requestEntityRepository.findAll()
                .as(StepVerifier::create)
                .expectNextCount(0)
                .verifyComplete();
    }
}
