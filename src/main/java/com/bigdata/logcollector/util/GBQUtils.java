package com.bigdata.logcollector.util;

import com.bigdata.logcollector.exception.LogCollectorException;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.api.client.util.Sets;
import com.google.api.services.bigquery.Bigquery;
import com.google.api.services.bigquery.BigqueryScopes;
import com.google.api.services.bigquery.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

/**
 * @author sanumandla
 */
@Slf4j
public class GBQUtils {

    private static final String APPLICATION_NAME = "GBQ_LogCollector_App";
    private static final String CLIENT_ERROR = "Uninitialized Google Bigquery client";
    private static final String PROJECT_ID_ERROR = "ProjectId cannot be null";
    private static final String DATASET_ID_ERROR = "DatasetId cannot be null";
    private static final String TABLE_ID_ERROR = "TableId cannot be null";

    private static HttpTransport httpTransport = new NetHttpTransport();
    private static JsonFactory jsonFactory = new JacksonFactory();

    private GBQUtils() {
    }

    public static Bigquery createClient(String applicationName, String credentialsJson) throws LogCollectorException {
        if (StringUtils.isEmpty(credentialsJson)) {
            log.error("Couldn't locate credentials json");
            throw new LogCollectorException("Couldn't locate credentials json");
        }

        Bigquery client;
        GoogleCredential credential;

        try {
            InputStream inputStream = GBQUtils.class.getClassLoader().getResourceAsStream(credentialsJson);
            if (inputStream == null) {
                log.error("Error reading credentials file");
                throw new LogCollectorException("Error reading credentials file");
            }

            credential = GoogleCredential.fromStream(inputStream, httpTransport, jsonFactory);
            if (credential.createScopedRequired()) {
                credential = credential.createScoped(getScopes());
            }

            String appName = StringUtils.isEmpty(applicationName) ? APPLICATION_NAME : applicationName;
            client = new Bigquery.Builder(httpTransport, jsonFactory, credential)
                    .setApplicationName(appName).build();
        } catch (IOException ex) {
            log.error("Error initializing bigquery client", ex);
            throw new LogCollectorException("Error initializing bigquery client", ex);
        }

        log.info("Successfully initialized google bigquery client");

        return client;
    }

    public static boolean isProjectExists(Bigquery client, String projectId) {
        Preconditions.checkArgument(client != null, CLIENT_ERROR);
        Preconditions.checkArgument(!StringUtils.isEmpty(projectId), PROJECT_ID_ERROR);

        try {
            ProjectList projectList = client.projects().list().execute();
            if (projectList == null) {
                return false;
            }

            for (ProjectList.Projects project : projectList.getProjects()) {
                if (project.getFriendlyName().equalsIgnoreCase(projectId)) {
                    return true;
                }
            }
        } catch (IOException e) {
            log.error("Project [{}] not found", projectId, e);
        }

        return false;
    }

    public static boolean isDatasetExists(Bigquery client, String projectId, String datasetId) {
        Preconditions.checkArgument(client != null, CLIENT_ERROR);
        Preconditions.checkArgument(!StringUtils.isEmpty(projectId), PROJECT_ID_ERROR);
        Preconditions.checkArgument(!StringUtils.isEmpty(datasetId), DATASET_ID_ERROR);

        try {
            client.datasets().get(projectId, datasetId).execute();
        } catch (Exception e) {
            log.error("Dataset [{}] not found", datasetId, e);
            return false;
        }

        return true;
    }

    public static void createDataset(Bigquery client, String projectId, String datasetId)
            throws LogCollectorException {
        Preconditions.checkArgument(client != null, CLIENT_ERROR);
        Preconditions.checkArgument(!StringUtils.isEmpty(projectId), PROJECT_ID_ERROR);
        Preconditions.checkArgument(!StringUtils.isEmpty(datasetId), DATASET_ID_ERROR);

        DatasetReference reference = getDatasetReference(projectId, datasetId);

        Dataset dataset = new Dataset();
        dataset.setDatasetReference(reference);

        try {
            client.datasets().insert(projectId, dataset).execute();
            log.info("Dataset [{}] created successfully in Google Biquery", datasetId);
        } catch (Exception e) {
            log.error("Error creating dataset [{}] in Google Bigquery", datasetId, e);
            throw new LogCollectorException("Error creating dataset [" + datasetId + "] in Google Biquery", e);
        }
    }

    public static boolean isTableExists(Bigquery client, String projectId, String datasetId, String tableId) {
        Preconditions.checkArgument(client != null, CLIENT_ERROR);
        Preconditions.checkArgument(!StringUtils.isEmpty(projectId), PROJECT_ID_ERROR);
        Preconditions.checkArgument(!StringUtils.isEmpty(datasetId), DATASET_ID_ERROR);
        Preconditions.checkArgument(!StringUtils.isEmpty(tableId), TABLE_ID_ERROR);

        try {
            client.tables().get(projectId, datasetId, tableId).execute();
        } catch (Exception e) {
            log.error("Table [{}] not found", tableId, e);
            return false;
        }

        return true;
    }

    public static void createTable(Bigquery client, TableSchema schema, String projectId, String datasetId, String tableId)
            throws LogCollectorException {
        Preconditions.checkArgument(client != null, CLIENT_ERROR);
        Preconditions.checkArgument(schema != null, "Table schema cannot be null");
        Preconditions.checkArgument(!StringUtils.isEmpty(projectId), PROJECT_ID_ERROR);
        Preconditions.checkArgument(!StringUtils.isEmpty(datasetId), DATASET_ID_ERROR);
        Preconditions.checkArgument(!StringUtils.isEmpty(tableId), TABLE_ID_ERROR);


        TableReference reference = getTableReference(projectId, datasetId, tableId);

        Table table = new Table();
        table.setTableReference(reference);
        table.setSchema(schema);

        try {
            client.tables().insert(projectId, datasetId, table).execute();
            log.info("Table [{}] created successfully in Google Biquery", tableId);
        } catch (Exception e) {
            log.error("Error creating table [{}] in Google Bigquery", tableId, e);
            throw new LogCollectorException("Error creating table [" + tableId + "] in Google Biquery", e);
        }
    }

    private static DatasetReference getDatasetReference(String projectId, String datasetId) {
        DatasetReference reference = new DatasetReference();
        reference.setProjectId(projectId);
        reference.setDatasetId(datasetId);

        return reference;
    }

    private static TableReference getTableReference(String projectId, String datasetId, String tableId) {
        TableReference reference = new TableReference();
        reference.setProjectId(projectId);
        reference.setDatasetId(datasetId);
        reference.setTableId(tableId);

        return reference;
    }

    private static Set<String> getScopes() {
        Set<String> scopes = Sets.newHashSet();
        scopes.add(BigqueryScopes.BIGQUERY);

        log.debug("Assigned scopes for {} are {}", APPLICATION_NAME, scopes);

        return scopes;
    }

}
