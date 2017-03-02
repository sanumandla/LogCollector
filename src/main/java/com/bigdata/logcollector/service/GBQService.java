package com.bigdata.logcollector.service;

import com.bigdata.logcollector.LogCollectorConstants;
import com.bigdata.logcollector.dto.GBQRow;
import com.bigdata.logcollector.exception.LogCollectorException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.util.Lists;
import com.google.api.services.bigquery.Bigquery;
import com.google.api.services.bigquery.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static com.bigdata.logcollector.LogCollectorConstants.LOG_COLLECTOR_PROPERTIES;
import static com.bigdata.logcollector.LogCollectorConstants.LOG_COLLECTOR_TABLE_SCHEMA;
import static com.bigdata.logcollector.util.GBQUtils.*;

/**
 * @author sanumandla
 */
@Slf4j
public class GBQService implements IPublishService {

    private Bigquery client;

    private String applicationName;
    private String projectId;
    private String datasetId;
    private String tableId;
    private String credentialsJson;
    private TableSchema tableSchema;

    public GBQService(Properties properties, TableSchema tableSchema) throws LogCollectorException {
        this.applicationName = properties.getProperty(LogCollectorConstants.APPLICATION_NAME);
        this.projectId = properties.getProperty(LogCollectorConstants.PROJECT_ID);
        this.datasetId = properties.getProperty(LogCollectorConstants.DATASET_ID);
        this.tableId = properties.getProperty(LogCollectorConstants.TABLE_ID);
        this.credentialsJson = properties.getProperty(LogCollectorConstants.CREDENTIALS_JSON);
        this.tableSchema = tableSchema;

        init();
    }

    public void init() throws LogCollectorException {
        this.client = createClient(applicationName, credentialsJson);

        if (!isProjectExists(client, projectId)) {
            throw new LogCollectorException("Project [" + projectId + "] not found");
        }

        if (!isDatasetExists(client, projectId, datasetId)) {
            createDataset(client, projectId, datasetId);
        }

        if (!isTableExists(client, projectId, datasetId, tableId)) {
            createTable(client, tableSchema, projectId, datasetId, tableId);
        }
    }

    public Bigquery getClient() {
        return client;
    }

    @Override
    public void publishEvent(GBQRow row) throws LogCollectorException {
        log.info("Writing record to GBQ: {}", row);

        TableRow data = new TableRow();

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> props = mapper.convertValue(row, Map.class);
        for (Map.Entry<String, Object> entry : props.entrySet()) {
            data.put(entry.getKey(), entry.getValue());
        }

        TableDataInsertAllRequest.Rows tableRow = new TableDataInsertAllRequest.Rows();
        tableRow.setInsertId(String.valueOf(System.currentTimeMillis()));
        tableRow.setJson(data);

        TableDataInsertAllRequest request = new TableDataInsertAllRequest();
        request.setRows(Collections.singletonList(tableRow));

        TableDataInsertAllResponse response;

        try {
            Bigquery.Tabledata.InsertAll insertAll = getClient().tabledata().insertAll(projectId, datasetId, tableId, request);
            response = insertAll.execute();
        } catch (Exception e) {
            log.error("Failed to write to GBQ", e);
            throw new LogCollectorException("Failed to write to GBQ", e);
        }

        checkStatus(response);
        log.debug("Record written successfully to GBQ !");
    }

    private void checkStatus(TableDataInsertAllResponse response) throws LogCollectorException {
        List<String> errors = Lists.newArrayList();

        if (response == null) {
            return;
        }

        List<TableDataInsertAllResponse.InsertErrors> insertErrorsList = response.getInsertErrors();
        if (insertErrorsList != null) {
            insertErrorsList.forEach((TableDataInsertAllResponse.InsertErrors insertErrors) -> {
                if (insertErrors != null) {
                    List<ErrorProto> errorProtoList = insertErrors.getErrors();
                    if (errorProtoList != null) {
                        errorProtoList.forEach(error -> {
                            log.error("Write to bigquery failed with message: {}, reason: {}, error: {}, debugInfo: {}", error.getMessage(), error.getReason(), error.getLocation(), error.getDebugInfo());
                            errors.add("reason" + error.getDebugInfo());
                        });
                    }
                }
            });
        }

        if (!errors.isEmpty()) {
            LogCollectorException exception = new LogCollectorException("Failed inserting records to GBQ");
            errors.forEach(error -> exception.setAdditionalInfo("debugInfo", error));
            throw exception;
        }
    }

}
