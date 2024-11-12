package guru.qa.niffler.page.component;

import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Selenide.$;

public class SearchField extends BaseComponent<SearchField> {

    public SearchField() {
        super($("input[placeholder='Search']"));
    }

    @Step("Fill search field and find")
    @Nonnull
    public SearchField search(String query) {
        self.setValue(query)
                .pressEnter();
        return this;
    }

    @Step("Clear search field")
    @Nonnull
    public SearchField clearIfNotEmpty() {
        $("#input-clear").click();
        return this;
    }
}
