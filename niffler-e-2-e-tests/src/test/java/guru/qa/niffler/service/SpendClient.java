package guru.qa.niffler.service;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

public interface SpendClient {

    SpendJson createSpend(SpendJson spend);

    SpendJson updateSpend(SpendJson spend);

    CategoryJson createCategory(CategoryJson category);

    CategoryJson updateCategory(CategoryJson category);

    void removeCategory(CategoryJson category);
}
