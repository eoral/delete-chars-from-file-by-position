Let's assume that we have a file, and we want to delete some chars at specific positions. To specify positions, we need to define one or more deletion rules. A deletion rule is basically a line number and a char range. This is the structure of a deletion rule: 
```
lineNumber:startColumnNumber-endColumnNumber
```

- **lineNumber:** It is the number of the line that contains the chars we want to delete. It starts from 1. In other words, it is the line index (but 1-based).
- **startColumnNumber:** It is the column number of the first char we want to delete. In other words, it is the lower bound of the char range, and it is inclusive.
- **endColumnNumber:** It is the column number of the last char we want to delete. In other words, it is the upper bound of the char range, and it is inclusive.

You can omit some parts of a deletion rule based on your needs. For example:
- When you want to delete all chars starting from a position till the end of line (excluding line break), you can omit **endColumnNumber**: `lineNumber:startColumnNumber-`
- When you want to delete an entire line (including line break), you can omit **startColumnNumber** and **endColumnNumber**: `lineNumber`

Here are some deletion rule examples:
- Delete 2nd, 3rd, 4th and 5th chars from 7th line: `7:2-5`
- Delete 2nd char from 7th line: `7:2-2`
- Delete all chars from 7th line starting from 2nd char till the end of line (eventually, 7th line will have only one char): `7:2-`
- Delete all chars from 7th line (eventually, 7th line will be empty): `7:1-`
- Delete 7th line (will delete all chars and the new line char too): `7`

When you need multiple deletion rules, you can combine them using a comma. Order of the rules doesn't matter. For example:
- Delete 2nd and 5th chars from 7th line: `7:2-2,7:5-5` or `7:5-5,7:2-2`

**How to run this Maven project:**
- Clone this repository to your machine. Let's assume you cloned it into directory `/usr`.
- Open a terminal window and go to `/usr/delete-chars-from-file-by-position`. Run `mvn clean package`.
- In the terminal window, go to `/usr/delete-chars-from-file-by-position/target`. Run `java -classpath delete-chars-from-file-by-position-0.1.jar com.eoral.deletecharsfromfilebyposition.DeletionRuleExecution --file /path/to/file --charset UTF-8 --deletion-rules 4:2-5,12:10-14`. 

There is an additional option named `delete-line-if-blank` while running the app. Its default value is false, and it is not mandatory. You can set it to true like below:
```
java -classpath delete-chars-from-file-by-position-0.1.jar com.eoral.deletecharsfromfilebyposition.DeletionRuleExecution ... --delete-line-if-blank
```

But, what does `delete-line-if-blank` option do? Let's explain it with an example:
- Assume that we have a file, and we want to delete 2nd, 3rd and 4th chars from every line.
  - First line is `xabcx`.
  - Second line is `<space>abc<space>`. Beware that there is one leading and one trailing space.
  - Third line is `yabcy`.
- We need a deletion rule like this: `1:2-4,2:2-4,3:2-4`
- When `delete-line-if-blank` is false:
  - After deletion, first line will be `xx`.
  - After deletion, second line will be `<space><space>`.
  - After deletion, third line will be `yy`.
- When `delete-line-if-blank` is true:
  - First line will be `xx`.
  - Second line will be deleted.
  - Third line will be `yy`. But, it will be the new second line in the file.
- As you can guess, when `delete-line-if-blank` is true, the behavior is as follows:
  - Deletion rules are executed on matching lines and chars are deleted.
  - If a line is blank (contains only whitespaces) after deleting chars, line will be deleted.
