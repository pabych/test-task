package com.example.test;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

/**
 * Component for holding id of RequestEntity in processing context during RequestScope
 */
@Data
@Component
@RequestScope
public class ProcessContext {

    private Integer id;
}
