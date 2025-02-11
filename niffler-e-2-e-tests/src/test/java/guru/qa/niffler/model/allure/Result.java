package guru.qa.niffler.model.allure;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Result {

    @JsonProperty("file_name")
    private String fileName;

    @JsonProperty("content_base64")
    private String contentBase64;
}
