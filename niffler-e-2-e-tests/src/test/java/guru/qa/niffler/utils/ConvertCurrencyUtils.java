package guru.qa.niffler.utils;

import java.util.function.Function;

public class ConvertCurrencyUtils {

    public static Function<String, String> convertCurrency = str -> switch (str) {
        case "RUB" -> "₽";
        case "EUR" -> "€";
        case "USD" -> "$";
        case "KZT" -> "₸";
        default -> str;
    };
}
