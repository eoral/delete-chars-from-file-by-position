package com.eoral.deletecharsfromfilebyposition;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DeletionRuleTest {

    @Test
    void shouldThrowExceptionWhenLineNumberIsNull() {
        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new DeletionRule(null, null, null);
        });
        assertEquals("Line must be greater than or equal to 1.", thrown.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenLineNumberIsLessThanOne() {
        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new DeletionRule(0, null, null);
        });
        assertEquals("Line must be greater than or equal to 1.", thrown.getMessage());
    }

    @Test
    void shouldCreateRuleWhenLineNumberIsGiven() {
        DeletionRule rule = new DeletionRule(1, null, null);
        assertEquals(1, rule.getLine());
        assertNull(rule.getStartColumn());
        assertNull(rule.getEndColumn());
    }

    @Test
    void shouldThrowExceptionWhenStartColumnNumberIsLessThanOne() {
        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new DeletionRule(1, 0, null);
        });
        assertEquals("Start column must be greater than or equal to 1.", thrown.getMessage());
    }

    @Test
    void shouldCreateRuleWhenLineNumberAndStartColumnNumberAreGiven() {
        DeletionRule rule = new DeletionRule(1, 3, null);
        assertEquals(1, rule.getLine());
        assertEquals(3, rule.getStartColumn());
        assertNull(rule.getEndColumn());
    }

    @Test
    void shouldThrowExceptionWhenStartColumnNumberIsNullAndEndColumnNumberIsNotNull() {
        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new DeletionRule(1, null, 3);
        });
        assertEquals("When end column is specified, start column must be specified too.", thrown.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenColumnNumbersAreNotNullButStartColumnNumberIsLessThanOne() {
        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new DeletionRule(1, 0, 3);
        });
        assertEquals("Start column must be greater than or equal to 1.", thrown.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenEndColumnNumberIsLessThanStartColumnNumber() {
        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new DeletionRule(1, 1, 0);
        });
        assertEquals("End column must be greater than or equal to start column.", thrown.getMessage());
    }

    @Test
    void shouldCreateRuleWhenAllArgsAreGiven() {
        DeletionRule rule = new DeletionRule(1, 3, 5);
        assertEquals(1, rule.getLine());
        assertEquals(3, rule.getStartColumn());
        assertEquals(5, rule.getEndColumn());
    }

    @Test
    void deletesLineShouldReturnExpectedResult() {
        DeletionRule rule1 = TestUtils.createRuleThatDeletesLine(1);
        DeletionRule rule2 = TestUtils.createRuleThatEmptiesLine(1);
        DeletionRule rule3 = new DeletionRule(1, 3, 5);
        assertTrue(rule1.deletesLine());
        assertFalse(rule2.deletesLine());
        assertFalse(rule3.deletesLine());
    }

    @Test
    void emptiesLineShouldReturnExpectedResult() {
        DeletionRule rule1 = TestUtils.createRuleThatDeletesLine(1);
        DeletionRule rule2 = TestUtils.createRuleThatEmptiesLine(1);
        DeletionRule rule3 = new DeletionRule(1, 3, 5);
        assertFalse(rule1.emptiesLine());
        assertTrue(rule2.emptiesLine());
        assertFalse(rule3.emptiesLine());
    }

    @Test
    void getEffectedColumnsShouldThrowExceptionWhenRuleDeletesLine() {
        DeletionRule rule = new DeletionRule(1, null, null);
        IllegalStateException thrown = Assertions.assertThrows(IllegalStateException.class, () -> {
            rule.getEffectedColumns(5);
        });
        assertEquals("This method cannot be invoked when start column and end column are null.", thrown.getMessage());
    }

    @Test
    void getEffectedColumnsShouldReturnEmptySetWhenStartColumnNumberIsGreaterThanLineLength() {
        DeletionRule rule = new DeletionRule(1, 7, null);
        Set<Integer> effectedColumns = rule.getEffectedColumns(5);
        assertTrue(effectedColumns.isEmpty());
    }

    @Test
    void getEffectedColumnsShouldReturnColumnsWhenStartColumnNumberIsEqualToLineLengthAndEndColumnNumberIsNull() {
        DeletionRule rule = new DeletionRule(1, 5, null);
        Set<Integer> effectedColumns = rule.getEffectedColumns(5);
        assertEquals(1, effectedColumns.size());
        assertTrue(effectedColumns.contains(5));
    }

    @Test
    void getEffectedColumnsShouldReturnColumnsWhenStartColumnNumberIsEqualToLineLengthAndEndColumnNumberIsEqualToLineLength() {
        DeletionRule rule = new DeletionRule(1, 5, 5);
        Set<Integer> effectedColumns = rule.getEffectedColumns(5);
        assertEquals(1, effectedColumns.size());
        assertTrue(effectedColumns.contains(5));
    }

    @Test
    void getEffectedColumnsShouldReturnColumnsWhenStartColumnNumberIsEqualToLineLengthAndEndColumnNumberIsGreaterThanLineLength() {
        DeletionRule rule = new DeletionRule(1, 5, 8);
        Set<Integer> effectedColumns = rule.getEffectedColumns(5);
        assertEquals(1, effectedColumns.size());
        assertTrue(effectedColumns.contains(5));
    }

    @Test
    void getEffectedColumnsShouldReturnColumnsWhenStartColumnNumberIsLessThanLineLengthAndEndColumnNumberIsNull() {
        DeletionRule rule = new DeletionRule(1, 3, null);
        Set<Integer> effectedColumns = rule.getEffectedColumns(5);
        assertEquals(3, effectedColumns.size());
        assertTrue(effectedColumns.contains(3));
        assertTrue(effectedColumns.contains(4));
        assertTrue(effectedColumns.contains(5));
    }

    @Test
    void getEffectedColumnsShouldReturnColumnsWhenStartColumnNumberIsLessThanLineLengthAndEndColumnNumberIsLessThanLineLength() {
        DeletionRule rule = new DeletionRule(1, 3, 4);
        Set<Integer> effectedColumns = rule.getEffectedColumns(5);
        assertEquals(2, effectedColumns.size());
        assertTrue(effectedColumns.contains(3));
        assertTrue(effectedColumns.contains(4));
    }

    @Test
    void getEffectedColumnsShouldReturnColumnsWhenStartColumnNumberIsLessThanLineLengthAndEndColumnNumberIsEqualToLineLength() {
        DeletionRule rule = new DeletionRule(1, 3, 5);
        Set<Integer> effectedColumns = rule.getEffectedColumns(5);
        assertEquals(3, effectedColumns.size());
        assertTrue(effectedColumns.contains(3));
        assertTrue(effectedColumns.contains(4));
        assertTrue(effectedColumns.contains(5));
    }

    @Test
    void getEffectedColumnsShouldReturnColumnsWhenStartColumnNumberIsLessThanLineLengthAndEndColumnNumberIsGreaterThanLineLength() {
        DeletionRule rule = new DeletionRule(1, 3, 8);
        Set<Integer> effectedColumns = rule.getEffectedColumns(5);
        assertEquals(3, effectedColumns.size());
        assertTrue(effectedColumns.contains(3));
        assertTrue(effectedColumns.contains(4));
        assertTrue(effectedColumns.contains(5));
    }
}
