package guru.qa.niffler.model.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public record TestData(
        @JsonIgnore @Nonnull String password,
        @JsonIgnore @Nonnull List<CategoryJson> categories,
        @JsonIgnore @Nonnull List<SpendJson> spendings,
        @JsonIgnore @Nonnull List<UserJson> friends,
        @JsonIgnore @Nonnull List<UserJson> outcomingRequest,
        @JsonIgnore @Nonnull List<UserJson> incomingRequest) {

    public TestData(@Nonnull String password) {
        this(
                password,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>());
    }
}
