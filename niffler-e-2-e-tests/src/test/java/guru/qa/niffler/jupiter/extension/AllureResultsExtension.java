package guru.qa.niffler.jupiter.extension;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.qa.niffler.model.allure.Result;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class AllureResultsExtension implements SuiteExtension {

    private static final Logger LOG = LoggerFactory.getLogger(AllureResultsExtension.class);
    private final String allureDockerApi = System.getenv("ALLURE_DOCKER_API");
    private final String projectName = "niffler-ng-6";
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public void afterSuite() {
        sendResult(encodeResults());
        generateReport();
    }

    @SneakyThrows
    private void sendResult(String encodeResults) {
        final String sendResults = "allure-docker-service/send-results";
        String uri = String.format("%s%s?project_id=%s&force_project_creation=true", allureDockerApi, sendResults, projectName);
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(new URI(uri))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(encodeResults))
                .build();
        httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
    }

    @SneakyThrows
    private void generateReport() {
        String generateReport = "allure-docker-service/generate-report";
        String uri = String.format("%s%s?project_id=%s&execution_name=Gradle", allureDockerApi, generateReport, projectName);
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(new URI(uri))
                .GET()
                .build();
        HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
        LOG.info("GET Response: {}", getResponse.body());
    }

    @SneakyThrows
    private String encodeResults() {
        String allureResultsPath = "niffler-e-2-e-tests/build/allure-results";
        File resultsDir = new File(allureResultsPath).getAbsoluteFile();
        ObjectMapper mapper = new ObjectMapper();
        List<Result> results = new ArrayList<>();
        Base64.Encoder encoder = Base64.getEncoder();

        for (File file : Objects.requireNonNull(resultsDir.listFiles())) {
            if (file.isFile()) {
                byte[] bytes = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
                Result result = new Result(file.getName(), encoder.encodeToString(bytes));
                results.add(result);
            }
        }
        Map<String, List<Result>> body = new HashMap<>();
        body.put("results", results);
        return mapper.writeValueAsString(body);
    }
}
