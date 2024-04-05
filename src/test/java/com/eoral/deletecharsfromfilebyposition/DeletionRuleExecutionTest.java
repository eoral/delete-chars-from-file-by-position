package com.eoral.deletecharsfromfilebyposition;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DeletionRuleExecutionTest {

    final Path resourcesDirPath = Paths.get("src","test", "resources").toAbsolutePath();
    final String baseTestFileName = "base-test-file.txt";
    final Path baseTestFilePath = resourcesDirPath.resolve(baseTestFileName);
    final Path tempDirectoryPath = Utils.createTempDirectory();
    final Charset charset = StandardCharsets.UTF_8;

    @AfterAll
    void tearDown() {
        Utils.deleteRecursively(tempDirectoryPath);
    }

    Path createCopyOfBaseTestFileInTempDirectory() {
        Path pathOfCopy = tempDirectoryPath.resolve(baseTestFileName);
        Utils.copyFileReplaceExisting(baseTestFilePath, pathOfCopy);
        return pathOfCopy;
    }

    @Test
    void shouldReturnLineAsIsWhenThereIsNoRule() {
        String line = "abc";
        List<DeletionRule> deletionRules = Collections.emptyList();
        String result = new DeletionRuleExecution().deleteCharsFromLine(line, deletionRules, null);
        assertEquals(line, result);
    }

    @Test
    void shouldReturnNullWhenThereIsOneRuleThatDeletesLine() {
        String line = "abc";
        List<DeletionRule> deletionRules = new ArrayList<>();
        deletionRules.add(new DeletionRule(1, 3, 5));
        deletionRules.add(TestUtils.createRuleThatEmptiesLine(1));
        deletionRules.add(TestUtils.createRuleThatDeletesLine(1));
        String result = new DeletionRuleExecution().deleteCharsFromLine(line, deletionRules, null);
        assertNull(result);
    }

    @Test
    void shouldReturnEmptyStringWhenThereIsOneRuleThatEmptiesLine() {
        String line = "abc";
        List<DeletionRule> deletionRules = new ArrayList<>();
        // Don't add a rule that deletes line because it has priority over a rule that empties line.
        deletionRules.add(new DeletionRule(1, 3, 5));
        deletionRules.add(TestUtils.createRuleThatEmptiesLine(1));
        String result = new DeletionRuleExecution().deleteCharsFromLine(line, deletionRules, null);
        assertEquals("", result);
    }

    @Test
    void shouldReturnEmptyStringWhenLineIsEmpty() {
        String line = "";
        List<DeletionRule> deletionRules = new ArrayList<>();
        // Don't add a rule that deletes line because it has priority over a rule that empties line.
        // Don't add a rule that empties line because it has priority over other rules.
        deletionRules.add(new DeletionRule(1, 3, 5));
        String result = new DeletionRuleExecution().deleteCharsFromLine(line, deletionRules, null);
        assertEquals("", result);
    }

    @Test
    void shouldDeleteSingleCharFromStart() {
        String line = "abc";
        List<DeletionRule> deletionRules = new ArrayList<>();
        deletionRules.add(new DeletionRule(1, 1, 1));
        String result = new DeletionRuleExecution().deleteCharsFromLine(line, deletionRules, null);
        assertEquals("bc", result);
    }

    @Test
    void shouldDeleteSingleCharFromMiddle() {
        String line = "abc";
        List<DeletionRule> deletionRules = new ArrayList<>();
        deletionRules.add(new DeletionRule(1, 2, 2));
        String result = new DeletionRuleExecution().deleteCharsFromLine(line, deletionRules, null);
        assertEquals("ac", result);
    }

    @Test
    void shouldDeleteSingleCharFromEnd() {
        String line = "abc";
        List<DeletionRule> deletionRules = new ArrayList<>();
        deletionRules.add(new DeletionRule(1, 3, 3));
        String result = new DeletionRuleExecution().deleteCharsFromLine(line, deletionRules, null);
        assertEquals("ab", result);
    }

    @Test
    void shouldReturnLineAsIsWhenSingleCharRuleIsOutOfBoundsAndRightAfterLineEnd() {
        String line = "abc";
        List<DeletionRule> deletionRules = new ArrayList<>();
        deletionRules.add(new DeletionRule(1, 4, 4));
        String result = new DeletionRuleExecution().deleteCharsFromLine(line, deletionRules, null);
        assertEquals(line, result);
    }

    @Test
    void shouldReturnLineAsIsWhenSingleCharRuleIsOutOfBoundsButNotRightAfterLineEnd() {
        String line = "abc";
        List<DeletionRule> deletionRules = new ArrayList<>();
        deletionRules.add(new DeletionRule(1, 5, 5));
        String result = new DeletionRuleExecution().deleteCharsFromLine(line, deletionRules, null);
        assertEquals(line, result);
    }

    @Test
    void shouldDeleteMultipleCharsFromStart() {
        String line = "abcd";
        List<DeletionRule> deletionRules = new ArrayList<>();
        deletionRules.add(new DeletionRule(1, 1, 2));
        String result = new DeletionRuleExecution().deleteCharsFromLine(line, deletionRules, null);
        assertEquals("cd", result);
    }

    @Test
    void shouldDeleteMultipleCharsFromMiddle() {
        String line = "abcd";
        List<DeletionRule> deletionRules = new ArrayList<>();
        deletionRules.add(new DeletionRule(1, 2, 3));
        String result = new DeletionRuleExecution().deleteCharsFromLine(line, deletionRules, null);
        assertEquals("ad", result);
    }

    @Test
    void shouldDeleteMultipleCharsFromEndWhenRuleEndsAtLineEnd() {
        String line = "abcd";
        List<DeletionRule> deletionRules = new ArrayList<>();
        deletionRules.add(new DeletionRule(1, 3, 4));
        String result = new DeletionRuleExecution().deleteCharsFromLine(line, deletionRules, null);
        assertEquals("ab", result);
    }

    @Test
    void shouldDeleteMultipleCharsFromEndWhenRuleEndsAfterLineEnd() {
        String line = "abcd";
        List<DeletionRule> deletionRules = new ArrayList<>();
        deletionRules.add(new DeletionRule(1, 3, 6));
        String result = new DeletionRuleExecution().deleteCharsFromLine(line, deletionRules, null);
        assertEquals("ab", result);
    }

    @Test
    void shouldReturnLineAsIsWhenMultipleCharRuleIsOutOfBoundsAndRightAfterLineEnd() {
        String line = "abcd";
        List<DeletionRule> deletionRules = new ArrayList<>();
        deletionRules.add(new DeletionRule(1, 5, 6));
        String result = new DeletionRuleExecution().deleteCharsFromLine(line, deletionRules, null);
        assertEquals(line, result);
    }

    @Test
    void shouldReturnLineAsIsWhenMultipleCharRuleIsOutOfBoundsButNotRightAfterLineEnd() {
        String line = "abcd";
        List<DeletionRule> deletionRules = new ArrayList<>();
        deletionRules.add(new DeletionRule(1, 6, 7));
        String result = new DeletionRuleExecution().deleteCharsFromLine(line, deletionRules, null);
        assertEquals(line, result);
    }

    @Test
    void shouldDeleteMultipleCharsWhenRuleDoesNotHaveEndColumn() {
        String line = "abcd";
        List<DeletionRule> deletionRules = new ArrayList<>();
        deletionRules.add(new DeletionRule(1, 3, null));
        String result = new DeletionRuleExecution().deleteCharsFromLine(line, deletionRules, null);
        assertEquals("ab", result);
    }

    @Test
    void shouldCombineSingleCharRuleWithSameSingleCharRule() {
        String line = "abcd";
        List<DeletionRule> deletionRules = new ArrayList<>();
        deletionRules.add(new DeletionRule(1, 2, 2));
        deletionRules.add(new DeletionRule(1, 2, 2));
        String result = new DeletionRuleExecution().deleteCharsFromLine(line, deletionRules, null);
        assertEquals("acd", result);
    }

    @Test
    void shouldCombineSingleCharRuleWithDifferentSingleCharRule() {
        String line = "abcd";
        List<DeletionRule> deletionRules = new ArrayList<>();
        deletionRules.add(new DeletionRule(1, 2, 2));
        deletionRules.add(new DeletionRule(1, 3, 3));
        String result = new DeletionRuleExecution().deleteCharsFromLine(line, deletionRules, null);
        assertEquals("ad", result);
    }

    @Test
    void shouldCombineSingleCharRuleWithMultipleCharRule() {
        String line = "abcd";
        List<DeletionRule> deletionRules = new ArrayList<>();
        deletionRules.add(new DeletionRule(1, 2, 2));
        deletionRules.add(new DeletionRule(1, 3, 4));
        String result = new DeletionRuleExecution().deleteCharsFromLine(line, deletionRules, null);
        assertEquals("a", result);
    }

    @Test
    void shouldCombineMultipleCharRuleWithMultipleCharRule() {
        String line = "abcd";
        List<DeletionRule> deletionRules = new ArrayList<>();
        deletionRules.add(new DeletionRule(1, 1, 2));
        deletionRules.add(new DeletionRule(1, 3, 4));
        String result = new DeletionRuleExecution().deleteCharsFromLine(line, deletionRules, null);
        assertEquals("", result);
    }

    @Test
    void shouldCombineMultipleCharRuleWithOverlappingMultipleCharRule() {
        String line = "abcd";
        List<DeletionRule> deletionRules = new ArrayList<>();
        deletionRules.add(new DeletionRule(1, 1, 3));
        deletionRules.add(new DeletionRule(1, 2, 4));
        String result = new DeletionRuleExecution().deleteCharsFromLine(line, deletionRules, null);
        assertEquals("", result);
    }

    @Test
    void shouldDeleteCharsEvenIfRulesAreNotSorted() {
        String line = "abcdefg";
        List<DeletionRule> deletionRules = new ArrayList<>();
        deletionRules.add(new DeletionRule(1, 3, 5));
        deletionRules.add(new DeletionRule(1, 5, 6));
        deletionRules.add(new DeletionRule(1, 2, 4));
        String result = new DeletionRuleExecution().deleteCharsFromLine(line, deletionRules, null);
        assertEquals("ag", result);
    }

    @Test
    void shouldApplyBehaviorAfterDeletionRulesExecutedForEachLine() {
        String line = " ab ";
        List<DeletionRule> deletionRules = new ArrayList<>();
        deletionRules.add(new DeletionRule(1, 2, 3));
        BehaviorAfterDeletionRulesExecutedForEachLine behavior = new BehaviorAfterDeletionRulesExecutedForEachLine();
        behavior.setDeleteLineIfBlank(true);
        String result = new DeletionRuleExecution().deleteCharsFromLine(line, deletionRules, behavior);
        assertNull(result);
    }

    @Test
    void shouldDeleteCharsFromFile() {
        Path testFilePath = createCopyOfBaseTestFileInTempDirectory();
        String deletionRulesStr = "2,3,4:1-,5:1-,7:2-2,7:5-10,7:9-15,8:3-3,8:6-11,8:10-16,9:4-4,9:7-12,9:11-17,10:5-5,10:8-13,10:12-18";
        new DeletionRuleExecution().deleteCharsFromFile(testFilePath.toString(), charset.name(), deletionRulesStr, null);
        Path expectedFilePath = resourcesDirPath.resolve("base-test-file-after-deletion.txt");
        assertTrue(Utils.areFileContentsIdentical(testFilePath, expectedFilePath, charset));
    }
}
