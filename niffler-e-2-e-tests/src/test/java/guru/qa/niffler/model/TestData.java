package guru.qa.niffler.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public record TestData(
        @JsonIgnore @Nonnull String password,
        @JsonIgnore @Nonnull List<CategoryJson> categories,
        @JsonIgnore @Nonnull List<SpendJson> spendings,
        @JsonIgnore @Nonnull List<UdUserJson> friends,
        @JsonIgnore @Nonnull List<UdUserJson> outcomingRequest,
        @JsonIgnore @Nonnull List<UdUserJson> incomingRequest) {

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
