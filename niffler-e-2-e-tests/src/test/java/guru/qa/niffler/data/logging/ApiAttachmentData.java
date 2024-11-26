package guru.qa.niffler.data.logging;

import io.qameta.allure.attachment.AttachmentData;
import lombok.Getter;

@Getter
public class ApiAttachmentData implements AttachmentData {


    private final String name;
    private final String apiRequest;

    public ApiAttachmentData(String name, String apiRequest) {
        this.name = name;
        this.apiRequest = apiRequest;
    }
}
