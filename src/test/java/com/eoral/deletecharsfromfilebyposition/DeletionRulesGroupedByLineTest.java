package com.eoral.deletecharsfromfilebyposition;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DeletionRulesGroupedByLineTest {

    @Test
    void shouldGroupRulesByLine() {
        DeletionRule rule1 = new DeletionRule(1, 10, 20);
        DeletionRule rule2 = new DeletionRule(2, 30, 40);
        DeletionRule rule3 = new DeletionRule(2, 50, 60);
        DeletionRule rule4 = new DeletionRule(3, 70, 80);
        DeletionRule rule5 = new DeletionRule(3, 90, 100);
        DeletionRule rule6 = new DeletionRule(3, 110, 120);
        List<DeletionRule> rules = new ArrayList<>();
        rules.add(rule1);
        rules.add(rule2);
        rules.add(rule3);
        rules.add(rule4);
        rules.add(rule5);
        rules.add(rule6);
        DeletionRulesGroupedByLine deletionRulesGroupedByLine = new DeletionRulesGroupedByLine(rules);
        List<DeletionRule> rulesOfLine1 = deletionRulesGroupedByLine.getByLine(1);
        List<DeletionRule> rulesOfLine2 = deletionRulesGroupedByLine.getByLine(2);
        List<DeletionRule> rulesOfLine3 = deletionRulesGroupedByLine.getByLine(3);
        assertEquals(1, rulesOfLine1.size());
        assertTrue(rulesOfLine1.contains(rule1));
        assertEquals(2, rulesOfLine2.size());
        assertTrue(rulesOfLine2.contains(rule2));
        assertTrue(rulesOfLine2.contains(rule3));
        assertEquals(3, rulesOfLine3.size());
        assertTrue(rulesOfLine3.contains(rule4));
        assertTrue(rulesOfLine3.contains(rule5));
        assertTrue(rulesOfLine3.contains(rule6));
    }

    @Test
    void shouldReturnEmptyListWhenLineDoesNotExist() {
        List<DeletionRule> rules = new ArrayList<>();
        DeletionRulesGroupedByLine deletionRulesGroupedByLine = new DeletionRulesGroupedByLine(rules);
        List<DeletionRule> rulesOfLine1 = deletionRulesGroupedByLine.getByLine(1);
        assertEquals(0, rulesOfLine1.size());
    }
}
