package guru.qa.niffler.condition;

public enum Color {
    yellow("rgba(255, 183, 3, 1)"), green("rgba(53, 173, 123, 1)");

    public final String rgb;

    Color(String rgb) {
        this.rgb = rgb;
    }
}
