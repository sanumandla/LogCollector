package com.bigdata.logcollector.aop;

import org.aspectj.lang.annotation.Pointcut;

/**
 * @author sanumandla
 */
public class LogStreamPointcut {

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public void anyRequestMappingAnnotation() {}

    @Pointcut("@target(com.bigdata.logcollector.annotation.Stream) || " +
            "@annotation(com.bigdata.logcollector.annotation.Stream)")
    public void streamAnnotation() {}

}
