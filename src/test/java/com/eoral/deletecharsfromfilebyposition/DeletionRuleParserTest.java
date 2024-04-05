package com.eoral.deletecharsfromfilebyposition;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DeletionRuleParserTest {

    @Test
    void shouldThrowExceptionWhenStringIsGivenAsLineNumber() {
        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            DeletionRuleParser.parseSingle("x");
        });
        assertEquals("Invalid deletion rule: x", thrown.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenZeroIsGivenAsLineNumber() {
        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            DeletionRuleParser.parseSingle("0");
        });
        assertEquals("Invalid deletion rule: 0", thrown.getMessage());
    }

    @Test
    void shouldCreateRuleWhenPositiveIntegerIsGivenAsLineNumber() {
        for (int line = 1; line <= 99; line++) {
            String str =  "" + line;
            DeletionRule rule = DeletionRuleParser.parseSingle(str);
            assertEquals(line, rule.getLine());
            assertNull(rule.getStartColumn());
            assertNull(rule.getEndColumn());
        }
    }

    @Test
    void shouldThrowExceptionWhenStringIsGivenAsStartColumnNumber() {
        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            DeletionRuleParser.parseSingle("123:x-");
        });
        assertEquals("Invalid deletion rule: 123:x-", thrown.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenZeroIsGivenAsStartColumnNumber() {
        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            DeletionRuleParser.parseSingle("123:0-");
        });
        assertEquals("Invalid deletion rule: 123:0-", thrown.getMessage());
    }

    @Test
    void shouldCreateRuleWhenPositiveIntegerIsGivenAsStartColumnNumber() {
        for (int startColumn = 1; startColumn <= 99; startColumn++) {
            int line = 123;
            String str = line + ":" + startColumn + "-";
            DeletionRule rule = DeletionRuleParser.parseSingle(str);
            assertEquals(line, rule.getLine());
            assertEquals(startColumn, rule.getStartColumn());
            assertNull(rule.getEndColumn());
        }
    }

    @Test
    void shouldThrowExceptionWhenStringIsGivenAsEndColumnNumber() {
        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            DeletionRuleParser.parseSingle("123:1-x");
        });
        assertEquals("Invalid deletion rule: 123:1-x", thrown.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenZeroIsGivenAsEndColumnNumber() {
        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            DeletionRuleParser.parseSingle("123:1-0");
        });
        assertEquals("Invalid deletion rule: 123:1-0", thrown.getMessage());
    }

    @Test
    void shouldCreateRuleWhenPositiveIntegerIsGivenAsEndColumnNumber() {
        for (int endColumn = 1; endColumn <= 99; endColumn++) {
            int line = 123;
            int startColumn = 1;
            String str = line + ":" + startColumn + "-" + endColumn;
            DeletionRule rule = DeletionRuleParser.parseSingle(str);
            assertEquals(line, rule.getLine());
            assertEquals(startColumn, rule.getStartColumn());
            assertEquals(endColumn, rule.getEndColumn());
        }
    }

    @Test
    void shouldThrowExceptionWhenDifferentDelimiterIsUsedBetweenLineNumberAndStartColumnNumber() {
        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            DeletionRuleParser.parseSingle("123|1-99");
        });
        assertEquals("Invalid deletion rule: 123|1-99", thrown.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenDifferentDelimiterIsUsedBetweenStartColumnNumberAndEndColumnNumber() {
        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            DeletionRuleParser.parseSingle("123:1|99");
        });
        assertEquals("Invalid deletion rule: 123:1|99", thrown.getMessage());
    }

    @Test
    void shouldReturnSingleRule() {
        List<DeletionRule> rules = DeletionRuleParser.parseMultiple("123:1-99");
        assertEquals(1, rules.size());
    }

    @Test
    void shouldReturnTwoRules() {
        List<DeletionRule> rules = DeletionRuleParser.parseMultiple("123:1-99,456:2-100");
        assertEquals(2, rules.size());
    }
}
