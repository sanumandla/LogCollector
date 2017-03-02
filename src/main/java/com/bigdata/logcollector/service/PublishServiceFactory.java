package com.bigdata.logcollector.service;

import com.bigdata.logcollector.exception.LogCollectorException;
import com.google.api.services.bigquery.model.TableSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Properties;

import static com.bigdata.logcollector.LogCollectorConstants.*;

/**
 * @author sanumandla
 */
@Service
public class PublishServiceFactory {

    private GBQService gbqService;
    private ConsoleService consoleService;

    @Autowired
    public PublishServiceFactory(@Qualifier(GBQ_SERVICE) GBQService gbqService, @Qualifier(CONSOLE_SERVICE) ConsoleService consoleService) {
        this.gbqService = gbqService;
        this.consoleService = consoleService;
    }

    public IPublishService getPublishService(String type) throws LogCollectorException {
        String serviceType;
        if (StringUtils.isEmpty(type)) {
            serviceType = TYPE_CONSOLE;
        } else {
            serviceType = type;
        }

        IPublishService service = null;

        switch (serviceType) {
            case TYPE_GBQ:
                service = gbqService;
                break;
            case TYPE_CONSOLE:
                service = consoleService;
                break;
        }

        return service;
    }

}
