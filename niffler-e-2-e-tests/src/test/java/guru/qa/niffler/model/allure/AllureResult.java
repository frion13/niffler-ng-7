package guru.qa.niffler.model.allure;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonInclude(JsonInclude.Include.NON_NULL)
public record AllureResult(
        @JsonProperty("file_name")
        String name,

        @JsonProperty("content_base64")
        String content
) {
}