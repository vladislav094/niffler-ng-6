package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementsCondition;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.utils.ConvertCurrencyUtils;
import org.apache.commons.lang.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SpendConditions {

    public static WebElementsCondition spends(@NotNull SpendJson... expectedSpends) {

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
        // создаю список строк с описанием трат из данных в аннотации теста
        // далее буду сравнивать строки из списка с ожидаемыми данными и актуальными
        final List<String> expectedSpendsTextList = new ArrayList<>(Arrays.stream(expectedSpends)
                .map(spendJson -> spendJson.category().name() +
                        " " + spendJson.amount().intValue() +
                        " " + ConvertCurrencyUtils.convertCurrency.apply(spendJson.currency().name()) +
                        " " + spendJson.description() +
                        " " + sdf.format(spendJson.spendDate()))
                .toList());
        final String expectedSpendsText = expectedSpendsTextList.toString();

        return new WebElementsCondition() {

            @NotNull
            @Override
            public CheckResult check(@NotNull Driver driver, @NotNull List<WebElement> elements) {
                if (ArrayUtils.isEmpty(expectedSpends)) {
                    throw new IllegalArgumentException("No expected spends given");
                }

                if (expectedSpends.length != elements.size()) {
                    String message = String.format("List size mismatch (expected: %s, actual: %s)",
                            expectedSpends.length, elements.size());
                    return CheckResult.rejected(message, elements.size());
                }

                // создаю массив, в который буду добавлять строки с актуальными данными спендинга
                List<String> actualSpendsTextList = new ArrayList<>();

                // итерация по строкам спендингов в таблице на главной странице
                for (WebElement element : elements) {
                    // использую временную потокобезопасную строку, в которую буду аппендить текст элементов из каждого
                    // спендинга в таблице
                    StringBuilder tempString = new StringBuilder();

                    List<WebElement> cells = element.findElements(By.tagName("td"));
                    // перебираю каждый элемент строки в таблице спендингов и добавляю в строку, соблюдая протокол, чтобы
                    // было так же однообразно оформлено, как и строки в списке expectedSpendsTextList
                    tempString.append(cells.get(1).getText().trim())
                            .append(" ")
                            .append(cells.get(2).getText().trim())
                            .append(" ")
                            .append(cells.get(3).getText().trim())
                            .append(" ")
                            .append(cells.get(4).getText().trim());

                    actualSpendsTextList.add(tempString.toString());
                }

                // сортирую оба массива в алфавитном порядке
                expectedSpendsTextList.sort(String::compareTo);
                actualSpendsTextList.sort(String::compareTo);

                // если passed == false, в это условие код не попадёт и продолжит исполнение оставшейся части программы
                boolean passed = actualSpendsTextList.equals(expectedSpendsTextList);


                if (!passed) {
                    String actualSpendsText = actualSpendsTextList.toString();
                    String message = String.format("Spendings mismatch (expected: %s, actual: %s)", expectedSpendsText,
                            actualSpendsText);
                    return CheckResult.rejected(message, actualSpendsText);
                }

                return CheckResult.accepted();
            }

            @Override
            public String toString() {
                return expectedSpendsText;
            }
        };
    }
}
