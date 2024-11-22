package guru.qa.niffler.utils;

import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.SelenideDriver;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;

public class BrowserConverter implements ArgumentConverter {

    @Override
    public SelenideDriver convert(Object source, ParameterContext context) throws ArgumentConversionException {
        if (!(source instanceof Browser browser)) {
            throw new ArgumentConversionException("Source must be an instance of Browser");
        }

        SelenideConfig config = switch (browser) {
            case FIREFOX -> SelenideUtils.firefoxConfig;
            case CHROME -> SelenideUtils.chromeConfig;
        };

        return new SelenideDriver(config);
    }

    public enum Browser {
        CHROME, FIREFOX
    }
}
