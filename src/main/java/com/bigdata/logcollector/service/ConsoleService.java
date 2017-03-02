package com.bigdata.logcollector.service;

import com.bigdata.logcollector.dto.GBQRow;
import com.bigdata.logcollector.exception.LogCollectorException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sanumandla
 */
@Slf4j
public class ConsoleService implements IPublishService {

    @Override
    public void publishEvent(GBQRow row) throws LogCollectorException {
        log.info("Writing record to console: {}", row);
    }

}
