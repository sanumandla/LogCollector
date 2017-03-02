package com.bigdata.logcollector.aop;

import com.bigdata.logcollector.LogCollectorConstants;
import com.bigdata.logcollector.dto.GBQRow;
import com.bigdata.logcollector.exception.LogCollectorException;
import com.bigdata.logcollector.service.GBQService;
import com.bigdata.logcollector.service.IPublishService;
import com.bigdata.logcollector.service.PublishServiceFactory;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sun.swing.StringUIClientPropertyKey;

import javax.servlet.http.HttpServletRequest;

/**
 * @author sanumandla
 */
@Slf4j
@Aspect
@Component
public class LogStreamAspect extends LogStreamPointcut {

    private HttpServletRequest request;
    private PublishServiceFactory factory;

    @Autowired
    public LogStreamAspect(HttpServletRequest request, PublishServiceFactory factory) {
        this.request = request;
        this.factory = factory;
    }

    @Before("anyRequestMappingAnnotation() && streamAnnotation()")
    public void stream(JoinPoint jp) {
        log.info("Streaming data now ...");

        GBQRow row = new GBQRow();
        row.setRequestUri(request.getRequestURI());
        row.setRequestMethod(request.getMethod());
        row.setProtocol(request.getProtocol());
        row.setContentType(request.getContentType());
        row.setServerName(request.getServerName());
        row.setServerPort(request.getServerPort());
        row.setRemoteServerName(request.getRemoteHost());
        row.setRemoteServerPort(request.getRemotePort());
        row.setTimestamp(System.currentTimeMillis());

        String type = request.getParameter(LogCollectorConstants.TYPE);
        log.info("Streaming data to {}", type == null ?  LogCollectorConstants.TYPE_CONSOLE : type);

        try {
            factory.getPublishService(type).publishEvent(row);
        } catch (LogCollectorException e) {
            log.error("Error publishing event to GBQ: {}", e);
        }
    }

}