package com.bigdata.logcollector.service;

import com.bigdata.logcollector.dto.GBQRow;
import com.bigdata.logcollector.exception.LogCollectorException;

/**
 * @author sanumandla
 */
public interface IPublishService {
    void publishEvent(GBQRow row) throws LogCollectorException;
}
