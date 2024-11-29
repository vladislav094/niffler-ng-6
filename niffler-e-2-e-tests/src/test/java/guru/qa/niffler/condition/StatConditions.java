package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.WebElementsCondition;
import org.apache.commons.lang.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class StatConditions {

    public static WebElementCondition color(@NotNull Color expectedColor) {
        return new WebElementCondition("color") {
            @NotNull
            @Override
            public CheckResult check(Driver driver, WebElement webElement) {
                final String rgba = webElement.getCssValue("background-color");
                return new CheckResult(
                        expectedColor.rgb.equals(rgba),
                        rgba
                );
            }
        };
    }

    public static WebElementsCondition color(@NotNull Color... expectedColors) {
        return new WebElementsCondition() {

            final String expectedRgba = Arrays.stream(expectedColors)
                    .map(color -> color.rgb)
                    .toList()
                    .toString();

            @NotNull
            @Override
            public CheckResult check(@NotNull Driver driver, @NotNull List<WebElement> elements) {
                if (ArrayUtils.isEmpty(expectedColors)) {
                    throw new IllegalArgumentException("No expected colors given");
                }
                if (expectedColors.length != elements.size()) {
                    String message = String.format("List size mismatch (expected: %s, actual: %s)",
                            expectedColors.length, elements.size());
                    return CheckResult.rejected(message, elements.size());
                }

                boolean passed = true;
                List<String> actualRgbaList = new ArrayList<>();
                for (int i = 0; i < elements.size(); i++) {
                    final WebElement elementToCheck = elements.get(i);
                    final Color colorToCheck = expectedColors[i];
                    final String rgba = elementToCheck.getCssValue("background-color");
                    actualRgbaList.add(rgba);
                    if (passed) {
                        passed = colorToCheck.rgb.equals(rgba);
                    }
                }

                if (!passed) {
                    final String actualRgba = actualRgbaList.toString();
                    String message = String.format("List colors mismatch (expected: %s, actual: %s)", expectedRgba, actualRgba);

                    return CheckResult.rejected(message, actualRgba);
                }
                return CheckResult.accepted();
            }

            @Override
            public String toString() {
                return expectedRgba;
            }
        };
    }

    public static WebElementsCondition statBubbles(@NotNull Bubble... expectedBubbles) {
        return new WebElementsCondition() {

            final String expectedColorAndText = Arrays.stream(expectedBubbles)
                    .map(b -> b.color().rgb + " " + b.text())
                    .toList()
                    .toString();

            @NotNull
            @Override
            public CheckResult check(@NotNull Driver driver, @NotNull List<WebElement> elements) {
                if (ArrayUtils.isEmpty(expectedBubbles)) {
                    throw new IllegalArgumentException("No expected bubbles given");
                }
                if (expectedBubbles.length != elements.size()) {
                    final String message = String.format("List size mismatch (expected: %s, actual: %s)",
                            expectedBubbles.length, elements.size());
                    return CheckResult.rejected(message, elements.size());
                }

                boolean passed = true;
                List<String> actualColorAndTextList = new ArrayList<>();
                for (int i = 0; i < elements.size(); i++) {
                    final WebElement elementToCheck = elements.get(i);
                    //ожидаемые атрибуты веб-элемента
                    final Color coloToCheck = expectedBubbles[i].color();
                    final String textToCheck = expectedBubbles[i].text();
                    //актуальные атрибуты веб-элемента
                    final String rgba = elementToCheck.getCssValue("background-color");
                    final String elementText = elementToCheck.getText();
                    actualColorAndTextList.add(rgba + " " + elementText);

                    if (passed) {
                        passed = coloToCheck.rgb.equals(rgba) && textToCheck.equals(elementText);
                    }
                }

                if (!passed) {
                    final String actualRgba = actualColorAndTextList.toString();
                    String message = String.format("Bubbles mismatch (expected: %s, actual: %s)", expectedColorAndText,
                            actualRgba);

                    return CheckResult.rejected(message, actualRgba);
                }
                return CheckResult.accepted();
            }

            @Override
            public String toString() {
                return expectedColorAndText;
            }
        };
    }

    public static WebElementsCondition statBubblesInAnyOrder(@NotNull Bubble... expectedBubbles) {
        return new WebElementsCondition() {

            final String expectedColorAndText = Arrays.stream(expectedBubbles)
                    .map(b -> b.color().rgb + " " + b.text())
                    .toList()
                    .toString();

            @NotNull
            @Override
            public CheckResult check(@NotNull Driver driver, @NotNull List<WebElement> elements) {
                List<Bubble> sortedExpectedBubblesList = Arrays.stream(expectedBubbles)
                        .sorted(Comparator.comparing(Bubble::text))
                        .toList();

                elements.sort(Comparator.comparing(WebElement::getText));

                if (ArrayUtils.isEmpty(expectedBubbles)) {
                    throw new IllegalArgumentException("No expected bubbles given");
                }
                if (expectedBubbles.length != elements.size()) {
                    final String message = String.format("List size mismatch (expected: %s, actual: %s)",
                            expectedBubbles.length, elements.size());
                    return CheckResult.rejected(message, elements.size());
                }

                boolean passed = true;
                List<String> actualColorAndTextList = new ArrayList<>();
                for (int i = 0; i < elements.size(); i++) {
                    final WebElement elementToCheck = elements.get(i);
                    //ожидаемые атрибуты веб-элемента
                    final Color coloToCheck = sortedExpectedBubblesList.get(i).color();
                    final String textToCheck = sortedExpectedBubblesList.get(i).text();
                    //актуальные атрибуты веб-элемента
                    final String rgba = elementToCheck.getCssValue("background-color");
                    final String elementText = elementToCheck.getText();
                    actualColorAndTextList.add(rgba + " " + elementText);

                    if (passed) {
                        passed = coloToCheck.rgb.equals(rgba) && textToCheck.equals(elementText);
                    }
                }

                if (!passed) {
                    final String actualRgba = actualColorAndTextList.toString();
                    String message = String.format("Bubbles mismatch (expected: %s, actual: %s)", expectedColorAndText,
                            actualRgba);

                    return CheckResult.rejected(message, actualRgba);
                }
                return CheckResult.accepted();
            }

            @Override
            public String toString() {
                return expectedColorAndText;
            }
        };
    }

    public static WebElementsCondition statBubblesContains(@NotNull Bubble... expectedBubbles) {
        return new WebElementsCondition() {
            final List<String> expectedColorAndTextList = Arrays.stream(expectedBubbles)
                    .map(b -> b.color().rgb + " " + b.text())
                    .toList();

            final String expectedColorAndText = expectedColorAndTextList
                    .toString();

            @NotNull
            @Override
            public CheckResult check(@NotNull Driver driver, @NotNull List<WebElement> elements) {
                if (ArrayUtils.isEmpty(expectedBubbles)) {
                    throw new IllegalArgumentException("No expected bubbles given");
                }

                List<String> actualColorAndTextList = new ArrayList<>();
                for (final WebElement elementToCheck : elements) {
                    //актуальные атрибуты веб-элемента
                    final String rgba = elementToCheck.getCssValue("background-color");
                    final String elementText = elementToCheck.getText();
                    actualColorAndTextList.add(rgba + " " + elementText);
                }

                if (actualColorAndTextList.containsAll(expectedColorAndTextList)) {
                    return CheckResult.accepted();
                } else {
                    final String actualRgba = actualColorAndTextList.toString();
                    String message = String.format("Bubbles mismatch (expected: %s, actual: %s)",
                            expectedColorAndTextList, actualRgba);
                    return CheckResult.rejected(message, actualRgba);
                }
            }

            @Override
            public String toString() {
                return expectedColorAndText;
            }
        };
    }
}
