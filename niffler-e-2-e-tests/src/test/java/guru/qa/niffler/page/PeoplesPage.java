package guru.qa.niffler.page;

public class PeoplesPage extends BasePage<PeoplesPage> {

    public static final String URL = CFG.frontUrl() + "people/all";

    @Override
    public PeoplesPage checkThatPageLoaded() {
        return null;
    }
}
