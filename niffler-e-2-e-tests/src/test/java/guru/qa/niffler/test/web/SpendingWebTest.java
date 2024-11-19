package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotations.Category;
import guru.qa.niffler.jupiter.annotations.ScreenShotTest;
import guru.qa.niffler.jupiter.annotations.Spending;
import guru.qa.niffler.jupiter.annotations.User;
import guru.qa.niffler.jupiter.annotations.meta.WebTest;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.UdUserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.utils.RandomDataUtils;
import lombok.SneakyThrows;
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
    @Test
    void categoryDescriptionShouldBeChangedFromTable(UdUserJson user) {
        final String newDescription = "Обучение Niffler Next Generation";

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .editSpending(user.testData().spendings().getFirst().description())
                .setNewSpendingDescription(newDescription)
                .save();
        new MainPage().checkThatTableContainsSpending(newDescription);
    }


    @User(
            categories = @Category
    )
    @Test
    void addNewSpending(UdUserJson user) {
        int amount = 50;
        Date date = new Date();
        String description = RandomDataUtils.randomDescription();

        Selenide.open(frontUrl, LoginPage.class)
                .login(user.username(), user.testData().password())
                .getHeader()
                .toAddSpendingPage()
                .setAmount(amount)
                .setCategory(user.testData().categories().getFirst())
                .setDate(date)
                .setDescription(description)
                .saveSpending()
                .checkAlertMessage("New spending is successfully created");
    }

    @SneakyThrows
    @User(
            spendings = @Spending(
                    category = "Обучение",
                    description = "Обучение Advanced 2.0",
                    amount = 79990
            )
    )
    @ScreenShotTest("img/expected-stat.png")
    void checkStatComponentTest(UdUserJson user, BufferedImage expected) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .checkStatisticComponent(expected);
    }


    @SneakyThrows
    @User(
            spendings = @Spending(
                    category = "Обучение",
                    description = "Обучение Advanced 2.0",
                    amount = 79990
            )
    )
    @ScreenShotTest("img/expected-edit-stat.png")
    void checkStatComponentAfterEditSpendingTest(UdUserJson user, BufferedImage expected) {
        BufferedImage imageBeforeEdit = ImageIO.read(new ClassPathResource("img/expected-stat.png").getInputStream());

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
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
    @ScreenShotTest("img/expected-deleted-stat.png")
    void checkStatComponentAfterDeleteSpendingTest(UdUserJson user, BufferedImage expected) {
        BufferedImage imageBeforeEdit = ImageIO.read(new ClassPathResource("img/expected-stat.png").getInputStream());
        List<String> categoriesName = user.testData().categories().stream()
                .map(CategoryJson::name).toList();

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .checkStatisticComponent(imageBeforeEdit)
                .checkCellsByCategoryName(List.of("Обучение 79990"))
                .checkCellsByCategoryName(categoriesName)
                .deleteSpending(user.testData().spendings().getFirst().description())
                .checkStatisticComponent(expected);
    }
}

