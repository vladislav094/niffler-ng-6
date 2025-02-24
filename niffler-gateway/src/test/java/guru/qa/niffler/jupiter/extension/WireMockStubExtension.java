package guru.qa.niffler.jupiter.extension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import guru.qa.niffler.jupiter.annotation.WireMockStub;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WireMockStubExtension implements BeforeAllCallback, BeforeEachCallback, AfterEachCallback, AfterAllCallback {

    private final String mappingDirectory = "src/test/resources/wiremock/stubs/mapping/";
    private final WireMockServer wiremock = new WireMockServer(
            new WireMockConfiguration()
                    .port(8093)
                    .globalTemplating(true)
    );

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        wiremock.start();
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), WireMockStub.class)
                .ifPresent(wireMockStub -> {
                            for (String stubPath : wireMockStub.paths()) {
                                Path path = Paths.get(mappingDirectory + stubPath);
                                File file = path.toFile();
                                StubMapping stubMapping;
                                try {
                                    stubMapping = new ObjectMapper().readValue(file, StubMapping.class);
                                } catch (IOException e) {
                                    throw new RuntimeException("Не удалось загрузить мок из файла: " + file.getAbsolutePath(), e);
                                }
                                wiremock.addStubMapping(stubMapping);
                            }
                        }
                );
    }

    @Override
    public void afterEach(ExtensionContext extensionContext){
        wiremock.resetMappings();
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) {
        wiremock.shutdown();
    }
}