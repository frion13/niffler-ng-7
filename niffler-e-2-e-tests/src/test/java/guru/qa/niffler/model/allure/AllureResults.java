package guru.qa.niffler.model.allure;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Objects;

public record AllureResults(
        @JsonProperty("results")
        List<AllureResult> results
) {
    public AllureResults {
        Objects.requireNonNull(results, "Results list cannot be null");
    }
}