package com.example.test.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Entity for request data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestEntity {

    /**
     * Id of request
     */
    @NotNull
    private Integer id;

    /**
     * Message text
     */
    @Size(max = 255)
    private String message;
}
