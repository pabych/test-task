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

@Slf4j
@RestController
@RequestMapping("/v1/")
public class Controller {

    private final ProcessContext processContext;

    private final RequestEntityRepository requestEntityRepository;

    private final WebService webService;

    public Controller(ProcessContext processContext, RequestEntityRepository requestEntityRepository, WebService webService) {
        this.processContext = processContext;
        this.requestEntityRepository = requestEntityRepository;
        this.webService = webService;
    }

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
