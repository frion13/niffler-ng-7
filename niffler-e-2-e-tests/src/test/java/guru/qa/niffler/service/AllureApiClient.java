package guru.qa.niffler.service;

import guru.qa.niffler.api.AllureApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.model.allure.AllureResults;
import guru.qa.niffler.model.allure.Project;
import io.qameta.allure.Step;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AllureApiClient extends RestClient {

    private final AllureApi allureApi;

    public AllureApiClient() {
        super(CFG.allureDockerServiceUrl());
        this.allureApi = create(AllureApi.class);
    }

    @Step("Clean results")
    public void clean(String projectId) {
        try {
            allureApi.cleanResults(projectId).execute();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    @Step("Generate allure report")
    public void generateReport(String projectId,
                               String executionName,
                               String executionFrom,
                               String executionType) throws IOException {
        allureApi.generateReport(projectId, executionName, executionFrom, executionType).execute();
    }

    @Step("Send allure result")
    public void sendResults(String projectId, AllureResults results) throws IOException {
        allureApi.sendResults(projectId, results).execute();
    }

    @Step("Get project")
    public void project(String projectId) throws IOException {
        allureApi.project(projectId).execute();
    }

    @Step("Get project")
    public void createProject(Project project) {
        try {
            allureApi.createProject(project).execute();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void createProjectIfNotExist(String projectId) throws IOException {
        int code = allureApi.project(
                projectId
        ).execute().code();
        if (code==404){
            code = allureApi.createProject(new Project(projectId))
                    .execute().code();
        assertEquals(201, code);
        }else {
            assertEquals(200, code);
        }
    }
}