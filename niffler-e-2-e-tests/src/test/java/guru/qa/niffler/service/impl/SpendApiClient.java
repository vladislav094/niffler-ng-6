package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.SpendApi;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.service.SpendClient;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.apache.hc.core5.http.HttpStatus.SC_CREATED;
import static org.apache.hc.core5.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public class SpendApiClient implements SpendClient {

    private static final Config CFG = Config.getInstance();
    private final SpendApi spendApi = new RestClient.EmptyClient(CFG.spendUrl()).create(SpendApi.class);

    @Override
    @Nonnull
    public SpendJson createSpend(@Nonnull SpendJson spendJson) {
        final Response<SpendJson> response;
        try {
            response = spendApi.addSpend(spendJson)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(SC_CREATED, response.code());
        return requireNonNull(response.body());
    }

    @Override
    @Nonnull
    public SpendJson updateSpend(@Nonnull SpendJson spendJson) {
        final Response<SpendJson> response;
        try {
            response = spendApi.editSpend(spendJson)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(SC_OK, response.code());
        return requireNonNull(response.body());
    }

    @Nullable
    public SpendJson getSpend(@Nonnull String id) {
        final Response<SpendJson> response;
        try {
            response = spendApi.getSpendById(id)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(SC_OK, response.code());
        return response.body();
    }

    @Nullable
    public List<SpendJson> getAllSpend(String username,
                                                 @Nullable CurrencyValues currency,
                                                 @Nullable String from,
                                                 @Nullable String to) {
        final Response<List<SpendJson>> response;
        try {
            response = spendApi.getAllSpends(username, currency, from, to)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(SC_OK, response.code());
        return response.body() != null
                ? response.body()
                : Collections.emptyList();
    }

    public void removeSpends(String username, List<String> ids) {
        Response<Void> response;
        try {
            response = spendApi.removeSpends(username, ids)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(SC_OK, response.code());
    }

    @Override
    @Nonnull
    public CategoryJson createCategory(CategoryJson categoryJson) {
        final Response<CategoryJson> response;
        try {
            response = spendApi.addCategory(categoryJson)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(SC_OK, response.code());
        return requireNonNull(response.body());
    }

    @Override
    @Nonnull
    public CategoryJson updateCategory(CategoryJson categoryJson) {
        final Response<CategoryJson> response;
        try {
            response = spendApi.updateCategory(categoryJson)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(SC_OK, response.code());
        return requireNonNull(response.body());
    }

    @Nullable
    public List<CategoryJson> getAllCategories(boolean excludeArchived) {
        final Response<List<CategoryJson>> response;
        try {
            response = spendApi.getAllCategories(excludeArchived)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(SC_OK, response.code());
        return response.body() != null
                ? response.body()
                : Collections.emptyList();
    }

    @Override
    public void removeCategory(@Nonnull CategoryJson category) {
        throw new UnsupportedOperationException("Category can't remove by API");
    }
}
