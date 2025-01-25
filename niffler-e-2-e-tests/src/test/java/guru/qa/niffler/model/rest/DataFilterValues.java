package guru.qa.niffler.model.rest;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum DataFilterValues {
    MONTH("Last month"), WEEK("Last week"), TODAY("Today");
    public final String text;
}
