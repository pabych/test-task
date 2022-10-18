package com.example.test.controller;

import com.example.test.ProcessContext;
import com.example.test.entity.RequestEntity;
import com.example.test.repository.RequestEntityRepository;
import com.example.test.service.Message;
import com.example.test.service.Resultcode;
import com.example.test.service.WebService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.validation.Valid;

/**
 * REST Controller
 */
@Slf4j
@RestController
@RequestMapping("/v1/")
public class Controller {

    /**
     * Process context for Id of request
     */
    private final ProcessContext processContext;

    /**
     * Interface for generic CRUD operations on a repository for RequestEntity
     */
    private final RequestEntityRepository requestEntityRepository;

    /**
     * Service to communicate with external web-service
     */
    private final WebService webService;

    /**
     * Controller method with autowiring of ProcessContext, RequestEntityRepository and WebService
     * @param processContext ProcessContext
     * @param requestEntityRepository RequestEntityRepository
     * @param webService WebService
     */
    public Controller(ProcessContext processContext, RequestEntityRepository requestEntityRepository, WebService webService) {
        this.processContext = processContext;
        this.requestEntityRepository = requestEntityRepository;
        this.webService = webService;
    }

    /**
     * Endpoint asynchronously process requests using Spring WebFlux
     * <p>
     * Process incoming data with format:
     * <blockquote><pre>
     * {
     *  id: Integer,
     *  message: String
     * }
     *  </pre></blockquote>
     *
     * @param requestEntity an RequestEntity
     * @return {@code Mono<Integer>}
     */
    @PostMapping("/process")
    private Mono<Integer> process(@RequestBody @Valid RequestEntity requestEntity) {
        processContext.setId(requestEntity.getId());

        Message message = new Message();
        message.setValue(requestEntity.getMessage());

        Mono<Resultcode> resultcodeMono = webService.postMessage(message);
        resultcodeMono = resultcodeMono.publishOn(Schedulers.boundedElastic()).doOnSuccess(resultcode -> {
            log.error("postMessage succeeded with result code: {}", resultcode.getValue());
            if (resultcode.getValue() == 200) {
                requestEntityRepository.save(requestEntity)
                        .doOnError(ex -> log.error("Failed during saving to DB", ex))
                        .subscribe();
            }
        });

        return resultcodeMono.then(Mono.just(processContext.getId()));
    }
}
