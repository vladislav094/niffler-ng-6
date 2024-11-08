package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.faker.RandomDataUtils;
import guru.qa.niffler.jupiter.annotations.Category;
import guru.qa.niffler.jupiter.annotations.Spending;
import guru.qa.niffler.jupiter.annotations.User;
import guru.qa.niffler.jupiter.annotations.meta.WebTest;
import guru.qa.niffler.model.UdUserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.Test;

import java.util.Date;

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
                .checkSuccessfulMessage("New spending is successfully created");
    }
}

