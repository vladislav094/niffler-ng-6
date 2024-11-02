package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class Calendar {

    private final SelenideElement inputDate = $("[placeholder='MM/DD/YYYY']");
    private final SelenideElement calendarButton = $("button[aria-label*='Choose date']");

    public Calendar setDate(String value) {
        inputDate.setValue(value);
        return this;
    }
}
