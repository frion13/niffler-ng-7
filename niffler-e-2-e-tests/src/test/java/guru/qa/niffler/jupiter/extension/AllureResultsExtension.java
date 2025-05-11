package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.model.allure.AllureResult;
import guru.qa.niffler.model.allure.AllureResults;
import guru.qa.niffler.service.AllureApiClient;
import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Stream;

public class AllureResultsExtension implements SuiteExtension {
    private static final Logger LOG = LoggerFactory.getLogger(AllureResultsExtension.class);
    private static final boolean isDocker = "docker".equals(System.getProperty("test.env"));
    private static final AllureApiClient allureApiClient = new AllureApiClient();
    private static final String projectId = "niffler-ng-7";
    private final Path pathToAllure = Path.of("/niffler-e-2-e-tests/build/allure-results");
    private static final Base64.Encoder encoder = Base64.getEncoder();

    @Override
    public void beforeSuite(ExtensionContext context) {
        if (isDocker) {
            try {
                allureApiClient.createProjectIfNotExist(projectId);
                allureApiClient.clean(projectId);
            } catch (Exception e) {
                LOG.error("Failed to initialize Allure project", e);
                throw new RuntimeException("Failed to initialize Allure project", e);
            }
        }
    }

    @Override
    @SneakyThrows
    public void afterSuite() {
        if (isDocker) {
            try (Stream<Path> paths = Files.walk(pathToAllure).filter(Files::isRegularFile)) {
                final List<Path> pathsList = paths.toList();
                List<AllureResult> results = new ArrayList<>();
                for (Path path : pathsList) {
                    try (InputStream is = Files.newInputStream(pathToAllure)) {
                        results.add(new AllureResult(
                                        path.getFileName().toString(),
                                        encoder.encodeToString(is.readAllBytes())
                                )
                        );
                    }
                }
                final AllureResults allureResults = new AllureResults(results);
                allureApiClient.sendResults(
                        projectId,
                        allureResults);
                allureApiClient.generateReport(projectId,
                        null,
                        null,
                        null);
            }
        }
    }
}