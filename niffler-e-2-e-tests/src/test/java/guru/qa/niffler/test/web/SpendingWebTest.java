package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.condition.Bubble;
import guru.qa.niffler.condition.Color;
import guru.qa.niffler.jupiter.annotations.*;
import guru.qa.niffler.jupiter.annotations.meta.WebTest;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UdUserJson;
import guru.qa.niffler.page.EditSpendingPage;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.component.SpendingTable;
import guru.qa.niffler.page.component.StatComponent;
import guru.qa.niffler.utils.RandomDataUtils;
import io.qameta.allure.Story;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.List;

@WebTest
public class SpendingWebTest extends BaseWebTest {

    @User(
            spendings = @Spending(
                    category = "Обучение",
                    description = "Обучение Advanced 2.0",
                    amount = 79990
            )
    )
    @ApiLogin
    @Test
    void categoryDescriptionShouldBeChangedFromTable() {
        final String newDescription = "Обучение Niffler Next Generation";

        Selenide.open(EditSpendingPage.URL, EditSpendingPage.class)
                .setNewSpendingDescription(newDescription)
                .save();
        new MainPage().checkThatTableContainsSpending(newDescription);
    }


    @User(
            categories = @Category
    )
    @ApiLogin
    @Test
    void addNewSpending(UdUserJson user) {
        int amount = 50;
        Date date = new Date();
        String description = RandomDataUtils.randomDescription();

        Selenide.open(EditSpendingPage.URL, EditSpendingPage.class)
                .setAmount(amount)
                .setCategory(user.testData().categories().getFirst())
                .setDate(date)
                .setDescription(description)
                .saveSpending()
                .checkAlertMessage("New spending is successfully created");
    }

    @SneakyThrows
    @User(
            spendings = {
                    @Spending(
                            category = "Обучение",
                            description = "Обучение Advanced 2.0",
                            amount = 79990
                    ),
                    @Spending(
                            category = "Рыбалка",
                            description = "Рыбалка на Волге",
                            amount = 400
                    )
            }
    )
    @ApiLogin
    @ScreenShotTest("img/expected-stat.png")
    @Story("Скриншотный тест компонента статистики на главно странице")
    @DisplayName("Сравниваем состояние компонента статистик с имеющимся скриншотом, который соответствует данным аннотации")
    void checkStatComponentTest() {
        StatComponent statComponent = new StatComponent();

        Selenide.open(MainPage.URL, MainPage.class)
                .checkStatisticComponent(statComponent.chartScreenshot());
    }


    @SneakyThrows
    @User(
            spendings = @Spending(
                    category = "Обучение",
                    description = "Обучение Advanced 2.0",
                    amount = 79990
            )
    )
    @ApiLogin
    @ScreenShotTest("img/expected-edit-stat.png")
    @Story("Скриншотный тест компонента статистики на главно странице")
    @DisplayName("Сравниваем состояние компонента статистик с имеющимся скриншотом, который соответствует данным аннотации")
    void checkStatComponentAfterEditSpendingTest(UdUserJson user, BufferedImage expected) {
        BufferedImage imageBeforeEdit = ImageIO.read(new ClassPathResource("img/expected-stat.png").getInputStream());

        Selenide.open(MainPage.URL, MainPage.class)
                .checkStatisticComponent(imageBeforeEdit)
                .checkCellsByCategoryName(List.of("Обучение 79990"))
                .editSpending(user.testData().spendings().getFirst().description())
                .setAmount(50000)
                .saveSpending()
                .checkStatisticComponent(expected)
                .checkCellsByCategoryName(List.of("Обучение 50000"));
    }

    @SneakyThrows
    @User(
            spendings = @Spending(
                    category = "Обучение",
                    description = "Обучение Advanced 2.0",
                    amount = 79990
            )
    )
    @ApiLogin
    @ScreenShotTest("img/expected-deleted-stat.png")
    @Story("Скриншотный тест компонента статистики на главно странице")
    @DisplayName("Сравниваем состояние компонента статистик с имеющимся скриншотом, который соответствует данным аннотации")
    void checkStatComponentAfterDeleteSpendingTest(UdUserJson user, BufferedImage expected) {
        BufferedImage imageBeforeEdit = ImageIO.read(new ClassPathResource("img/expected-stat.png").getInputStream());
        List<String> categoriesName = user.testData().categories().stream()
                .map(CategoryJson::name).toList();

        Selenide.open(MainPage.URL, MainPage.class)
                .checkStatisticComponent(imageBeforeEdit)
                .checkCellsByCategoryName(List.of("Обучение 79990"))
                .checkCellsByCategoryName(categoriesName)
                .deleteSpending(user.testData().spendings().getFirst().description())
                .checkStatisticComponent(expected);
    }

    @SneakyThrows
    @User(
            spendings = {
                    @Spending(
                            category = "Обучение",
                            description = "Обучение Advanced 2.0",
                            amount = 79990
                    ),
                    @Spending(
                            category = "Рыбалка",
                            description = "Рыбалка на Волге",
                            amount = 400
                    )
            }
    )
    @Test
    @ApiLogin
    @Story("Состояние таблицы трат на главной странице")
    @DisplayName("Проверяем описание трат в таблице на главной странице и сравниваем их с данными из аннотации")
    void checkSpendingsInTableOnMainPage(UdUserJson user) {
        List<SpendJson> spendings = user.testData().spendings();

        Selenide.open(MainPage.URL, MainPage.class);
        new SpendingTable().checkSpends(spendings.get(0), spendings.get(1));
    }

    @SneakyThrows
    @User(
            spendings = {
                    @Spending(
                            category = "Обучение",
                            description = "Обучение Advanced 2.0",
                            amount = 79990
                    ),
                    @Spending(
                            category = "Рыбалка",
                            description = "Рыбалка на Волге",
                            amount = 400
                    )
            }
    )
    @ApiLogin
    @Test
    @Story("Состояние плашек с тратами на главной странице")
    @DisplayName("Проверяем описание трат в плашках под компонентом статистики на главной странице")
    void checkBubblesInStatComponent(UdUserJson user) {
        Bubble firstBubble = new Bubble(Color.yellow, "Обучение 79990 ₽");
        Bubble secondBubble = new Bubble(Color.green, "Рыбалка 400 ₽");
        StatComponent statComponent = new StatComponent();

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password());
        statComponent.checkBubbles(Color.yellow, Color.green)
                .checkBubbles(firstBubble, secondBubble)
                .checkBubblesInAnyOrder(secondBubble, firstBubble)
                .checkBubblesContains(firstBubble);
    }
}

