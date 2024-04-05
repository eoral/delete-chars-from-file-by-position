package com.eoral.deletecharsfromfilebyposition;

public class TestUtils {

    private TestUtils() {}

    public static DeletionRule createRuleThatDeletesLine(Integer line) {
        return new DeletionRule(line, null, null);
    }

    public static DeletionRule createRuleThatEmptiesLine(Integer line) {
        return new DeletionRule(line, 1, null);
    }
}
