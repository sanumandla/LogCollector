package com.bigdata.logcollector;

import com.bigdata.logcollector.exception.LogCollectorException;
import com.bigdata.logcollector.service.ConsoleService;
import com.bigdata.logcollector.service.GBQService;
import com.bigdata.logcollector.service.IPublishService;
import com.google.api.client.util.Lists;
import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TableSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;

import java.util.List;
import java.util.Properties;

import static com.bigdata.logcollector.LogCollectorConstants.*;

/**
 * @author sanumandla
 */
@Slf4j
@SpringBootApplication
@EnableAspectJAutoProxy
@PropertySource("classpath:log.collector.properties")
public class LogCollectorApp {

    @Value("${" + APPLICATION_NAME_PROP_KEY + "}")
    private String appName;

    @Value("${" + CREDENTIALS_JSON_PROP_KEY + "}")
    private String credentialsJson;

    @Value("${" + PROJECT_ID_PROP_KEY + "}")
    private String projectId;

    @Value("${" + DATASET_ID_PROP_KEY + "}")
    private String datasetId;

    @Value("${" + TABLE_ID_PROP_KEY + "}")
    private String tableId;

    @Bean(GBQ_SERVICE)
    public GBQService getGBQService() throws LogCollectorException {
        return new GBQService(getConfigProperties(), getTableSchema());
    }

    @Bean(CONSOLE_SERVICE)
    public ConsoleService getConsoleService() throws LogCollectorException {
        return new ConsoleService();
    }

    @Bean(LOG_COLLECTOR_PROPERTIES)
    public Properties getConfigProperties() {
        Properties properties = new Properties();
        properties.setProperty(APPLICATION_NAME, appName);
        properties.setProperty(CREDENTIALS_JSON, credentialsJson);
        properties.setProperty(PROJECT_ID, projectId);
        properties.setProperty(DATASET_ID, datasetId);
        properties.setProperty(TABLE_ID, tableId);

        return properties;
    }

    @Bean(LOG_COLLECTOR_TABLE_SCHEMA)
    public TableSchema getTableSchema() {
        List<TableFieldSchema> fields = Lists.newArrayList();
        fields.add(new TableFieldSchema().setName(REQUEST_URI).setType(DataType.STRING.name()).setMode(ModeType.NULLABLE.name()));
        fields.add(new TableFieldSchema().setName(REQUEST_METHOD).setType(DataType.STRING.name()).setMode(ModeType.NULLABLE.name()));
        fields.add(new TableFieldSchema().setName(PROTOCOL).setType(DataType.STRING.name()).setMode(ModeType.NULLABLE.name()));
        fields.add(new TableFieldSchema().setName(CONTENT_TYPE).setType(DataType.STRING.name()).setMode(ModeType.NULLABLE.name()));
        fields.add(new TableFieldSchema().setName(SERVER_NAME).setType(DataType.STRING.name()).setMode(ModeType.NULLABLE.name()));
        fields.add(new TableFieldSchema().setName(SERVER_PORT).setType(DataType.INTEGER.name()).setMode(ModeType.NULLABLE.name()));
        fields.add(new TableFieldSchema().setName(REMOTE_SERVER_NAME).setType(DataType.STRING.name()).setMode(ModeType.NULLABLE.name()));
        fields.add(new TableFieldSchema().setName(REMOTE_SERVER_PORT).setType(DataType.INTEGER.name()).setMode(ModeType.NULLABLE.name()));
        fields.add(new TableFieldSchema().setName(TIMESTAMP).setType(DataType.INTEGER.name()).setMode(ModeType.REQUIRED.name()));

        TableSchema tableSchema = new TableSchema();
        tableSchema.setFields(fields);

        return tableSchema;
    }

    public static void main(String[] args) {
        SpringApplication.run(LogCollectorApp.class, args);
        log.info("Initializing {}", LogCollectorApp.class.getName());
    }

}
