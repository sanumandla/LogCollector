package com.bigdata.logcollector.exception;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sanumandla
 */
public class LogCollectorException extends Exception {

    private Map<String, String> additionalInfo = new HashMap<>();

    public LogCollectorException() {
        super();
    }

    public LogCollectorException(String message) {
        super(message);
    }

    public LogCollectorException(Throwable cause) {
        super(cause);
    }

    public LogCollectorException(String message, Throwable cause) {
        super(message, cause);
    }


    public Map<String, String> getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(Map<String, String> additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public void setAdditionalInfo(String key, String value) {
        this.additionalInfo.put(key, value);
    }

}
