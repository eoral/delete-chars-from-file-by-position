package com.eoral.deletecharsfromfilebyposition;

import org.apache.commons.cli.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.*;

public class DeletionRuleExecution {

    public static void main(String[] args) throws ParseException {

        String optionFile = "o1";
        String optionCharset = "o2";
        String optionDeletionRules = "o3";
        String optionDeleteLineIfBlank = "o4";

        Options options = new Options();
        options.addOption(optionFile, "file", true, "Absolute path of the file");
        options.addOption(optionCharset, "charset", true, "Charset of the file");
        options.addOption(optionDeletionRules, "deletion-rules", true,
                "Single rule: lineNumber[:startColumnNumber-[endColumnNumber]] \nIf you need multiple rules, use comma to join the rules.");
        options.addOption(optionDeleteLineIfBlank, "delete-line-if-blank", false,
                "After deletion rules are executed on a line, delete line if it is blank (contains only whitespace).");

        boolean hasMissingOrInvalidOptions = false;
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            hasMissingOrInvalidOptions = true;
        }

        if (hasMissingOrInvalidOptions) {
            printHelp(options);
        } else {
            String inputFilePathStr = cmd.getOptionValue(optionFile);
            if (inputFilePathStr == null || inputFilePathStr.trim().length() == 0) {
                hasMissingOrInvalidOptions = true;
            }
            String inputFileCharset = cmd.getOptionValue(optionCharset);
            if (inputFileCharset == null || inputFileCharset.trim().length() == 0) {
                hasMissingOrInvalidOptions = true;
            }
            String deletionRulesStr = cmd.getOptionValue(optionDeletionRules);
            if (deletionRulesStr == null || deletionRulesStr.trim().length() == 0) {
                hasMissingOrInvalidOptions = true;
            }
            boolean deleteLineIfBlank = false;
            if (cmd.hasOption(optionDeleteLineIfBlank)) {
                deleteLineIfBlank = true;
            }
            if (hasMissingOrInvalidOptions) {
                printHelp(options);
            } else {
                BehaviorAfterDeletionRulesExecutedForEachLine behavior = new BehaviorAfterDeletionRulesExecutedForEachLine();
                behavior.setDeleteLineIfBlank(deleteLineIfBlank);
                new DeletionRuleExecution().deleteCharsFromFile(inputFilePathStr, inputFileCharset, deletionRulesStr, behavior);
            }
        }
    }

    public static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(100);
        formatter.printHelp("delete-chars-from-file-by-position", options);
    }

    public void deleteCharsFromFile(
            String inputFilePathStr,
            String inputFileCharset,
            String deletionRulesStr,
            BehaviorAfterDeletionRulesExecutedForEachLine behaviorAfterDeletionRulesExecutedForEachLine) {
        deleteCharsFromFile(
                Path.of(inputFilePathStr),
                Charset.forName(inputFileCharset),
                DeletionRuleParser.parseMultiple(deletionRulesStr),
                behaviorAfterDeletionRulesExecutedForEachLine);
    }

    public void deleteCharsFromFile(
            Path inputFilePath,
            Charset inputFileCharset,
            List<DeletionRule> deletionRules,
            BehaviorAfterDeletionRulesExecutedForEachLine behaviorAfterDeletionRulesExecutedForEachLine) {

        DeletionRulesGroupedByLine deletionRulesGroupedByLine = new DeletionRulesGroupedByLine(deletionRules);
        Path tempFilePath = Utils.createTempFile();

        try (FileInputStream fis = new FileInputStream(inputFilePath.toFile());
             InputStreamReader isr = new InputStreamReader(fis, inputFileCharset);
             BufferedReader reader = new BufferedReader(isr);
             FileOutputStream fos = new FileOutputStream(tempFilePath.toFile());
             OutputStreamWriter osw = new OutputStreamWriter(fos, inputFileCharset);
             BufferedWriter writer = new BufferedWriter(osw)) {

            int lineNumber = 0;
            String line;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                List<DeletionRule> deletionRulesOfLine = deletionRulesGroupedByLine.getByLine(lineNumber);
                String restOfTheLine = deleteCharsFromLine(line, deletionRulesOfLine, behaviorAfterDeletionRulesExecutedForEachLine);
                if (restOfTheLine != null) {
                    writer.write(restOfTheLine);
                    writer.newLine();
                }
            }

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        Utils.moveFileReplaceExisting(tempFilePath, inputFilePath);
    }

    public String deleteCharsFromLine(
            String line,
            List<DeletionRule> deletionRules,
            BehaviorAfterDeletionRulesExecutedForEachLine behaviorAfterDeletionRulesExecutedForEachLine) {
        if (deletionRules.isEmpty()) {
            return line; // return line as is
        } else {
            String lineAfterRulesApplied;
            if (containsOneRuleThatDeletesLine(deletionRules)) {
                lineAfterRulesApplied = null; // line will be deleted
            } else if (containsOneRuleThatEmptiesLine(deletionRules)) {
                lineAfterRulesApplied = ""; // line will be emptied
            } else {
                Set<Integer> columns = Utils.getColumns(1, line.length());
                if (!columns.isEmpty()) {
                    for (DeletionRule deletionRule : deletionRules) {
                        Set<Integer> columnsToBeRemoved = deletionRule.getEffectedColumns(line.length());
                        columns.removeAll(columnsToBeRemoved);
                        if (columns.isEmpty()) {
                            break;
                        }
                    }
                }
                lineAfterRulesApplied = getRemainingChars(line, columns); // return rest of the line
            }
            return applyBehaviorAfterDeletionRulesExecutedForEachLine(
                    lineAfterRulesApplied, behaviorAfterDeletionRulesExecutedForEachLine);
        }
    }

    private boolean containsOneRuleThatDeletesLine(List<DeletionRule> deletionRules) {
        for (DeletionRule deletionRule : deletionRules) {
            if (deletionRule.deletesLine()) {
                return true;
            }
        }
        return false;
    }

    private boolean containsOneRuleThatEmptiesLine(List<DeletionRule> deletionRules) {
        for (DeletionRule deletionRule : deletionRules) {
            if (deletionRule.emptiesLine()) {
                return true;
            }
        }
        return false;
    }

    private String getRemainingChars(String line, Set<Integer> remainingColumns) {
        if (remainingColumns.isEmpty()) {
            return "";
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            SortedSet<Integer> remainingColumnsSorted = new TreeSet<>(remainingColumns);
            for (Integer column : remainingColumnsSorted) {
                int beginIndex = column - 1;
                int endIndex = beginIndex + 1;
                stringBuilder.append(line, beginIndex, endIndex);
            }
            return stringBuilder.toString();
        }
    }

    private String applyBehaviorAfterDeletionRulesExecutedForEachLine(
            String lineAfterRulesApplied, BehaviorAfterDeletionRulesExecutedForEachLine behavior) {
        String result = lineAfterRulesApplied;
        if (lineAfterRulesApplied != null && behavior != null) {
            if (behavior.isDeleteLineIfBlank() && lineAfterRulesApplied.trim().length() == 0) {
                result = null;
            }
        }
        return result;
    }
}
