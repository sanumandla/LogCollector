package com.bigdata.logcollector.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import static com.bigdata.logcollector.LogCollectorConstants.*;

/**
 * @author sanumandla
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GBQRow {

    @JsonProperty(REQUEST_URI)
    private String requestUri;

    @JsonProperty(REQUEST_METHOD)
    private String requestMethod;

    private String protocol;

    @JsonProperty(CONTENT_TYPE)
    private String contentType;

    @JsonProperty(SERVER_NAME)
    private String serverName;

    @JsonProperty(SERVER_PORT)
    private int serverPort;

    @JsonProperty(REMOTE_SERVER_NAME)
    private String remoteServerName;

    @JsonProperty(REMOTE_SERVER_PORT)
    private int remoteServerPort;

    private long timestamp;

}
