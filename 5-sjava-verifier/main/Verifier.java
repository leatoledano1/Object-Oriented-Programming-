package ex5.main;

import java.io.*;
import java.util.regex.*;

/**the main verification class of the s-Java compiler
 * performs a two-pass scan over the input file:
 *     First pass: collects all declared method names
 *     Second pass: validates syntax, scopes, and semantics
 */
public class Verifier {

    /**regular expression representing a legal variable or method name
     * according to s-Java rules
     */
    private static final String VAR_NAME =
            "(?:[A-Za-z][A-Za-z0-9_]*|_[A-Za-z0-9][A-Za-z0-9_]*)";

    /**
     * Pattern that matches a valid void method declaration line
     */
    private static final Pattern METHOD_HEADER =
            Pattern.compile("^\\s*void\\s+(" + VAR_NAME + ")\\s*\\(([^)]*)\\)\\s*\\{\\s*$");

    /**pattern that matches a method call statement.
     */
    private static final Pattern METHOD_CALL =
            Pattern.compile("^\\s*(" + VAR_NAME + ")\\s*\\(.*\\)\\s*;\\s*$");

    /**pattern that matches if / while opening statements.
     */
    private static final Pattern IF_WHILE_HEADER =
            Pattern.compile("^\\s*(if|while)\\s*\\((.*)\\)\\s*\\{\\s*$");

    /**pattern that matches variable declaration statements,
     * including optional final modifier and initialization.
     */
    private static final Pattern VAR_DECLARATION =
            Pattern.compile("^\\s*(final\\s+)?(int|double|boolean|char|String)\\s+" +
                    "(" + VAR_NAME + ")\\s*(=\\s*[^;]+)?\\s*" +
                    "(,\\s*(" + VAR_NAME + ")\\s*(=\\s*[^;]+)?\\s*)*" +
                    ";\\s*$");

    /**
     * pattern that matches assignment statements.
     */
    private static final Pattern ASSIGNMENT =
            Pattern.compile("^\\s*" + VAR_NAME + "\\s*=\\s*[^;]+\\s*;\\s*$");

    /**table that stores all declared method names.
     */
    private final MethodTable methodTable = new MethodTable();

    private static final int GLOBAL_DEPTH = 0;
    private static final int METHOD_NAME_GROUP = 1;
    private static final int METHOD_PARAMS_GROUP = 2;
    private static final int NO_MATCH = 0;
    private static final int SEMICOLON_LENGTH = 1;
    private static final int DECLARATION_SPLIT_LIMIT = 2;
    private static final int PARAM_PARTS_COUNT = 2;
    private static final int FINAL_KEYWORD_LENGTH = 6;
    private static final int INDEX_FIRST = 0;
    private static final int INDEX_SECOND = 1;

    private static final String COMMENT_PREFIX = "//";
    private static final String MULTI_LINE_COMMENT_START = "/*";
    private static final String MULTI_LINE_COMMENT_END = "*/";

    private static final String SUFFIX_SEMICOLON = ";";
    private static final String SUFFIX_OPEN_BRACE = "{";
    private static final String SUFFIX_CLOSE_BRACE = "}";

    private static final String RETURN_STATEMENT = "return;";
    private static final String FINAL_KEYWORD_PREFIX = "final ";

    private static final String PARAM_SEPARATOR = ",";
    private static final String SPLIT_WHITESPACE_REGEX = "\\s+";
    private static final String SPLIT_WHITESPACE_LIMIT_REGEX = "\\s+";
    private static final String ASSIGNMENT_OPERATOR = "=";

    /**verifies an s-Java source file.
     * @param file the file to verify
     * @return an ErrorType describing the verification result
     */
    public ErrorType verify(File file) {
        ErrorType firstPass = collectMethods(file);
        if (firstPass != null) return firstPass;
        ErrorType secondPass = validateFile(file);
        if (secondPass != null) return secondPass;
        return ErrorType.NONE;
    }

    //first pass

    /**first verification pass
     * Collects all declared method names in the file
     * and detects duplicate method declarations
     * @param file the source file
     * @return an ErrorType if an error is found, null otherwise
     */
    private ErrorType collectMethods(File file) {
        try (BufferedReader r = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = r.readLine()) != null) {
                Matcher m = METHOD_HEADER.matcher(line);
                if (m.matches()) {
                    try {
                        methodTable.add(m.group(METHOD_NAME_GROUP));
                    } catch (IllegalArgumentException e) {
                        return ErrorType.DUPLICATE_METHOD;
                    }
                }
            }
        } catch (IOException e) {
            return ErrorType.IO_ERROR;
        }
        return null;
    }

    //second pass

    /**second verification pass
     * Performs full syntax, scope, and semantic validation
     * @param file the source file
     * @return an ErrorType if an error is found,  null otherwise
     */
    private ErrorType validateFile(File file) {
        try (BufferedReader r = new BufferedReader(new FileReader(file))) {
            int depth = GLOBAL_DEPTH;
            Scope global = new Scope(null);
            Scope current = null;
            String line;
            while ((line = r.readLine()) != null) {

                String t = line.trim();
                if (t.isEmpty() || t.startsWith(COMMENT_PREFIX)) continue;

                ErrorType basic = basicLineValidation(line, t);
                if (basic != null) return basic;

                if (depth == GLOBAL_DEPTH) {
                    ErrorType globalResult = handleGlobalLine(line, t, global);
                    if (globalResult != null) return globalResult;

                    Matcher m = METHOD_HEADER.matcher(line);
                    if (m.matches()) {
                        current = new Scope(global);
                        ErrorType err = validateMethodParameters(m.group(2), current);
                        if (err != null) return err;
                    }
                } else {
                    ErrorType methodResult = handleMethodLine(line, t, current);
                    if (methodResult != null) return methodResult;
                }

                depth += count(line, SUFFIX_OPEN_BRACE.charAt(NO_MATCH));
                depth -= count(line, SUFFIX_CLOSE_BRACE.charAt(NO_MATCH));

                if (depth < GLOBAL_DEPTH)
                    return ErrorType.UNMATCHED_CLOSING_BRACE;
            }

            if (depth != GLOBAL_DEPTH)
                return ErrorType.UNMATCHED_OPENING_BRACE;

        } catch (IOException e) {
            return ErrorType.IO_ERROR;
        }
        return null;
    }

    //line validation

    /**performs basic validation common to all lines
     * @param line the original line
     * @param t the trimmed line
     * @return an ErrorType if the line is illegal, null otherwise
     */
    private ErrorType basicLineValidation(String line, String t) {
        if (line.contains(MULTI_LINE_COMMENT_START) || line.contains(MULTI_LINE_COMMENT_END))
            return ErrorType.ILLEGAL_COMMENT;
        if (!(t.endsWith(SUFFIX_SEMICOLON) ||
                t.endsWith(SUFFIX_OPEN_BRACE) || t.endsWith(SUFFIX_CLOSE_BRACE)))
            return ErrorType.ILLEGAL_METHOD_STATEMENT;
        return null;
    }

    //global scope check

    /**
     * Handles a line appearing in the global scope
     * @param line the original line
     * @param t the trimmed line
     * @param global the global scope
     * @return an ErrorType if an error is found ,null elsewise
     */
    private ErrorType handleGlobalLine(String line, String t, Scope global) {
        Matcher m = METHOD_HEADER.matcher(line);
        if (m.matches()) {
            return null;
        }
        if (VAR_DECLARATION.matcher(line).matches()) {
            return handleVarDeclaration(t, global)
                    ? null : ErrorType.ILLEGAL_VAR_DECLARATION;
        }
        if (!t.equals(SUFFIX_CLOSE_BRACE))
            return ErrorType.ILLEGAL_GLOBAL_STATEMENT;

        return null;
    }

    /**validates method parameters and declares them in a new scope
     * @param params the parameter string
     * @param current the current scope
     * @return an ErrorType if an error is find, null otherwise
     */
    private ErrorType validateMethodParameters(String params, Scope current) {

        if (params.isEmpty()) return null;

        for (String p : params.split(",")) {
            String[] parts = p.trim().split("\\s+");
            if (parts.length != 2)
                return ErrorType.ILLEGAL_METHOD_PARAMETER;

            if (!current.declare(parts[1],
                    new VarInfo(parts[0], false, true)))
                return ErrorType.DUPLICATE_PARAMETER;
        }
        return null;
    }


    //method body check

    /**
     * handle a line inside a method body
     * @param line the original line
     * @param t the trimmed line
     * @param current the current scope
     * @return an ErrorType if an error is found, null otherwise */
    private ErrorType handleMethodLine(String line, String t, Scope current) {
        Matcher cond = IF_WHILE_HEADER.matcher(line);
        if (cond.matches()) {
            return ConditionValidator.isValid(cond.group(METHOD_PARAMS_GROUP), current)
                    ? null : ErrorType.ILLEGAL_CONDITION;
        }
        if (VAR_DECLARATION.matcher(line).matches()) {
            return handleVarDeclaration(t, current)
                    ? null : ErrorType.ILLEGAL_VAR_DECLARATION;
        }
        if (ASSIGNMENT.matcher(line).matches()) {
            return validateAssignment(t, current);
        }
        if (METHOD_CALL.matcher(line).matches()) {
            Matcher call = METHOD_CALL.matcher(line);
            call.matches();
            return methodTable.exists(call.group(METHOD_NAME_GROUP))
                    ? null : ErrorType.UNDEFINED_METHOD;
        }
        if (!t.equals(RETURN_STATEMENT) && !t.equals(SUFFIX_CLOSE_BRACE))
            return ErrorType.ILLEGAL_METHOD_STATEMENT;
        return null;
    }

    /**
     * Validate an assignment statement
     * @param t the trimmed assignment line
     * @param current the current scope
     * @return an ErrorType if an error is found, null otherwise
     */
    private ErrorType validateAssignment(String t, Scope current) {
        String[] p = t.substring(NO_MATCH, t.length() - SEMICOLON_LENGTH).split(ASSIGNMENT_OPERATOR);
        String name = p[INDEX_FIRST].trim();
        VarInfo v = current.lookup(name);
        if (v == null || v.isFinal())
            return ErrorType.ILLEGAL_ASSIGNMENT;
        if (!AssignmentValidator.isValid(v.getType(), p[INDEX_SECOND].trim(), current))
            return ErrorType.INCOMPATIBLE_ASSIGNMENT;

        current.assign(name);
        return null;
    }

    //helpers

    /**handles variable declaration statements inside a given scope
     * @param line  the declaration line
     * @param scope the scope to declare variables in
     * @return true if the declaration is legal, false otherwise
     */
    private boolean handleVarDeclaration(String line, Scope scope) {
        boolean isFinal = line.startsWith(FINAL_KEYWORD_PREFIX);
        String content = isFinal ? line.substring(FINAL_KEYWORD_LENGTH).trim() : line;
        content = content.substring(NO_MATCH, content.length() - SEMICOLON_LENGTH);
        String[] parts = content.split(SPLIT_WHITESPACE_LIMIT_REGEX, DECLARATION_SPLIT_LIMIT);
        String type = parts[INDEX_FIRST];
        for (String d : parts[INDEX_SECOND].split(PARAM_SEPARATOR)) {
            d = d.trim();
            boolean init = d.contains(ASSIGNMENT_OPERATOR);
            String name = init ? d.substring(NO_MATCH, d.indexOf(ASSIGNMENT_OPERATOR)).trim() : d;
            String value = init ?
                    d.substring(d.indexOf(ASSIGNMENT_OPERATOR) + SEMICOLON_LENGTH).trim() : null;

            if (scope.lookup(name) != null) return false;
            if (isFinal && !init) return false;

            if (init && !AssignmentValidator.isValid(type, value, scope))
                return false;

            scope.declare(name, new VarInfo(type, isFinal, init));
        }
        return true;
    }

    /**counts occurrences of a specific character in a string.
     * @param s the string to scan
     * @param c the character to count
     * @return number of occurrences of c in s
     */
    private int count(String s, char c) {
        int k = NO_MATCH;
        for (char x : s.toCharArray()) if (x == c) k++;
        return k;
    }
}