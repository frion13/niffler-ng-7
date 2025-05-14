package guru.qa.niffler.api;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.model.allure.AllureResults;
import guru.qa.niffler.model.allure.Project;
import retrofit2.Call;
import retrofit2.http.*;


public interface AllureApi {

    @POST("allure-docker-service/send-results")
    Call<JsonNode> sendResults(@Query("project_id") String projectId,
                               @Body AllureResults results);

    @GET("allure-docker-service/projects{project_id}")
    Call<JsonNode> project(@Path("project_id") String projectId);

    @GET("allure-docker-service/clean-results")
    Call<JsonNode> cleanResults(@Query("project_id") String projectId);

    @GET("allure-docker-service/generate-report")
    Call<JsonNode> generateReport(@Query("project_id") String projectId,
                                  @Query("execution_name") String executionName,
                                  @Query(value = "execution_from", encoded = true) String executionFrom,
                                  @Query("execution_type") String executionType);

    @POST("allure-docker-service/projects")
    Call<JsonNode> createProject(@Body Project project);


}