package com.bigdata.logcollector;

/**
 * @author sanumandla
 */
public class LogCollectorConstants {

    private LogCollectorConstants() {
    }

    public enum DataType {
        INTEGER, STRING
    }

    public enum ModeType {
        NULLABLE, REQUIRED
    }

    public static final String LOG_COLLECTOR_PROPERTIES = "logCollectorProps";
    public static final String LOG_COLLECTOR_TABLE_SCHEMA = "logCollectorTableSchema";

    public static final String TYPE = "type";
    public static final String TYPE_GBQ = "bigquery";
    public static final String TYPE_CONSOLE = "console";
    public static final String GBQ_SERVICE = "gbqService";
    public static final String CONSOLE_SERVICE = "consoleService";

    // fields
    public static final String REQUEST_URI = "request_uri";
    public static final String REQUEST_METHOD = "request_method";
    public static final String PROTOCOL = "protocol";
    public static final String CONTENT_TYPE = "content_type";
    public static final String SERVER_NAME = "server_name";
    public static final String SERVER_PORT = "server_port";
    public static final String REMOTE_SERVER_NAME = "remote_server_name";
    public static final String REMOTE_SERVER_PORT = "remote_server_port";
    public static final String TIMESTAMP = "timestamp";

    // properties
    public static final String APPLICATION_NAME_PROP_KEY = "log.collector.applicationName";
    public static final String CREDENTIALS_JSON_PROP_KEY = "log.collector.credentialsJson";
    public static final String PROJECT_ID_PROP_KEY = "log.collector.bigquery.projectId";
    public static final String DATASET_ID_PROP_KEY = "log.collector.bigquery.datasetId";
    public static final String TABLE_ID_PROP_KEY = "log.collector.bigquery.tableId";

    public static final String APPLICATION_NAME = "applicationName";
    public static final String CREDENTIALS_JSON = "credentialsJson";
    public static final String PROJECT_ID = "projectId";
    public static final String DATASET_ID = "datasetId";
    public static final String TABLE_ID = "tableId";

}
